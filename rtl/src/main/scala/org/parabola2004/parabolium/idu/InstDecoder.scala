package org.parabola2004.parabolium.idu

import chisel3._
import chisel3.util.{Fill, MuxLookup}
import org.parabola2004.parabolium.Defines.{XLEN, XLEN_WIDTH}
import org.parabola2004.parabolium.inst.OpCode

/**
 * a combinatorial module that decodes different info from an instruction
 */
class InstDecoder extends Module {
  val io = IO(new Bundle {
    val inst    = Input(UInt(XLEN.W))

    val opcode  = Output(UInt(7.W))
    val funct3  = Output(UInt(3.W))
    val funct7  = Output(UInt(7.W))

    val rd      = Output(UInt(XLEN_WIDTH.W))
    val rs1     = Output(UInt(XLEN_WIDTH.W))
    val rs2     = Output(UInt(XLEN_WIDTH.W))

    /** decoded immediate value that can be used directly as input to ALU */
    val imm     = Output(UInt(XLEN.W))
  })

  val inst = io.inst

  io.opcode := inst(6, 0)
  io.funct3 := inst(14, 12)
  io.funct7 := inst(31, 25)

  io.rd     := inst(11, 7)
  io.rs1    := inst(19, 15)
  io.rs2    := inst(24, 20)

  val imm_i = inst(31, 20)
  val imm_s = inst(31, 25) ## inst(11, 7)
  val imm_b = inst(31) ## inst(7) ## inst(30, 25) ## inst(11, 8)
  val imm_u = inst(31, 12)
  val imm_j = inst(31) ## inst(19, 12) ## inst(20) ## inst(30, 21)

  val sign_extended_imm_i = Fill(20, imm_i(11)) ## imm_i
  val sign_extended_imm_s = Fill(20, imm_s(11)) ## imm_s
  val imm_u_zeros = imm_u ## 0.U(12.W)

  io.imm := MuxLookup(io.opcode, 0.U)(Seq(
    OpCode.OP_IMM   -> sign_extended_imm_i,
    OpCode.AUIPC    -> imm_u_zeros,
    OpCode.JAL      -> Fill(11, imm_j(19)) ## imm_j ## 0.U(1.W),    // extend sign, x2
    OpCode.JALR     -> sign_extended_imm_i,
    OpCode.BRANCH   -> Fill(19, imm_b(11)) ## imm_b ## 0.U(1.W),    // extend sign, x2
    OpCode.LOAD     -> sign_extended_imm_i,
    OpCode.STORE    -> sign_extended_imm_s
  ))
}
