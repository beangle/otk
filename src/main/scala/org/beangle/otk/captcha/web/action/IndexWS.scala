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

package org.beangle.otk.captcha.web.action

import org.beangle.commons.cache.CacheManager
import org.beangle.commons.activation.MediaTypes
import org.beangle.commons.bean.Initializing
import org.beangle.commons.lang.Strings
import org.beangle.otk.captcha.core.image.GmailEngine
import org.beangle.otk.captcha.core.service.{CaptchaService, CaptchaStore, DefaultCaptchaService}
import org.beangle.webmvc.annotation.{action, mapping, param}
import org.beangle.webmvc.support.{ActionSupport, ServletSupport}
import org.beangle.webmvc.view.{Status, Stream, View}

import java.io.InputStream

@action("")
class IndexWS extends ActionSupport , ServletSupport , Initializing {

  var cacheManager: CacheManager = _
  var captchaService: CaptchaService[InputStream, String] = _

  override def init(): Unit = {
    val store = new CaptchaStore.CacheStore(cacheManager.getCache("captcha", classOf[String], classOf[String]))
    captchaService = new DefaultCaptchaService(store, GmailEngine.image())
  }

  @mapping("image/{id}")
  def image(@param("id") id: String): View = {
    if (Strings.isEmpty(id) || id.length != 50) {
      Status.NotFound
    } else {
      Stream(captchaService.getChallenge(id), MediaTypes.ImageJpeg, id + ".jpg")
    }
  }

  @mapping("validate/{id}")
  def validate(@param("id") id: String): View = {
    val captcha_response = get("response").orNull
    val trial = getBoolean("trial", false)
    if captchaService.validateResponse(id, captcha_response, !trial) then
      response.getWriter.write("success")
      Status.Ok
    else
      response.getWriter.write("mismatch")
      Status.NotFound

  }

}
