import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*

organization := "org.beangle.otk"
version := "0.0.31-SNAPSHOT"

scmInfo := Some(
  ScmInfo(
    uri("https://github.com/beangle/otk"),
    "scm:git@github.com:beangle/otk.git"
  )
)

developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = uri("http://github.com/duantihua")
  )
)

description := "Beangle online toolkit"
homepage := Some(uri("http://beangle.github.io/otk/index.html"))
resolvers += Resolver.mavenLocal

val beangle_commons = "org.beangle.commons" % "beangle-commons" % "6.3.7"
val beangle_she = "org.beangle.she" % "beangle-she" % "0.0.19"
val beangle_cache = "org.beangle.cache" % "beangle-cache" % "0.1.24"
val beangle_doc_pdf = "org.beangle.doc" % "beangle-doc-pdf" % "0.5.15"
val beangle_doc_excel = "org.beangle.doc" % "beangle-doc-excel" % "0.5.15"
val beangle_doc_docx = "org.beangle.doc" % "beangle-doc-docx" % "0.5.15"

val pinyin4j = "com.belerweb" % "pinyin4j" % "2.5.1"
val zxing = "com.google.zxing" % "javase" % "3.5.4"
val language_en = "org.languagetool" % "language-en" % "6.7"
val guava = "com.google.guava" % "guava" % "33.2.1-jre"

// 以下两个依赖仅供 nativeImage 使用：镜像入口是 sas 的嵌入式 Tomcat Bootstrap，
// 用 provided 让它们进入 Compile / fullClasspath，同时不打进 WAR、不随 POM 传递。
val sas_engine = "org.beangle.sas" % "beangle-sas-engine" % "0.13.13"
val tomcat_embed_core = ("org.apache.tomcat.embed" % "tomcat-embed-core" % "11.0.26")
  .exclude("org.apache.tomcat", "tomcat-annotations-api")

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin, NativeImagePlugin)
  .enablePlugins(AotPlugin, MetaPlugin, ProxyPlugin)
  .settings(
    name := "beangle-otk-ws",
    common,
    libraryDependencies ++= Seq(beangle_commons, beangle_she, typesafe_config),
    libraryDependencies ++= Seq(beangle_doc_pdf, beangle_doc_excel, beangle_doc_docx, jodconverter_local, libreoffice),
    libraryDependencies ++= Seq(pinyin4j, zxing),
    libraryDependencies ++= Seq(beangle_cache, caffeine, caffeine_jcache, jedis),
    libraryDependencies ++= Seq(language_en, guava),
    libraryDependencies ++= Seq(scalatest),
    // native-image 配置。注意 AWT/字体相关的运行时准备放在代码里
    // （org.beangle.otk.config.AwtInitializer，见 docs/native.md 4.3）：
    // 原生镜像的 java.home 为空，只有运行期才能设置，写成本地 -D 是构建期属性，不进镜像。
    Compile / mainClass := Some("org.beangle.sas.engine.tomcat.Bootstrap"),
    nativeImageOutput := xsbti.VirtualFileRef.of((NativeImage / target).value.getAbsolutePath + "/otk-ws"),
    nativeImageOptions ++= Seq(
      "--sun-misc-unsafe-memory-access=allow",
      "-Os",
      "-H:+AddAllCharsets",
      "-H:+UnlockExperimentalVMOptions",
      "-H:IncludeResourceBundles=org.apache.xmlbeans.impl.regex.message",
      "-H:+ReportExceptionStackTraces",
      "--initialize-at-build-time=ch.qos.logback,org.slf4j,org.xml.sax,org.w3c.dom,javax.xml"
    ),
    snapshotRepoUrl := "https://sas.openurp.net/sas/repo/snapshot/upload/{fileName}",
    nativePublishUrl := "https://sas.openurp.net/sas/repo/native/upload/{path}/{fileName}",
    libraryDependencies ++= Seq(sas_engine % "provided", tomcat_embed_core % "provided")
  )
