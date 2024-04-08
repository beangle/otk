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

import org.beangle.otk.captcha.core.image.ColorGenerator

import java.awt.image.{BufferedImage, ImageObserver}
import java.awt.{Color, Graphics2D, RenderingHints}
import java.text.AttributedString

trait TextPaster {
  def pasteText(var1: BufferedImage, var2: AttributedString): BufferedImage
}

class DecoratedTextPaster(val colorGenerator: ColorGenerator, var manageColorPerGlyph: Boolean= false)
  extends TextPaster {
  protected def copyBackground(background: BufferedImage) = {
    new BufferedImage(background.getWidth, background.getHeight, background.getType)
  }

  protected def pasteBackgroundAndSetTextColor(out: BufferedImage, background: BufferedImage) = {
    val pie = out.getGraphics.asInstanceOf[Graphics2D]
    pie.drawImage(background, 0, 0, out.getWidth, out.getHeight, null.asInstanceOf[ImageObserver])
    pie.setColor(this.colorGenerator.next())
    pie
  }

  override def pasteText(background: BufferedImage, attributedWord: AttributedString): BufferedImage = {
    val out = this.copyBackground(background)
    val g2 = this.pasteBackgroundAndSetTextColor(out, background)
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    val newAttrString = ChangeableAttributedString(g2, attributedWord, 20)
    newAttrString.useMinimumSpacing(20.0D)
    newAttrString.moveToRandomSpot(background)
    if (this.manageColorPerGlyph) newAttrString.drawString(g2, this.colorGenerator)
    else newAttrString.drawString(g2)
    g2.dispose()
    out
  }
}
