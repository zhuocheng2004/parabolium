package org.parabola2004.parabolium

object Defines {
  /*
   * ================================
   * basic RV32I architecture definitions
   *
   * These definitions are general for all RV32I processors.
   */

  /**
   * RV32I is 32-bit.
   */
  val XLEN              = 32

  /**
   * `XLEN = XLEN_WIDTH << 1`
   */
  val XLEN_WIDTH        = 5

  /*
   * ================================
   * chip-specific definitions
   *
   * These definitions may only be for this processor design.
   */

  /**
   * core reset value for program counter (PC)
   */
  val RESET_PC          = 0x80000000L
}
