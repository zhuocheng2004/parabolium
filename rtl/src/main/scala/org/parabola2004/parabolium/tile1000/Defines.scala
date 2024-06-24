package org.parabola2004.parabolium.tile1000

import chisel3._

/**
 * constant definitions for Parabolium 1000 Tile
 */
object Defines {
  /**
   * core reset value for program counter (PC)
   */
  val RESET_PC          = 0x80000000L.U(32.W)


  /**
   * miscellaneous control base address
   */
  val MISC              = 0xA0001000L


  /**
   * W 1B, [[MISC]]
   *
   * write action: non-zero byte will stop simulator. No effect if not under simulation environment.
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
   * write action: set UART controller clock division register. Two bytes must be written at once.
   */
  val UART_CLK_DIV      = 0x0

  /**
   * W 1B, [[UART]]
   *
   * write action: transmit one byte data trough UART
   */
  val UART_TX           = 0x4
}
