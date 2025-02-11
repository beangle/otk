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

package org.beangle.otk.lang.web.action

import org.beangle.otk.lang.service.EnNameChecker
import org.beangle.webmvc.annotation.response
import org.beangle.webmvc.support.{ActionSupport, ServletSupport}
import org.beangle.webmvc.view.View

class EnWS extends ActionSupport, ServletSupport {

  val checker = new EnNameChecker()

  def check(): View = {
    forward()
  }

  @response
  def name(): Any = {
    val names = getAll("name", classOf[String])
    checker.check(names)
  }
}
