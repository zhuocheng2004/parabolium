package org.parabola2004.parabolium.alu

import chisel3._
import chisel3.util.MuxLookup
import org.parabola2004.parabolium.Config
import org.parabola2004.parabolium.Defines.{XLEN, XLEN_WIDTH}
import org.parabola2004.parabolium.inst.Funct3

/**
 * `XLEN`-wide ALU for RV32I
 *
 * `funct3` is the 3-bit data inst[14:12] decoded from an RV32I instruction
 *
 * `sub` and `shift_arith` can be got from `funct7`
 */
class ALU(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    val in1           = Input(UInt(XLEN.W))
    val in2           = Input(UInt(XLEN.W))
    val funct3        = Input(UInt(3.W))      // use funct3 to choose operation
    val sub           = Input(Bool())         // subtraction instead of addition?
    val shift_arith   = Input(Bool())         // arithmetic right shift?

    val out           = Output(UInt(XLEN.W))
  })

  val adder = Module(new Adder)
  adder.io.in1 := io.in1
  adder.io.in2 := io.in2
  adder.io.sub := MuxLookup(io.funct3, false.B)(Seq(
    Funct3.ADD    -> io.sub,    // funct3 `ADD` could be subtraction if bit funct7[5] is present
    Funct3.SLT    -> true.B,    // comparison need subtraction
    Funct3.SLTU   -> true.B
  ))

  val cmp = Module(new ComparatorCore)
  cmp.io.in1_sign := io.in1(XLEN - 1)
  cmp.io.in2_sign := io.in2(XLEN - 1)
  cmp.io.signed   := !io.funct3(0)
  cmp.io.carry    := adder.io.carry

  val shifter = if (config.sim) Module(new ShifterSim) else Module(new Shifter)
  shifter.io.in     := io.in1
  shifter.io.shamt  := io.in2(XLEN_WIDTH - 1, 0)
  shifter.io.left   := !io.funct3(2)
  shifter.io.arith  := io.shift_arith

  io.out := MuxLookup(io.funct3, 0.U)(Seq(
    Funct3.ADD    -> adder.io.sum,
    Funct3.SLL    -> shifter.io.out,
    Funct3.SLT    -> cmp.io.lt,
    Funct3.SLTU   -> cmp.io.lt,
    Funct3.XOR    -> (io.in1 ^ io.in2),
    Funct3.SRL    -> shifter.io.out,
    Funct3.OR     -> (io.in1 | io.in2),
    Funct3.AND    -> (io.in1 & io.in2)
  ))
}
