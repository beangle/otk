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

import org.beangle.otk.captcha.core.word.{DictionaryReader, WordGenerator}
import org.beangle.otk.captcha.core.{Captcha, CaptchaEngine}

import java.awt.image.BufferedImage
import java.awt.{Color, Font}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import java.security.SecureRandom
import java.util.{Locale, Random}
import javax.imageio.ImageIO

object GmailEngine {

  def image(): GmailEngine = {
    val minWordLength = 4
    val maxWordLength = 5
    val imageWidth = 90
    val imageHeight = 35
    val fontSize = 21

    val dictionnaryWords = new WordGenerator.ComposeDictionary(DictionaryReader.bundle("todd"))
    val randomPaster = new DecoratedRandomTextPaster(minWordLength, maxWordLength,
      ColorGenerator.random(Array(new Color(23, 170, 27), new Color(220, 34, 11), new Color(23, 67, 172))))
    val background = new UniColorBackgroundGenerator(imageWidth, imageHeight, Color.white)
    val font = new RandomFontGenerator(fontSize, fontSize,
      Array(new Font("nyala", 1, fontSize), new Font("Bell MT", 0, fontSize),
        new Font("Credit valley", 1, fontSize)))
    val word2image = new DeformedComposedWordToImage(font, background, randomPaster)
    new GmailEngine(dictionnaryWords, word2image)
  }
}

class GmailEngine(val generator: WordGenerator, val wordToImage: WordToImage) extends CaptchaEngine[InputStream, String] {
  private val myRandom = new SecureRandom

  override final def next(): Captcha[InputStream, String] = {
    val wordLength = this.getRandomLength
    val word = this.generator.getWord(wordLength)
    val image = wordToImage.getImage(word)
    val os = new ByteArrayOutputStream()
    ImageIO.write(image, "JPEG", os)
    Captcha(new ByteArrayInputStream(os.toByteArray), word)
  }

  override def validate(answer: String, response: String): Boolean = {
    response match {
      case null => false
      case r: String => r == answer
    }
  }

  protected def getRandomLength: Integer = {
    val wtoi = wordToImage
    val range = wtoi.maxAcceptedWordLength - wtoi.minAcceptedWordLength
    val randomRange = if range != 0 then this.myRandom.nextInt(range + 1) else 0
    randomRange + wtoi.minAcceptedWordLength
  }
}
