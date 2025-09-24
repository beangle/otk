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

package org.beangle.otk.security.web.action

import org.beangle.otk.security.service.PasswordService
import org.beangle.webmvc.annotation.response
import org.beangle.webmvc.support.ActionSupport

/** 密码服务
 */
class PasswordWS extends ActionSupport {

  private val passwordService = new PasswordService

  @response
  def generate(): String = {
    var len = getInt("len", 10)
    if (len <= 0) len = 10
    else if (len > 64) len = 64

    val includeUpper = getBoolean("upper", true)
    val includeSpecial = getBoolean("special", true)
    passwordService.generate(len, true, true, includeUpper, includeSpecial)
  }
}
