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

package org.beangle.otk.net.web

import org.beangle.cache.redis.{JedisPoolFactory, RedisCacheManager}
import org.beangle.commons.cdi.BindModule
import org.beangle.commons.io.DefaultBinarySerializer
import org.beangle.otk.config.Config
import org.beangle.otk.net.service.ShortURLGenerator
import org.beangle.otk.net.web.action.UrlWS

class DefaultModule extends BindModule {
  protected override def binding(): Unit = {
    bind("jedis.Factory", classOf[JedisPoolFactory]).constructor(Config.Redis.conf)
    bind("CacheManager.redis", classOf[RedisCacheManager]).constructor(ref("jedis.Factory"), DefaultBinarySerializer, true)
      .property("ttl", 7 * 24 * 60 * 60) //7 days

    bind(classOf[ShortURLGenerator]).property("cacheManager", ref("CacheManager.redis"))
    bind(classOf[UrlWS])
  }
}
