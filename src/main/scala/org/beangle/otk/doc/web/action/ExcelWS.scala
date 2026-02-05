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

import org.beangle.commons.activation.MediaTypes
import org.beangle.commons.io.IOs
import org.beangle.doc.excel.html.TableWriter
import org.beangle.doc.html.TableParser
import org.beangle.web.servlet.util.RequestUtils
import org.beangle.webmvc.annotation.mapping
import org.beangle.webmvc.support.{ActionSupport, ServletSupport}
import org.beangle.webmvc.view.{Status, View}

/** Convert a html table to excel
 */
class ExcelWS extends ActionSupport, ServletSupport {
  @mapping("")
  def index(): View = {
    var html = IOs.readString(request.getInputStream)
    if (!html.contains("<body")) {
      html = "<body>" + html + "</body>"
    }
    val doc = TableParser.parse(html)
    val workbook = TableWriter.write(doc)
    val os = response.getOutputStream
    response.setContentType(MediaTypes.xlsx.toString)
    RequestUtils.setContentDisposition(response, doc.title.getOrElse("table.xlsx"))
    workbook.write(os)
    workbook.close()
    Status.Ok
  }
}
