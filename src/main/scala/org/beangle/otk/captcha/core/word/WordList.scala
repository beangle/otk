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

import java.security.SecureRandom
import java.util.{Locale, ResourceBundle, StringTokenizer}

object WordList {
  def apply(words: Array[String]): WordList = {
    new WordList(words.groupBy(_.length))
  }
}

class WordList(words: Map[Int, Array[String]]) {
  private val myRandom = new SecureRandom

  def next(length: Int): Option[String] = {
    words.get(length) match {
      case None => None
      case Some(l) => Some(l(myRandom.nextInt(l.length)))
    }
  }

  override def toString: String = {
    words.map(x => (x._1, x._2.length)).toString()
  }
}
