/*
 * Copyright (C) 2005, The Beangle Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.beangle.otk.aot

import org.beangle.commons.aot.AotHintRegistrar

import java.io.File
import java.util.jar.JarFile
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/** beangle-otk-ws 的 GraalVM native-image 提示。
  *
  * otk 是终端应用：commons/webmvc/template/serializer/jdbc/cache/cdi/data 等库自带
  * registrar，由 AotPlugin 自动汇总；本类只补 otk 自身与 otk 特有第三方库的部分。
  *
  *  - 资源：验证码词库 `todd.properties`（`DictionaryReader.bundle("todd")`）、行政区划表
  *    （`IdHelper`）、拼音数据表（pinyin4j 从 jar 内 `/pinyindb` 直读）、LanguageTool 的
  *    规则 XML 与词库；
  *  - 反射：LanguageTool 的语言类与规则类。它们不在源码里静态引用，而是运行期按
  *    `META-INF/services/org.languagetool.Language`、grammar.xml 的 `class` 属性经
  *    `ClassBroker.forName`/`Class.forName` 加载，封闭世界分析下必须显式登记，
  *    否则 `/lang/en` 首个请求即抛 ClassNotFoundException。
  */
class OtkAotHints extends AotHintRegistrar {

  override def registering(): Unit = {
    hints.registerPattern(
      "todd.properties",
      "org/beangle/otk/sns/divisions.properties",
      "pinyindb/*.xml",
      "pinyindb/*.txt",
      "org/languagetool/**",
      "net/loomchild/segment/res/**")

    registerLanguageTool()
  }

  /** 登记 LanguageTool 的语言类与规则类（按类名，类不在编译期依赖里）。 */
  private def registerLanguageTool(): Unit = {
    val loader = getClass.getClassLoader
    val names = mutable.LinkedHashSet.empty[String]
    names ++= serviceClasses(loader)
    names ++= languageClasses()
    names ++= ruleClasses()

    names.foreach { name =>
      try {
        val clazz = Class.forName(name, false, loader)
        hints.registerType(clazz)
      } catch {
        case _: ClassNotFoundException | _: LinkageError => ()
      }
    }
  }

  /** `META-INF/services/org.languagetool.Language` 中声明的语言类。 */
  private def serviceClasses(loader: ClassLoader): Seq[String] = {
    val buf = mutable.ListBuffer.empty[String]
    classpathEntries.foreach { entry =>
      readEntry(entry, "META-INF/services/org.languagetool.Language") foreach { text =>
        text.linesIterator.map(_.trim).filter(l => l.nonEmpty && !l.startsWith("#")).foreach(buf += _)
      }
    }
    val url = loader.getResourceAsStream("META-INF/services/org.languagetool.Language")
    if (url != null) {
      try {
        val text = new String(url.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)
        text.linesIterator.map(_.trim).filter(l => l.nonEmpty && !l.startsWith("#")).foreach(buf += _)
      } finally url.close()
    }
    buf.toSeq
  }

  /** `org/languagetool/language/` 下的语言类。 */
  private def languageClasses(): Seq[String] =
    classNames("org/languagetool/language/").filter(n => !n.contains("$"))

  /** grammar.xml 里 `class="..."` 声明的规则类（运行期按名实例化）。 */
  private def ruleClasses(): Seq[String] = {
    val names = mutable.LinkedHashSet.empty[String]
    val ruleXml = mutable.ListBuffer.empty[String]
    classpathEntries.foreach { entry =>
      entryNames(entry).foreach { name =>
        if name.startsWith("org/languagetool/") && name.endsWith(".xml") then ruleXml += name
      }
    }
    ruleXml.distinct.foreach { name =>
      classpathEntries.foreach { entry =>
        readEntry(entry, name).foreach { text =>
          val matcher = ruleClassPattern.matcher(text)
          while matcher.find() do names += matcher.group(1)
        }
      }
    }
    names.toSeq
  }

  private def ruleClassPattern = java.util.regex.Pattern.compile("class=\"([A-Za-z_][\\w.$]*)\"")

  /** classpath 中符合前缀的类名（点号全限定名）。 */
  private def classNames(prefix: String): Seq[String] =
    classpathEntries.flatMap(entryNames)
      .filter(n => n.startsWith(prefix) && n.endsWith(".class"))
      .map(n => n.dropRight(6).replace('/', '.'))
      .distinct

  private def classpathEntries: Seq[File] = {
    val path = System.getProperty("java.class.path")
    if null == path then Nil
    else path.split(File.pathSeparator).toSeq.map(new File(_)).filter(_.exists())
  }

  /** 目录或 jar 中的条目名（统一用正斜杠路径）。 */
  private def entryNames(entry: File): Seq[String] = {
    if entry.isDirectory then
      val root = entry.toPath
      try {
        val stream = java.nio.file.Files.walk(root)
        try
          stream.iterator().asScala
            .filter(p => java.nio.file.Files.isRegularFile(p))
            .map(p => root.relativize(p).toString.replace(File.separatorChar, '/'))
            .toSeq
        finally stream.close()
      } catch { case _: Throwable => Nil }
    else if entry.getName.endsWith(".jar") then
      try {
        val jar = new JarFile(entry)
        try jar.entries().asScala.toList.map(_.getName)
        finally jar.close()
      } catch { case _: Throwable => Nil }
    else Nil
  }

  /** 读取目录或 jar 里某个条目的文本内容。 */
  private def readEntry(entry: File, name: String): Option[String] = {
    if entry.isDirectory then
      val f = new File(entry, name)
      if f.isFile then Some(new String(java.nio.file.Files.readAllBytes(f.toPath), java.nio.charset.StandardCharsets.UTF_8))
      else None
    else if entry.getName.endsWith(".jar") then
      try {
        val jar = new JarFile(entry)
        try {
          val e = jar.getEntry(name)
          if null == e then None
          else {
            val in = jar.getInputStream(e)
            try Some(new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8))
            finally in.close()
          }
        } finally jar.close()
      } catch { case _: Throwable => None }
    else None
  }
}
