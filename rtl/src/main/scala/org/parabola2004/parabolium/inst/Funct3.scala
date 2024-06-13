package org.parabola2004.parabolium.inst

import chisel3._

/**
 * Funct3 constant definitions: the 3-bit data inst[14:12] for RV32I instructions
 */
object Funct3 {
  // arithmetic/logical operation
  val ADD   = "b000".U    // could also be subtraction if bit funct7[5] is present
  val SLL   = "b001".U
  val SLT   = "b010".U
  val SLTU  = "b011".U
  val XOR   = "b100".U
  val SRL   = "b101".U    // could also be arithmetic right shift if bit funct7[5] is present
  val OR    = "b110".U
  val AND   = "b111".U

  // branching
  val BEQ   = "b000".U
  val BNE   = "b001".U
  val BLT   = "b100".U
  val BGE   = "b101".U
  val BLTU  = "b110".U
  val BGEU  = "b111".U
}
