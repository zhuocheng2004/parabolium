package org.parabola2004.parabolium.alu

import chisel3._
import chisel3.util.Reverse
import org.parabola2004.parabolium.Defines.{XLEN, XLEN_WIDTH}

abstract class AbstractShifter extends Module {
  val io = IO(new Bundle {
    val in      = Input(UInt(XLEN.W))
    val shamt   = Input(UInt(XLEN_WIDTH.W))
    val left    = Input(Bool())
    val arith   = Input(Bool())

    val out     = Output(UInt(XLEN.W))
  })
}

/**
 * `XLEN`-wide shifter
 *
 * If `left` is true, shift left, otherwise shift right.
 *
 * If `arith` is true, perform arithmetic shifting, otherwise perform logical shifting
 * (when right shifting).
 */
class Shifter extends AbstractShifter {
  val in_reversed = Reverse(io.in)
  // re-use shift circuit
  val logical_right_shift_result    = (Mux(io.left, in_reversed, io.in) >> io.shamt).asUInt
  val arithmetic_right_shift_result = (io.in.asSInt >> io.shamt).asUInt

  io.out := Mux(io.left,
              Reverse(logical_right_shift_result),
              Mux(io.arith, arithmetic_right_shift_result, logical_right_shift_result))
}
