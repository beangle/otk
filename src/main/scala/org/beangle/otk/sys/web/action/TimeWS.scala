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

package org.beangle.otk.sys.web.action

import org.beangle.web.action.annotation.{mapping, response}
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.View

import java.time.Instant

/**
 * 当前时间
 */
class TimeWS extends ActionSupport {

  def now(): View = {
    raw(System.currentTimeMillis())
  }

  def iso(): View = {
    raw(Instant.now.toString)
  }
}

