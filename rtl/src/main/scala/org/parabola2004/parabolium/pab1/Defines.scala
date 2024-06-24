package org.parabola2004.parabolium.pab1

/**
 * constant definitions for Pab1 core
 *
 * Pab1 core uses RV32I Base Integer Instruction Set
 */
object Defines {
  /**
   * instruction length
   */
  val ILEN              = 32

  /**
   *  x register width (also address width)
   */
  val XLEN              = 32

  /**
   * `XLEN` = `1 << XLEN_SHIFT`
   */
  val XLEN_SHIFT        = 5

  /**
   * x register count
   */
  val REG_COUNT         = 32

  /**
   * x register address width
   */
  val REG_ADDR_WIDTH    = 5
}
