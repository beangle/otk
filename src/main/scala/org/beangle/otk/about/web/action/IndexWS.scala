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

package org.beangle.otk.about.web.action

import org.beangle.webmvc.context.ActionContext
import org.beangle.webmvc.support.ActionSupport
import org.beangle.webmvc.view.View

class IndexWS extends ActionSupport {

  def index(): View = {
    val request = ActionContext.current.request
    val urls = Seq(
      "/captcha/image/{id}.jpg" -> "生成验证码",
      "/captcha/validate/{id}.jpg" -> "验证验证码",
      "/code/bar/{content}.jpg" -> "生成条形码",
      "/code/qr/{content}.jpg" -> "生成二维码",
      "/doc/excel" -> "html生成excel",
      "/doc/pdf?url={url}" -> "URL生成pdf",
      "/lang/en/check?name={name}" -> "检查英文拼写问题",
      "/net/url/shorten?url={url}" -> "生成短地址",
      "/net/url/{shortCode}" -> "直接访问短链接",
      "/net/url/restore/{shortCode}" -> "获取短地址的真实地址",
      "/security/password/generate" -> "生成随机密码",
      "/sns/person/pinyin/{name}" -> "生成姓名拼音名",
      "/sns/person/idcard/{idcard}" -> "解析身份证信息",
      "/sys/time/now" -> "当前时间戳",
      "/sys/time/iso" -> "当前时间ISO",
    )

    val contents = urls.map(e => s"<li><a href='${request.getContextPath}${e._1}' target='_blank'>${request.getContextPath}${e._1}"
      + "</a>  " + e._2 + "</li>").mkString("")
    val html =
      s"""
         |<!doctype html>
         |<html lang="en">
         |  <head>
         |    <meta charset="utf-8"/>
         |  </head>
         |<body>
         |<p>Beangle Online Toolkit Service List</p>
         |<ul>${contents}</ul>
         |</body>
         |</html>
         |""".stripMargin
    raw(html)
  }
}
