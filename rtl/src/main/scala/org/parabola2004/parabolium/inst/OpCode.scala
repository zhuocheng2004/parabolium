package org.parabola2004.parabolium.inst

import chisel3._

/**
 * opcode constant definitions: the 7-bit data inst[6:0]
 */
object OpCode {
  val OP_IMM    = "b0010011".U    // integer register-immediate
  val OP        = "b0110011".U    // integer register-register
  val LUI       = "b0110111".U    // (Load Upper Immediate)
  val AUIPC     = "b0010111".U    // (Add Upper Immediate to PC)
  val JAL       = "b1101111".U    // direct unconditional jump (Jump And Link)
  val JALR      = "b1100111".U    // indirect unconditional jump (Jump And Link Register)
  val BRANCH    = "b1100011".U    // conditional branch
  val LOAD      = "b0000011".U
  val STORE     = "b0100011".U
  val MISC_MEM  = "b0001111".U
  val SYSTEM    = "b1110011".U
}
