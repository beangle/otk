package org.beangle.otk.sys.web.action

import org.beangle.web.action.annotation.{mapping, response}
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.View

import java.time.Instant

/**
 * 当前时间
 */
class TimeWS extends ActionSupport {

  def now(): View = {
    raw(System.currentTimeMillis())
  }

  def iso(): View = {
    raw(Instant.now.toString)
  }
}

