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

package org.beangle.otk.security.service

import java.security.SecureRandom

class PasswordService {

  private val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  private val LOWER = "abcdefghijklmnopqrstuvwxyz"
  private val DIGITS = "0123456789"
  private val SPECIAL = "!@#$%^&*()_+-=[]{}|<>?" //;:,.

  private val random = new SecureRandom()

  def generate(length: Int,
               includeDigits: Boolean,
               includeLower: Boolean,
               includeUpper: Boolean,
               includeSpecial: Boolean): String = {
    require(length > 0, "密码长度必须大于0")
    // 构建字符集
    val chars = new StringBuilder
    if (includeUpper) chars.append(UPPER)
    if (includeLower) chars.append(LOWER)
    if (includeDigits) chars.append(DIGITS)
    if (includeSpecial) chars.append(SPECIAL)

    // 确保至少有一种字符类型被选中
    require(chars.nonEmpty, "至少要选择一种字符类型")
    // 生成密码
    val password = new StringBuilder(length)
    var i = 0
    while (i < length) {
      // 从字符集中随机选择一个字符
      val index = random.nextInt(chars.length)
      password.append(chars.charAt(index))
      i += 1
    }
    password.toString
  }
}
