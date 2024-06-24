package org.parabola2004.parabolium.inst

import chisel3._

/**
 * funct3 constant definitions: the 3-bit data inst[14:12]
 */
object Funct3 {
  // OP-IMM/OP: arithmetic/logical operation
  val ADD     = "b000".U    // could also mean subtraction if bit funct7[5] is present
  val SLL     = "b001".U    // logical left shift
  val SLT     = "b010".U    // less-than comparison (signed)
  val SLTU    = "b011".U    // less-than comparison (unsigned)
  val XOR     = "b100".U
  val SRL     = "b101".U    // logical left right; could also mean arithmetic right shift if bit funct7[5] is present
  val OR      = "b110".U
  val AND     = "b111".U

  // BRANCH: branching
  val BEQ     = "b000".U    // branch if equal
  val BNE     = "b001".U    // branch if not equal
  val BLT     = "b100".U    // branch if less than (signed)
  val BGE     = "b101".U    // branch if greater of equal than (signed)
  val BLTU    = "b110".U    // branch if less than (unsigned)
  val BGEU    = "b111".U    // branch if greater of equal than (unsigned)

  // MISC-MEM
  val FENCE   = "b000".U
  val FENCE_I = "b001".U

  // SYSTEM
  val PRIV    = "b000".U
  val CSRRW   = "b001".U    // atomic read/write CSR
  val CSRRS   = "b010".U    // atomic read and set bits in CSR
  val CSRRC   = "b011".U    // atomic read and clear bits in CSR
  val CSRRWI  = "b101".U
  val CSRRSI  = "b110".U
  val CSRRCI  = "b111".U
}
