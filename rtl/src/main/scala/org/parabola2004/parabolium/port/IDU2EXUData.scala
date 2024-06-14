package org.parabola2004.parabolium.port

import chisel3._
import org.parabola2004.parabolium.Defines.{XLEN, XLEN_WIDTH}

/**
 * data passed from IDU to EXU
 */
class IDU2EXUData extends Bundle {
  /** the instruction */
  val inst    = UInt(XLEN.W)

  /** PC of the instruction */
  val pc      = UInt(XLEN.W)

  val opcode  = UInt(7.W)

  val funct3  = UInt(3.W)

  val funct7  = UInt(7.W)

  val rd      = UInt(XLEN_WIDTH.W)

  /** decoded immediate value that can be used directly as input to ALU */
  val imm     = UInt(XLEN.W)

  /** register data 1 */
  val data1   = UInt(XLEN.W)

  /** register data 2 */
  val data2   = UInt(XLEN.W)
}
