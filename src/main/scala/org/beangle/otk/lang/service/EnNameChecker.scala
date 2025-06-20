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

package org.beangle.otk.lang.service

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.languagetool.{JLanguageTool, Languages}

/** 英文名检查器
 */
class EnNameChecker {
  val preps = Set("of", "to", "as", "by", "on", "in", "about", "from", "with", "for", "like", "up", "down", "under",
    "behind", "over", "beyond", "between", "below", "above", "including", "except", "without", "into", "onto",
    "through", "before", "after", "since", "off", "during", "beside", "besides", "past", "till", "until", "opposite",
    "near", "towards", "but", "among", "along", "against", "across", "around", "round", "next", "despite", "than",
    "outside", "inside",
    "a", "an", "at", "and")

  val symbols = Set('(', '&', '"')
  private val lang = Languages.getLanguageForShortCode("en-US")
  private val msgs = Map("It appears that a white space is missing." -> "少一个空格",
    "Possible spelling mistake found." -> "有可能拼写错误",
    "It seems like there are too many consecutive spaces here." -> "连续空格",
    "Possible typo: you repeated a whitespace" -> "连续空格",
    "Don't put a space after the opening parenthesis." -> "括弧后不要放空格")

  def check(enNames: Iterable[String]): Map[String, String] = {
    val rs = Collections.newMap[String, String]
    enNames foreach { enName =>
      val formatOK = isFormatCorrect(enName)
      if (!formatOK._1) {
        rs.put(enName, s"大小写错误:${formatOK._2}")
      } else {
        val tool = new JLanguageTool(lang)
        val matches = tool.check(enName)
        val suggested = new StringBuilder
        if (!matches.isEmpty) {
          val i = matches.iterator()
          while (i.hasNext) {
            val m = i.next()
            val msg = msgs.getOrElse(m.getMessage, m.getMessage)
            suggested.append(msg)
            if (msg == "连续空格") {
              var from = m.getFromPos
              var to = m.getToPos
              if (from - 3 >= 0) from -= 3
              if (to + 3 < enName.length) to += 3
              suggested.append(enName.substring(from, to).replace(" ", "&bull;"))
            } else {
              val replacements = m.getSuggestedReplacements
              if (!replacements.isEmpty) {
                suggested.append(enName.substring(m.getFromPos, m.getToPos) + "=>" + replacements.get(0))
              }
            }
          }
          rs.put(enName, s"${suggested}")
        }
      }
    }
    rs.toMap
  }

  private def isFormatCorrect(enName: String): (Boolean, String) = {
    val parts = Strings.split(enName)
    val names = Collections.newBuffer[String]
    var errors = 0
    parts foreach { part =>
      val lp = part.toLowerCase
      //如果是介词
      if (preps.contains(lp)) {
        if (lp == part) {
          names.addOne(part)
        } else {
          if (lp == "the" && part == "The" && enName.startsWith("The ")) {
            names.addOne(part)
          } else {
            errors += 1
            names.addOne(s"*${part}*")
          }
        }
      } else {
        if (Character.isUpperCase(part.charAt(0)) || symbols.contains(part.charAt(0))) {
          names.addOne(part)
        } else {
          errors += 1
          names.addOne(s"*${part}*")
        }
      }
    }
    if (errors > 0) {
      (false, names.mkString(" "))
    } else {
      (true, null)
    }
  }
}
