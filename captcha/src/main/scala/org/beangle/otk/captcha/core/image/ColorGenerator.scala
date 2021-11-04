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

package org.beangle.otk.captcha.core.image

import java.awt.Color
import java.security.SecureRandom

object ColorGenerator {
  class Single(val color: Color) extends ColorGenerator {
    override def next(): Color = this.color
  }

  class Random(val colorsList: Array[Color]) extends ColorGenerator {
    private val random = new SecureRandom

    override def next(): Color = {
      val index = this.random.nextInt(this.colorsList.length)
      this.colorsList(index)
    }
  }

  def random(colorsList: Array[Color]): ColorGenerator = {
    new Random(colorsList)
  }
}

trait ColorGenerator {
  def next(): Color
}
