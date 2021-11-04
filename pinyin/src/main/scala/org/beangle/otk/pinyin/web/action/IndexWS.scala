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

package org.beangle.otk.pinyin.web.action

import org.beangle.cache.CacheManager
import org.beangle.commons.bean.Initializing
import org.beangle.commons.lang.Strings
import org.beangle.otk.pinyin.web.helper.PinyinHelper
import org.beangle.web.action.annotation.{action, mapping, param, response}
import org.beangle.web.action.support.{ActionSupport, ServletSupport}
import org.beangle.web.action.view.{Status, Stream, View}

import java.io.InputStream
import java.net.URLDecoder
import java.util.Base64.Decoder

@action("")
class IndexWS extends ActionSupport with ServletSupport {

  @mapping("person/{n}")
  def person(@param("n") n: String): View = {
    if Strings.isEmpty(n) then
      Status.NotFound
    else
      response.getWriter.write( PinyinHelper.toNamePinyin(URLDecoder.decode(n,"UTF-8")))
      null
  }

}
