package org.parabola2004.parabolium

/**
 * constant definitions for RV32I and this core
 */
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


  /**
   * miscellaneous control base address
   */
  val MISC              = 0xA0001000L


  /**
   * W 1B, [[MISC]]
   *
   * write action: non-zero byte stops simulator, no effect otherwise
   */
  val STOP              = 0x0

  /**
   * W 1B, [[MISC]]
   *
   * write action: 8-bit LED output
   */
  val LED               = 0x4


  /**
   * UART control base address
   */
  val UART              = 0xA0002000L

  /**
   * W 2B, [[UART]]
   *
   * write action: set UART controller clock division register
   */
  val UART_CLK_DIV      = 0x0

  /**
   * W 1B, [[UART]]
   *
   * write action: transmit one byte data trough UART
   */
  val UART_TX           = 0x4
}
