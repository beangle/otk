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
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.{BarcodeFormat, DecodeHintType, EncodeHintType, MultiFormatWriter}
import org.beangle.commons.lang.Charsets
import org.beangle.commons.net.http.HttpUtils
import org.beangle.otk.code.web.helper.RangeChecker
import org.beangle.web.action.annotation.*
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.{Stream, View}

import java.awt.BasicStroke
import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.net.{URL, URLDecoder}
import java.time.Instant
import java.util as ju
import javax.imageio.ImageIO

class QrWS extends ActionSupport {
  val hints = new ju.HashMap[EncodeHintType, Any]
  hints.put(EncodeHintType.CHARACTER_SET, "utf-8")
  hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
  hints.put(EncodeHintType.MARGIN, 2)

  @mapping("{content}")
  def index(@param("content") content: String): View = {
    val size = RangeChecker.check(getInt("size"), 100, 500, 300)
    val decoded = URLDecoder.decode(content, Charsets.UTF_8)
    val bitMatrix = new MultiFormatWriter().encode(decoded, BarcodeFormat.QR_CODE, size, size, hints)
    val image = MatrixToImageWriter.toBufferedImage(bitMatrix)
    get("logo") foreach { url =>
      val rs = HttpUtils.getData(url)
      if rs.isOk then
        addLogo(image, rs.content.asInstanceOf[Array[Byte]])
    }
    val os = new ByteArrayOutputStream()
    ImageIO.write(image, "PNG", os)
    Stream(new ByteArrayInputStream(os.toByteArray), "image/png", "qrcode.png")
  }

  private def addLogo(qrImg: BufferedImage, logoBytes: Array[Byte]): Unit = {
    try {
      val logo = ImageIO.read(new ByteArrayInputStream(logoBytes))
      val logoSize = 0.15f
      val g = qrImg.createGraphics()
      val logoWidth =
        if logo.getWidth(null) > qrImg.getWidth * logoSize then
          (qrImg.getWidth * logoSize).asInstanceOf[Int]
        else logo.getWidth(null)
      val logoHeight =
        if logo.getHeight(null) > qrImg.getHeight * logoSize then
          (qrImg.getHeight * logoSize).asInstanceOf[Int]
        else logo.getHeight(null)
      val x = (qrImg.getWidth - logoWidth) / 2
      val y = (qrImg.getHeight - logoHeight) / 2
      g.drawImage(logo, x, y, logoWidth, logoHeight, null)
      g.drawRoundRect(x, y, logoWidth, logoHeight, 15, 15)
      g.setStroke(new BasicStroke(2))
      g.setColor(java.awt.Color.WHITE)
      g.drawRect(x, y, logoWidth, logoHeight)
      g.dispose()
      logo.flush()
      qrImg.flush()
    } catch {
      case e: Throwable => println(e.getMessage)
    }
  }
}
