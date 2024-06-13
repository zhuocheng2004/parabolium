package org.parabola2004.parabolium.alu

import chisel3._
import chisel3.util.Reverse
import org.parabola2004.parabolium.Config
import org.parabola2004.parabolium.Defines.{XLEN, XLEN_WIDTH}

/**
 * `XLEN`-wide shifter
 *
 * If `left` is true, shift left, otherwise shift right.
 *
 * If `arith` is true, perform arithmetic shifting, otherwise perform logical shifting
 * (when right shifting).
 */
class Shifter(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    val in      = Input(UInt(XLEN.W))
    val shamt   = Input(UInt(XLEN_WIDTH.W))
    val left    = Input(Bool())
    val arith   = Input(Bool())

    val out     = Output(UInt(XLEN.W))
  })

  val in_reversed = Reverse(io.in)
  // re-use shift circuit
  val logical_shift_result  = (Mux(io.left, in_reversed, io.in) >> io.shamt).asUInt

  val arithmetic_right_shift_result = (io.in.asSInt >> io.shamt).asUInt

  // faster for simulation
  val logical_left_shift_result     = (io.in << io.shamt).asUInt
  val logical_right_shift_result    = (io.in >> io.shamt).asUInt

  val out_default = Wire(UInt(XLEN.W))
  out_default :=  Mux(io.left,
                    Reverse(logical_shift_result),
                    Mux(io.arith, arithmetic_right_shift_result, logical_shift_result))

  val out_faster = Wire(UInt(XLEN.W))
  out_faster  :=  Mux(io.left,
                    logical_left_shift_result,
                    Mux(io.arith, arithmetic_right_shift_result, logical_right_shift_result))

  // formal verification
  assert(out_faster === out_default)

  if (config.sim) {
    io.out  := out_faster
  } else {
    io.out  := out_default
  }
}
