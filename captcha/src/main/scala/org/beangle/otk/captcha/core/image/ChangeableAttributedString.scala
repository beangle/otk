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

package org.beangle.otk.captcha.core.image

import org.beangle.otk.captcha.core.CaptchaException
import org.beangle.otk.captcha.core.image.ColorGenerator

import java.awt.font.{FontRenderContext, LineMetrics, TextAttribute}
import java.awt.geom.{Point2D, Rectangle2D}
import java.awt.image.BufferedImage
import java.awt.{Font, Graphics2D}
import java.security.SecureRandom
import java.text.{AttributedCharacterIterator, AttributedString}
import java.util.Random

object ChangeableAttributedString {
  def apply(g2: Graphics2D, aString: AttributedString, kerning: Int): ChangeableAttributedString = {
    val iter = aString.getIterator
    val n = iter.getEndIndex
    val aStrings = new Array[AttributedString](n)
    val bounds = new Array[Rectangle2D](n)
    val metrics = new Array[LineMetrics](n)
    for (i <- iter.getBeginIndex until iter.getEndIndex) {
      iter.setIndex(i)
      aStrings(i) = new AttributedString(iter, i, i + 1)
      val font = iter.getAttribute(TextAttribute.FONT).asInstanceOf[Font]
      if (font != null) g2.setFont(font)
      val frc = g2.getFontRenderContext
      bounds(i) = g2.getFont.getStringBounds(iter, i, i + 1, frc)
      metrics(i) = g2.getFont.getLineMetrics(Character.valueOf(iter.current).toString, frc)
    }
    new ChangeableAttributedString(g2, aStrings, bounds, metrics, kerning)
  }
}

class ChangeableAttributedString(val g2: Graphics2D, aStrings: Array[AttributedString], bounds: Array[Rectangle2D], metrics: Array[LineMetrics], var kerning: Int) {

  private val myRandom = new SecureRandom

  def drawString(g2: Graphics2D): Unit = {
    for (i <- 0 until this.length) {
      g2.drawString(this.getIterator(i), this.getX(i).toFloat, this.getY(i).toFloat)
    }
  }

  def drawString(g2: Graphics2D, colorGenerator: ColorGenerator): Unit = {
    for (i <- 0 until this.length) {
      g2.setColor(colorGenerator.next())
      g2.drawString(this.getIterator(i), this.getX(i).toFloat, this.getY(i).toFloat)
    }
  }

  def moveToRandomSpot(background: BufferedImage): Point2D = {
    this.moveToRandomSpot(background, null.asInstanceOf[Point2D])
  }

  private def moveToRandomSpot(background: BufferedImage, startingPoint: Point2D): Point2D = {
    val maxHeight = this.getMaxHeight.toInt
    var maxX = background.getWidth.toDouble - this.getTotalWidth
    val maxY = (background.getHeight - maxHeight).toDouble
    var newY = 0
    if (startingPoint == null) newY = this.getMaxAscent.toInt + this.myRandom.nextInt(Math.max(1, maxY.toInt))
    else newY = (startingPoint.getY + this.myRandom.nextInt(10).toDouble).toInt
    if (!(maxX < 0.0D) && !(maxY < 0.0D)) {
      var newX = 0
      if (startingPoint == null) newX = this.myRandom.nextInt(Math.max(1, maxX.toInt))
      else newX = (startingPoint.getX + this.myRandom.nextInt(10).toDouble).toInt
      this.moveTo(newX.toDouble, newY.toDouble)
      new Point2D.Float(newX.toFloat, newY.toFloat)
    } else {
      var problem = "too tall:"
      if (maxX < 0.0D && maxY > 0.0D) {
        problem = "too long:"
        this.useMinimumSpacing((this.kerning / 2).toDouble)
        maxX = background.getWidth.toDouble - this.getTotalWidth
        if (maxX < 0.0D) {
          this.useMinimumSpacing(0.0D)
          maxX = background.getWidth.toDouble - this.getTotalWidth
          if (maxX < 0.0D) maxX = this.reduceHorizontalSpacing(background.getWidth, 0.05D)
        }
        if (maxX > 0.0D) {
          this.moveTo(0.0D, newY.toDouble)
          return new Point2D.Float(0.0F, newY.toFloat)
        }
      }
      throw new CaptchaException("word is " + problem + " try to use less letters, smaller font" +
        " or bigger background: " + " text bounds = " + this + " with fonts " + this.getFontListing +
        " versus image width = " + background.getWidth + ", height = " + background.getHeight)
    }
  }

  private def getFontListing: String = {
    val buf = new StringBuffer
    buf.append("{")
    for (i <- 0 until this.length) {
      val iter = this.aStrings(i).getIterator
      val font = iter.getAttribute(TextAttribute.FONT).asInstanceOf[Font]
      if (font != null) buf.append(font.toString).append("\n\t")
    }
    buf.append("}")
    buf.toString
  }

