package org.parabola2004.parabolium.pab1.inst

import chisel3._

/**
 * funct3 constant definitions: the 3-bit data inst[14:12] for RV32I instructions
 */
object Funct3 {
  // arithmetic/logical operation
  val ADD   = "b000".U    // could also mean subtraction if bit funct7[5] is present
  val SLL   = "b001".U    // logical left shift
  val SLT   = "b010".U    // less-than comparison (signed)
  val SLTU  = "b011".U    // less-than comparison (unsigned)
  val XOR   = "b100".U
  val SRL   = "b101".U    // logical left right; could also mean arithmetic right shift if bit funct7[5] is present
  val OR    = "b110".U
  val AND   = "b111".U

  // branching
  val BEQ   = "b000".U    // branch if equal
  val BNE   = "b001".U    // branch if not equal
  val BLT   = "b100".U    // branch if less than (signed)
  val BGE   = "b101".U    // branch if greater of equal than (signed)
  val BLTU  = "b110".U    // branch if less than (unsigned)
  val BGEU  = "b111".U    // branch if greater of equal than (unsigned)
}
