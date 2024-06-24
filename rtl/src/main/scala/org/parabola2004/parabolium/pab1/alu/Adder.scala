package org.parabola2004.parabolium.pab1.alu

import chisel3._
import org.parabola2004.parabolium.pab1.Defines.XLEN

/**
 * `XLEN`-wide addition/subtraction module
 *
 * If `sub` is `false`, `sum` is `in1 + in2`;
 * if `sub` is true, `sum` is `in1 + ~in2 + 1`.
 */
class Adder extends Module {
  val io = IO(new Bundle {
    val in1   = Input(UInt(XLEN.W))
    val in2   = Input(UInt(XLEN.W))
    val sub   = Input(Bool())

    val sum   = Output(UInt(XLEN.W))
    val carry = Output(Bool())
  })

  val sum_with_carry = io.in1 +& Mux(io.sub, (~io.in2).asUInt, io.in2) +& io.sub

  io.sum    := sum_with_carry(XLEN - 1, 0)
  io.carry  := sum_with_carry(XLEN)
}