  def useMonospacing(kerning: Double): Unit = {
    val maxWidth = this.getMaxWidth
    for (i <- 1 until this.bounds.length) {
      this.getBounds(i).setRect(this.getX(i - 1) + maxWidth + kerning, this.getY(i), this.getWidth(i), this.getHeight(i))
    }
  }

  def useMinimumSpacing(kerning: Double): Unit = {
    for (i <- 1 until this.length) {
      this.bounds(i).setRect(this.bounds(i - 1).getX + this.bounds(i - 1).getWidth + kerning, this.bounds(i).getY, this.bounds(i).getWidth, this.bounds(i).getHeight)
    }
  }

  private def reduceHorizontalSpacing(imageWidth: Int, maxReductionPct: Double) = {
    var maxX = imageWidth.toDouble - this.getTotalWidth
    var pct = 0.0D
    val stepSize = maxReductionPct / 25.0D
    pct = stepSize
    while (pct < maxReductionPct && maxX < 0.0D) {
      for (i <- 1 until this.length) {
        this.bounds(i).setRect((1.0D - pct) * this.bounds(i).getX, this.bounds(i).getY, this.bounds(i).getWidth, this.bounds(i).getHeight)
      }
      maxX = imageWidth.toDouble - this.getTotalWidth
      pct += stepSize
    }
    maxX
  }

  private def moveTo(newX: Double, newY: Double): Unit = {
    this.bounds(0).setRect(newX, newY, this.bounds(0).getWidth, this.bounds(0).getHeight)
    for (i <- 1 until this.length) {
      this.bounds(i).setRect(newX + this.bounds(i).getX, newY, this.bounds(i).getWidth, this.bounds(i).getHeight)
    }
  }

  protected def shiftBoundariesToNonLinearLayout(backgroundWidth: Double, backgroundHeight: Double): Unit = {
    val newX = backgroundWidth / 20.0D
    val middleY = backgroundHeight / 2.0D
    val myRandom = new SecureRandom
    this.bounds(0).setRect(newX, middleY, this.bounds(0).getWidth, this.bounds(0).getHeight)
    for (i <- 1 until this.length) {
      val characterHeight = this.bounds(i).getHeight
      val randomY = myRandom.nextInt.toDouble % (backgroundHeight / 4.0D)
      val currentY = middleY + (if (myRandom.nextBoolean) randomY
      else -randomY) + characterHeight / 4.0D
      this.bounds(i).setRect(newX + this.bounds(i).getX, currentY, this.bounds(i).getWidth, this.bounds(i).getHeight)
    }
  }

  override def toString: String = {
    val buf = new StringBuffer
    buf.append("{text=")
    for (i <- 0 until this.length) {
      buf.append(this.aStrings(i).getIterator.current)
    }
    buf.append("\n\t")
    for (i <- 0 until this.length) {
      buf.append(this.bounds(i).toString)
      val m = this.metrics(i)
      buf.append(" ascent=").append(m.getAscent).append(" ")
      buf.append("descent=").append(m.getDescent).append(" ")
      buf.append("leading=").append(m.getLeading).append(" ")
      buf.append("\n\t")
    }
    buf.append("}")
    buf.toString
  }

  def length: Int = this.bounds.length

  def getX(index: Int): Double = this.getBounds(index).getX

  def getY(index: Int): Double = this.getBounds(index).getY

  def getHeight(index: Int): Double = this.getBounds(index).getHeight

  def getTotalWidth: Double = this.getX(this.length - 1) + this.getWidth(this.length - 1)

  def getWidth(index: Int): Double = this.getBounds(index).getWidth

  def getAscent(index: Int): Double = this.getMetric(index).getAscent.toDouble

  private def getDescent(index: Int) = this.getMetric(index).getDescent.toDouble

  def getMaxWidth: Double = {
    var maxWidth = -1.0D
    for (i <- this.bounds.indices) {
      val w = this.getWidth(i)
      if (maxWidth < w) maxWidth = w
    }
    maxWidth
  }

  def getMaxAscent: Double = {
    var maxAscent = -1.0D
    for (i <- this.bounds.indices) {
      val a = this.getAscent(i)
      if (maxAscent < a) maxAscent = a
    }
    maxAscent
  }

  def getMaxDescent: Double = {
    var maxDescent = -1.0D
    for (i <- this.bounds.indices) {
      val d = this.getDescent(i)
      if (maxDescent < d) maxDescent = d
    }
    maxDescent
  }

  def getMaxHeight: Double = {
    var maxHeight = -1.0D
    for (i <- this.bounds.indices) {
      val h = this.getHeight(i)
      if (maxHeight < h) maxHeight = h
    }
    maxHeight
  }

  def getMaxX: Double = this.getX(0) + this.getTotalWidth

  def getMaxY: Double = this.getY(0) + this.getMaxHeight

  def getBounds(index: Int): Rectangle2D = this.bounds(index)

  def getMetric(index: Int): LineMetrics = this.metrics(index)

  def getIterator(i: Int): AttributedCharacterIterator = this.aStrings(i).getIterator
}
