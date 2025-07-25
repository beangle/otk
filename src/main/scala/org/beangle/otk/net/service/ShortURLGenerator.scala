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

package org.beangle.otk.net.service

import org.beangle.commons.bean.Initializing
import org.beangle.commons.cache.{Cache, CacheManager}

import java.util.Random

class ShortURLGenerator extends Initializing {
  private val BASE62: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  private val SHORT_URL_LENGTH: Int = 6
  private val random: Random = new Random
  private var short2long: Cache[String, String] = _
  private var long2short: Cache[String, String] = _
  var cacheManager: CacheManager = _

  override def init(): Unit = {
    short2long = cacheManager.getCache("shorturl-short", classOf[String], classOf[String])
    long2short = cacheManager.getCache("shorturl-long", classOf[String], classOf[String])
  }

  def getLongUrl(shortUrl: String): Option[String] = {
    short2long.get(shortUrl)
  }

  // 生成短链接
  def getShortUrl(longUrl: String): String = {
    // 检查是否已存在该长链接的映射
    long2short.get(longUrl) match {
      case Some(sl) => sl
      case None => generate(longUrl)
    }
  }

  protected def generate(longUrl: String): String = {
    // 生成新的短链接
    var shortUrl: String = ""
    while {
      val sb = new StringBuilder
      for (i <- 0 until SHORT_URL_LENGTH) {
        sb.append(BASE62.charAt(random.nextInt(BASE62.length)))
      }
      shortUrl = sb.toString
      short2long.get(shortUrl).nonEmpty
    } do ()
    short2long.put(shortUrl, longUrl)
    long2short.put(longUrl, shortUrl)
    shortUrl
  }

}
