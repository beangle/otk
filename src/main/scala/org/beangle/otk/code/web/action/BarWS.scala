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

package org.beangle.otk.code.web.action

import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.{BarcodeFormat, EncodeHintType, MultiFormatWriter}
import jakarta.servlet.http.HttpServletResponse
import org.beangle.commons.activation.MediaTypes
import org.beangle.commons.lang.Charsets
import org.beangle.otk.code.web.helper.RangeChecker
import org.beangle.webmvc.annotation.*
import org.beangle.webmvc.context.ActionContext
import org.beangle.webmvc.support.ActionSupport
import org.beangle.webmvc.view.{Stream, View}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.net.URLDecoder
import java.util as ju
import javax.imageio.ImageIO

class BarWS extends ActionSupport {
  val hints = new ju.HashMap[EncodeHintType, Any]
  hints.put(EncodeHintType.CHARACTER_SET, "utf-8")
  hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
  hints.put(EncodeHintType.MARGIN, 2)

  @mapping("{content}")
  def index(@param("content") content: String): View = {
    val decoded = URLDecoder.decode(content, Charsets.UTF_8)
    val isLatin = decoded.forall(c => c <= 127)
    if (isLatin) {
      val width = RangeChecker.check(getInt("width"), 100, 300, 120)
      val height = RangeChecker.check(getInt("height"), 10, 100, 30)
      val bitMatrix = new MultiFormatWriter().encode(decoded, BarcodeFormat.CODE_128, width, height, hints)
      val image = MatrixToImageWriter.toBufferedImage(bitMatrix)
      val os = new ByteArrayOutputStream()
      ImageIO.write(image, "PNG", os)
      Stream(new ByteArrayInputStream(os.toByteArray),  MediaTypes.png, "barcode.png")
    } else {
      val res = ActionContext.current.response
      res.setContentType("text/html;charset=utf-8")
      res.getWriter.write("只能编码拉丁字符(<=127)")
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST)
      null
    }
  }
}
