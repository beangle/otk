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

package org.beangle.otk.doc.web.action

import org.beangle.commons.bean.Properties
import org.beangle.commons.codec.digest.Digests
import org.beangle.doc.core.{PageMargin, PrintOptions}
import org.beangle.doc.pdf.{Encryptor, SPDConverter}
import org.beangle.webmvc.annotation.*
import org.beangle.webmvc.context.Params
import org.beangle.webmvc.support.ActionSupport
import org.beangle.webmvc.view.{Status, Stream, View}

import java.io.File
import java.net.URI

/** Convert a html url to pdf
 */
class PdfWS extends ActionSupport {

  var converter: SPDConverter = _

  @mapping("")
  def index(@param("url") url: String): View = {
    if null == converter then converter = SPDConverter.getInstance()
    val pdf = File.createTempFile("doc", ".pdf")
    val options = PrintOptions.defaultOptions

    val params = Params.sub("options")
    params foreach { case (k, v) =>
      if (k == "margin") {
        options.margin = PageMargin(v.toString)
      } else {
        Properties.copy(options, k, v)
      }
    }
    if converter.convert(URI.create(url), pdf, options) then
      val userPassword = get("password")
      val ownerPassword = get("ownerPassword").getOrElse(Digests.md5Hex("Cannot change it."))
      Encryptor.encrypt(pdf, userPassword, ownerPassword)
      get("fileName") match {
        case Some(f) => Stream(pdf, f)
        case None => Stream(pdf)
      }
    else
      Status(500)
  }

}
