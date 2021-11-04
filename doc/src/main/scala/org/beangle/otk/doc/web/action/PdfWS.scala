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

import com.itextpdf.text.pdf.PdfWriter
import org.beangle.commons.codec.digest.Digests
import org.beangle.doc.pdf.{Encryptor, SPD}
import org.beangle.web.action.annotation.*
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.{Stream, View}

import java.io.File
import java.net.URL
import java.time.Instant

class PdfWS extends ActionSupport {

  @mapping("")
  def index(@param("url") url: String): View = {
    val pdf = File.createTempFile("doc", ".pdf")
    SPD.convertURL(new URL(url), pdf)
    val userPassword = get("password")
    val ownerPassword = get("ownerPassword").getOrElse(Digests.md5Hex("Cannot change it."))
    Encryptor.encrypt(pdf, userPassword, ownerPassword, PdfWriter.ALLOW_PRINTING)
    get("fileName") match {
      case Some(f) => Stream(pdf, f)
      case None => Stream(pdf)
    }
  }

}
