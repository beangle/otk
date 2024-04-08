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

import org.beangle.otk.captcha.core.CaptchaException

import java.awt.font.TextAttribute
import java.awt.image.{BufferedImage, ImageObserver}
import java.awt.{Font, Graphics2D}
import java.text.AttributedString

trait WordToImage {
  def getImage(word: String): BufferedImage

  def minAcceptedWordLength: Int

  def maxAcceptedWordLength: Int
}

abstract class AbstractWordToImage(fontGenerator: FontGenerator,
                                   backgroundGenerator: BackgroundGenerator,
                                   textPaster: TextPaster,
                                   val minAcceptedWordLength: Int,
                                   val maxAcceptedWordLength: Int) extends WordToImage {
  require(minAcceptedWordLength < maxAcceptedWordLength && minAcceptedWordLength > 0)

  override def getImage(word: String): BufferedImage = {
    val aword = this.getAttributedString(word, this.checkWordLength(word))
    val background = this.backgroundGenerator.next()
    this.pasteText(background, aword)
  }

  def getAttributedString(word: String, wordLength: Int) = {
    val attributedWord = new AttributedString(word)
    for (i <- 0 until wordLength) {
      val font = fontGenerator.next()
      attributedWord.addAttribute(TextAttribute.FONT, font, i, i + 1)
    }
    attributedWord
  }

  protected def checkWordLength(word: String) = {
    if (word == null) throw new CaptchaException("null word")
    else {
      val wordLength = word.length
      if (wordLength <= maxAcceptedWordLength && wordLength >= minAcceptedWordLength) wordLength
      else throw new CaptchaException("invalid length word")
    }
  }

  protected def pasteText(background: BufferedImage, attributedWord: AttributedString): BufferedImage = {
    this.textPaster.pasteText(background, attributedWord)
  }
}

class DeformedComposedWordToImage(fontGenerator: FontGenerator, backgroundGenerator: BackgroundGenerator,
                                  textPaster: TextPaster, minAcceptedWordLength: Int, maxAcceptedWordLength: Int)
  extends AbstractWordToImage(fontGenerator, backgroundGenerator, textPaster, minAcceptedWordLength, maxAcceptedWordLength) {

  override def getImage(word: String): BufferedImage = {
    val background = this.backgroundGenerator.next()
    val aword = this.getAttributedString(word, this.checkWordLength(word))
    val out = new BufferedImage(background.getWidth, background.getHeight, background.getType)
    val g2 = out.getGraphics.asInstanceOf[Graphics2D]
    g2.drawImage(background, 0, 0, out.getWidth, out.getHeight, null.asInstanceOf[ImageObserver])
    g2.dispose()

    var transparent = new BufferedImage(out.getWidth, out.getHeight, 2)
    transparent = this.pasteText(transparent, aword)

    val g3 = out.getGraphics.asInstanceOf[Graphics2D]
    g3.drawImage(transparent, 0, 0, null.asInstanceOf[ImageObserver])
    g3.dispose()

    out
  }
}
