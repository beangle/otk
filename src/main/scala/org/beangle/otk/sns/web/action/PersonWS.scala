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

package org.beangle.otk.sns.web.action

import jakarta.servlet.http.HttpServletResponse
import org.beangle.commons.cache.CacheManager
import org.beangle.commons.bean.Initializing
import org.beangle.commons.collection.Properties
import org.beangle.commons.lang.Strings
import org.beangle.otk.sns.web.helper.{IdHelper, PinyinHelper}
import org.beangle.webmvc.annotation.{action, mapping, param, response}
import org.beangle.webmvc.support.{ActionSupport, ServletSupport}
import org.beangle.webmvc.view.{Status, Stream, View}

import java.io.InputStream
import java.net.URLDecoder

class PersonWS extends ActionSupport with ServletSupport with Initializing {

  @mapping("pinyin/{name}")
  def pinyinName(@param("name") name: String): View = {
    if Strings.isEmpty(name) then
      Status.NotFound
    else
      response.getWriter.write(PinyinHelper.toNamePinyin(URLDecoder.decode(name, "UTF-8")))
      null
  }

  @response
  @mapping("id/{idcard}")
  def id(@param("idcard") idcard: String): Properties = {
    val rs = IdHelper.resolve(idcard)
    if rs._2.isEmpty then
      response.setContentType("text/html;charset=utf-8")
      response.getWriter.write(rs._1)
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
      null
    else
      rs._2
  }

  override def init(): Unit = {
    IdHelper.load()
  }
}
