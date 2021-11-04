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

package org.beangle.otk.captcha.core.image

import org.beangle.commons.io.IOs
import org.beangle.commons.lang.time.Stopwatch
import org.beangle.otk.captcha.core.service.{CaptchaService, CaptchaStore}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.springframework.util.StopWatch

import java.io.*
import javax.imageio.ImageIO

class ImageCaptchaTest extends AnyFunSpec with Matchers {
  describe("ImageCaptcha") {
    val captchaStore = new CaptchaStore.MemoryStore[String]
    val captchaService = CaptchaService(captchaStore, GmailEngine.image())
    val challenge = captchaService.getChallenge("123444232323123")
    val file = File.createTempFile("captcha", ".png")
    val fos = new FileOutputStream(file)
    IOs.copy(challenge, fos)
    fos.close
    println(file.getAbsolutePath)
  }
}
