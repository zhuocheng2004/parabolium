package org.parabola2004.parabolium.test

import scala.util.Random

object Helper {
  def truncateLongToU32(n: Long): Long = n & 0xFFFFFFFFL

  def castS32IntToU32Long(n: Int): Long = truncateLongToU32(n.longValue())

  def randomBool(): Boolean = Random.nextBoolean()

  def randomU5Int(): Int = Random.nextInt(1 << 5)

  def randomS32Int(): Int = Random.nextInt()

  def randomU32Long(): Long = Random.nextLong(1L << 32)
}
