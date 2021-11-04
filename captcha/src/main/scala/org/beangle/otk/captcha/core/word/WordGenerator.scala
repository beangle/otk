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

package org.beangle.otk.captcha.core.word

import org.beangle.commons.lang.Strings

import java.security.SecureRandom
import java.util.{Locale, Random}

object WordGenerator {

  class Random(val acceptedChars: String="abcdefghijklmnopqrstuvwxyz0123456789") extends WordGenerator {
    private val possiblesChars: Array[Char] = acceptedChars.toCharArray
    private val myRandom = new SecureRandom

    override def getWord(length: Int): String = {
      val word = new StringBuffer(length)
      var i = 0
      while (i < length) {
        word.append(this.possiblesChars(this.myRandom.nextInt(this.possiblesChars.length)))
        i += 1
      }
      word.toString
    }
  }

  class Dictionary(reader: DictionaryReader) extends WordGenerator {
    protected val words = reader.read()

    override def getWord(length: Int): String = {
      words.next(length).getOrElse(Strings.repeat('*', length))
    }
  }

  class ComposeDictionary(reader: DictionaryReader) extends Dictionary(reader) {
    override def getWord(length: Int): String = {
      if length <= 2 then words.next(length).getOrElse(Strings.repeat('*', length))
      else
        val firstLength = length / 2
        var firstWord: String = null
        for (i <- firstLength until 50; if null == firstWord) {
          words.next(firstLength + i) foreach { w =>
            firstWord = w.substring(0, firstLength)
          }
        }

        var secondWord: String = null
        for (i <- firstLength until 50; if secondWord == null) {
          words.next(length - firstLength + i) foreach { w =>
            secondWord = w.substring(w.length - length + firstLength, w.length)
          }
        }
        checkAndFindSmaller(firstWord, firstLength) + checkAndFindSmaller(secondWord, length - firstLength)
    }

    private def checkAndFindSmaller(firstWord: String, length: Int) = {
      if firstWord == null then getWord(length) else firstWord
    }
  }
}

trait WordGenerator {
  def getWord(length: Int): String
}
