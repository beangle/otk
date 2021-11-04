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

package org.beangle.otk.captcha.core.service

import org.beangle.cache.Cache
import org.beangle.otk.captcha.core.Captcha

import java.util as ju

object CaptchaStore {

  final class MemoryStore[A] extends CaptchaStore[A] {
    private val store = new java.util.HashMap[String, A]

    def exists(id: String): Boolean = store.containsKey(id)

    def put(id: String, response: A): Unit = store.put(id, response)

    def remove(id: String): Boolean = store.remove(id) != null

    def get(id: String): Option[A] = Option(store.get(id))
  }

  class CacheStore[A](cache: Cache[String, A]) extends CaptchaStore[A] {
    def exists(id: String): Boolean = cache.exists(id)

    def put(id: String, response: A): Unit = {
      cache.put(id, response)
    }

    def remove(id: String): Boolean = {
      cache.evict(id)
    }

    def get(id: String): Option[A] = {
      cache.get(id)
    }

  }
}

trait CaptchaStore[A] {
  def exists(id: String): Boolean

  def put(id: String, response: A): Unit

  def remove(id: String): Boolean

  def get(id: String): Option[A]
}
