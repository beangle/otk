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

package org.beangle.otk.net.web.action

import org.beangle.commons.lang.Strings
import org.beangle.otk.net.service.ShortURLGenerator
import org.beangle.webmvc.annotation.{mapping, param}
import org.beangle.webmvc.support.{ActionSupport, ServletSupport}
import org.beangle.webmvc.view.{Status, View}

/** 短地址服务
 */
class UrlWS extends ActionSupport, ServletSupport {
  var generator: ShortURLGenerator = _

  /** 直接访问短链接
   *
   * @param shortCode
   * @return
   */
  @mapping("{shortCode}")
  def index(@param("shortCode") shortCode: String): View = {
    generator.getLongUrl(shortCode) match {
      case None => Status.NotFound
      case Some(url) => response.sendRedirect(url); null
    }
  }

  /** 还原短链接
   *
   * @param shortCode
   * @return
   */
  @mapping("restore/{shortCode}")
  def restore(@param("shortCode") shortCode: String): View = {
    generator.getLongUrl(shortCode) match {
      case None => Status.NotFound
      case Some(url) =>
        response.getWriter.write(url)
        null
    }
  }

  /** 生成短链接
   *
   * @return
   */
  def shorten(): View = {
    val url = get("url", "")
    if (Strings.isEmpty(url)) {
      Status.NotFound
    } else {
      response.getWriter.write(generator.getShortUrl(url))
      null
    }
  }
}
