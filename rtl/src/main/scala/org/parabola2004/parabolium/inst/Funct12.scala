package org.parabola2004.parabolium.inst

import chisel3._

/**
 * funct12 constant definitions: the 12-bit data inst[31:20]
 */
object Funct12 {
  // SYSTEM
  val ECALL   = 0x000.U(12.W)
  val EBREAK  = 0x001.U(12.W)
}
