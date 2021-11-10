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

package org.beangle.otk.pinyin.web.helper

import net.sourceforge.pinyin4j.PinyinHelper as PHelper
import net.sourceforge.pinyin4j.format.{HanyuPinyinCaseType, HanyuPinyinOutputFormat, HanyuPinyinToneType}
import org.beangle.commons.collection.Collections
import org.beangle.commons.io.Files.writeOpen
import org.beangle.commons.io.{Files, IOs}
import org.beangle.commons.lang.Strings.{capitalize, substringBefore}
import org.beangle.commons.lang.{Charsets, Strings}

import java.io.File

object PinyinHelper {

  val doubles = Strings.split("欧阳,太史,端木,上官,司马,东方,独孤,南宫,万俟,闻人,夏侯,诸葛,尉迟,公羊,赫连,澹台,皇甫,宗政,濮阳," +
    "公冶,太叔,申屠,公孙,慕容,仲孙,钟离,长孙,宇文,司徒,鲜于,司空,闾丘,子车,亓官,司寇,巫马,公西,颛孙,壤驷,公良,漆雕,乐正,宰父,谷梁,拓跋,夹谷," +
    "轩辕,令狐,段干,百里,呼延,东郭,南门,羊舌,微生,公户,公玉,公仪,梁丘,公仲,公上,公门,公山,公坚,左丘,公伯,西门,公祖,第五,公乘,贯丘,公皙,南荣," +
    "东里,东宫,仲长,子书,子桑,即墨,达奚,褚师").toSet

  private val doublesFirstWords = doubles.map(_.substring(0, 1))

  def toNamePinyin(name: String): String = {
    val defaultFormat = new HanyuPinyinOutputFormat()
    defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE)
    defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE)
    if (name.length < 2) {
      Strings.capitalize(toPinyin(name, defaultFormat))
    } else {
      var familyName = name.substring(0, 1)
      var givenName = name.substring(1)
      if (doublesFirstWords.contains(familyName)) {
        val doubleWord = name.substring(0, 2)
        if (doubles.contains(doubleWord)) {
          familyName = doubleWord
          givenName = name.substring(2)
        }
      }
      val builder = new StringBuilder()
      builder.append(Strings.capitalize(toPinyin(familyName, defaultFormat)))
      builder.append(' ')
      builder.append(Strings.capitalize(toPinyin(givenName, defaultFormat)))
      builder.toString()
    }
  }

  private def toPinyin(w: String, format: HanyuPinyinOutputFormat): String = {
    val builder = new StringBuilder()
    val arr = w.toCharArray
    (0 until arr.length) foreach { i =>
      val pinyinArray = PHelper.toHanyuPinyinStringArray(arr(i), format)
      if (null != pinyinArray && pinyinArray.nonEmpty) {
        builder.append(pinyinArray(0))
      } else {
        builder.append(arr(i))
      }
    }
    builder.toString()
  }
}
