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

import org.beangle.commons.logging.Logging
import org.beangle.otk.captcha.core.{Captcha, CaptchaEngine, CaptchaException}

object CaptchaService {
  def apply[C, A](store: CaptchaStore[A], engine: CaptchaEngine[C, A]) = {
    new DefaultCaptchaService(store, engine)
  }
}

trait CaptchaService[C, A] {
  def getChallenge(id: String): C

  def validateResponse(id: String, response: A): Boolean
}

class DefaultCaptchaService[C, A](val store: CaptchaStore[A], val engine: CaptchaEngine[C, A])
  extends CaptchaService[C, A] with Logging {

  override def getChallenge(id: String): C = {
    val captcha = this.engine.next()
    this.store.put(id, captcha.answer)
    captcha.challenge
  }

  override def validateResponse(id: String, response: A): Boolean = {
    this.store.get(id) match {
      case None => false
      case Some(answer) =>
        store.remove(id)
        engine.validate(answer, response)
    }
  }

}