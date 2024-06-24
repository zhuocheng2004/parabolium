package org.parabola2004.parabolium.pab1.idu

import chisel3._
import chisel3.util.{Fill, MuxLookup}
import org.parabola2004.parabolium.inst.OpCode
import org.parabola2004.parabolium.pab1.Defines.{ILEN, REG_ADDR_WIDTH, XLEN}

/**
 * a combinatorial module that decodes different info from an instruction
 */
class InstDecoder extends Module {
  val io = IO(new Bundle {
    val inst    = Input(UInt(ILEN.W))

    val opcode  = Output(UInt(7.W))
    val funct3  = Output(UInt(3.W))
    val funct7  = Output(UInt(7.W))
    val funct12 = Output(UInt(12.W))

    val rd      = Output(UInt(REG_ADDR_WIDTH.W))
    val rs1     = Output(UInt(REG_ADDR_WIDTH.W))
    val rs2     = Output(UInt(REG_ADDR_WIDTH.W))

    /** decoded immediate value that can be used directly as the input to ALU */
    val imm     = Output(UInt(XLEN.W))
  })

  val inst = io.inst

  io.opcode := inst(6, 0)
  io.funct3 := inst(14, 12)
  io.funct7 := inst(31, 25)
  io.funct12  := inst(31, 20)

  // The RISC-V ISA keeps the source (`rs1` and `rs2`) and destination (`rd`) registers
  // at the same position in all formats to simplify decoding.
  io.rd     := inst(11, 7)
  io.rs1    := inst(19, 15)
  io.rs2    := inst(24, 20)

  // The sign bit for all immediates is always in bit 32 of the instruction.
  val imm_sign = inst(31)

  val imm_i = Fill(21, imm_sign) ## inst(30, 20)
  val imm_s = Fill(21, imm_sign) ## inst(30, 25) ## inst(11, 7)
  val imm_b = Fill(20, imm_sign) ## inst(7) ## inst(30, 25) ## inst(11, 8) ## 0.U(1.W)
  val imm_u = inst(31, 12) ## 0.U(12.W)
  val imm_j = Fill(12, imm_sign) ## inst(19, 12) ## inst(20) ## inst(30, 21) ## 0.U(1.W)

  io.imm := MuxLookup(io.opcode, 0.U)(Seq(
    OpCode.OP_IMM   -> imm_i,
    OpCode.LUI      -> imm_u,
    OpCode.AUIPC    -> imm_u,
    OpCode.JAL      -> imm_j,
    OpCode.JALR     -> imm_i,
    OpCode.BRANCH   -> imm_b,
    OpCode.LOAD     -> imm_i,
    OpCode.STORE    -> imm_s
  ))
}
