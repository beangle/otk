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

package org.beangle.otk.config

import jakarta.servlet.ServletContext
import org.beangle.web.servlet.init.Initializer

import java.nio.file.{Files, Paths}

/** 在 CDI 装载 Bean 之前准备好进程级的 AWT 设置。
  *
  * 普通 JVM/WAR 部署用不到它：JVM 一定有 java.home，是否 headless 由 DISPLAY 决定。
  * GraalVM 原生镜像不同——镜像里没有 java.home，会直接踩到两处 JDK 的字体逻辑：
  *
  *  - `sun.awt.FontConfiguration.findFontConfigFile` 在 java.home 为空时抛
  *    `java.lang.Error: java.home property not set`；
  *  - 即便 java.home 不为空，`X11FontManager.createFontConfiguration` 也只在
  *    `<java.home>/lib` 存在（即 foundOsSpecificFile() 为 false）时，才会退而使用
  *    `FcFontConfiguration` 找 fontconfig 合成字体配置；否则回落到 MFontConfiguration
  *    并抛 `RuntimeException: Fontconfig head is null, check your fonts or fonts configuration`。
  *
  * 所以这里造一个带 lib 子目录的 java.home，让 JDK 走能自行合成的分支（结果缓存在
  * `~/.java/fonts/<jdk 版本>/fcinfo-*.properties`，没有缓存时由镜像里的 libfontmanager
  * 调 fontconfig 现算一次）。服务端只渲染图片和文字，不需要显示器，因此固定 headless。
  */
object AwtInitializer {

  /** GraalVM 在原生镜像内设置该属性（值为 runtime），普通 JVM 上不存在。 */
  val NativeImageKey = "org.graalvm.nativeimage.imagecode"

  def isNativeImage: Boolean = null != System.getProperty(NativeImageKey)

  def prepare(): Unit = {
    if (isNativeImage) {
      if (null == System.getProperty("java.home")) {
        val lib = Paths.get(System.getProperty("java.io.tmpdir"), "beangle-otk-home", "lib")
        Files.createDirectories(lib)
        System.setProperty("java.home", lib.getParent.toString)
      }
      // 服务端没有显示器；显式指定，避免镜像落在带 DISPLAY 的机器上走 X11
      System.setProperty("java.awt.headless", "true")
    }
  }
}

/** 见 [[AwtInitializer]]。order 取 -2，排在 she 的 ConfigInitializer(-1) 与
  * ContainerInitializer(0) 之前，即早于任何会用到 AWT 的 Bean 初始化。
  */
class AwtInitializer extends Initializer {

  override def order: Int = -2

  override def onStartup(context: ServletContext): Unit = AwtInitializer.prepare()
}
