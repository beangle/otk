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
    response.setContentType(MediaTypes.ApplicationXlsx.toString)
    RequestUtils.setContentDisposition(response, doc.title.getOrElse("table.xlsx"))
    workbook.write(os)
    workbook.close()
    Status.Ok
  }
}
