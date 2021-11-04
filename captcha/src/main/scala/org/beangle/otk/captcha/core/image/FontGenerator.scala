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

import org.beangle.otk.captcha.core.image.RandomFontGenerator.*

import java.awt.Font
import java.security.SecureRandom
import scala.collection.mutable

trait FontGenerator {
  def next(): Font

  def minFontSize: Int

  def maxFontSize: Int
}

object RandomFontGenerator {
  var defaultBadFontNamePrefixes: Array[String] = Array[String]("Courier", "Times Roman")
  val GENERATED_FONTS_ARRAY_SIZE: Int = 3000
}

class RandomFontGenerator(val minFontSize: Int, val maxFontSize: Int, fontsList: Array[Font]) extends FontGenerator {
  private val STYLES = Array[Int](0, 2, 1, 3)
  private var requiredCharacters: String = "abcdefghijklmnopqrstuvwxyz0123456789"
  protected val myRandom = new SecureRandom
  private val generatedFonts = generateCustomStyleFontArray(fontsList)

  override def next(): Font = {
    this.generatedFonts(Math.abs(this.myRandom.nextInt(GENERATED_FONTS_ARRAY_SIZE)))
  }

  private def generateCustomStyleFontArray(fonts: Array[Font]): Array[Font] = {
    val fontList = fonts.filter(f => !this.requiredCharacters.exists(x => !f.canDisplay(x)))
    val generatedFonts = new Array[Font](GENERATED_FONTS_ARRAY_SIZE)
    for (i <- 0 until GENERATED_FONTS_ARRAY_SIZE) {
      val w = this.myRandom.nextInt(fontList.size)
      val font = fontList(w)
      val styled = this.applyStyle(font)
      generatedFonts(i) = this.applyCustomDeformationOnGeneratedFont(styled)
    }
    generatedFonts
  }

  protected def applyStyle(font: Font): Font = {
    var fontSizeIncrement: Int = 0
    if (this.getFontSizeDelta > 0) {
      fontSizeIncrement = Math.abs(this.myRandom.nextInt(this.getFontSizeDelta))
    }
    font.deriveFont(this.STYLES(this.myRandom.nextInt(this.STYLES.length)), (this.minFontSize + fontSizeIncrement).toFloat)
  }

  private def getFontSizeDelta: Int = {
    this.maxFontSize - this.minFontSize
  }

  protected def applyCustomDeformationOnGeneratedFont(font: Font): Font = {
    font
  }

  def getRequiredCharacters: String = {
    this.requiredCharacters
  }

  def setRequiredCharacters(requiredCharacters: String): Unit = {
    this.requiredCharacters = requiredCharacters
  }
}
