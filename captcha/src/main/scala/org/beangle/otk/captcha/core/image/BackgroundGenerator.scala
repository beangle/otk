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

import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D}
import java.security.SecureRandom
import java.util.Random

trait BackgroundGenerator {
  def next(): BufferedImage
}

class UniColorBackgroundGenerator(val width: Int = 100, val height: Int = 200, val color: Color) extends BackgroundGenerator {
  private val backgound = buildBackground()

  def buildBackground(): BufferedImage = {
    val backround = new BufferedImage(this.width, this.height, 1)
    val pie = backround.getGraphics.asInstanceOf[Graphics2D]
    pie.setColor(color)
    pie.setBackground(color)
    pie.fillRect(0, 0, this.width, this.height)
    pie.dispose()
    backround
  }

  override def next(): BufferedImage = backgound
}
