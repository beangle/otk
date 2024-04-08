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

package org.beangle.otk.sns.web.helper

import org.beangle.commons.collection.Properties
import org.beangle.commons.io.IOs
import org.beangle.commons.lang.{ClassLoaders, Numbers, Strings}

import java.io.{InputStreamReader, LineNumberReader}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.mutable

object IdHelper {
  private val validChars = Set('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'X', 'x')
  private val weights = Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
  private val checkCodes = Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')
  private var divisions: Map[String, String] = _

  def load(): Unit = {
    val input = ClassLoaders.getResource("org/beangle/otk/sns/divisions.properties").get
    divisions = IOs.readProperties(input)
  }

  def resolve(idcard: String): (String, Properties) = {
    val properties = new Properties()

    if !isFormatValid(idcard) then
      ("格式非法", properties)
    else if !isCheckSumRight(idcard) then
      ("校验错误", properties)
    else {
      val division = idcard.substring(0, 6)
      val birthday = idcard.substring(6, 14)
      val seqNo = idcard.substring(14, 17)
      try {
        val bornOn = LocalDate.parse(birthday, DateTimeFormatter.BASIC_ISO_DATE)
        val places = new mutable.ArrayBuffer[String]
        val province = division.substring(0, 2) + "0000"
        val city = division.substring(0, 4) + "00"
        places ++= divisions.get(province)
        places ++= divisions.get(city)
        places ++= divisions.get(division)
        if places.isEmpty then ("前六位行政区划代码错误", properties)
        else {
          properties.put("division", places.mkString(" "))
          properties.put("birthday", bornOn.toString)
          val gender =
            if Numbers.toInt(seqNo) % 2 == 1 then new Properties("id" -> 1, "name" -> "男")
            else new Properties("id" -> 2, "name" -> "女")
          properties.put("gender", gender)
          ("OK", properties)
        }
      } catch
        case e: Throwable => ("生日格式非法", properties)
    }
  }

  def isCheckSumRight(s: String): Boolean = {
    val sum = (0 until 17).map(i => s.charAt(i).toString.toInt * weights(i)).sum
    checkCodes(sum % 11) == s.charAt(17)
  }

  def isFormatValid(s: String): Boolean = {
    if Strings.isBlank(s) || s.length != 18 then false
    else s.forall(x => validChars.contains(x))
  }

}
