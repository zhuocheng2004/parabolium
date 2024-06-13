package org.parabola2004.parabolium.alu

import chisel3._

/**
 * `XLEN`-wide shifter for faster simulation
 * @see [[Shifter]]
 */
class ShifterSim extends AbstractShifter {
  val logical_left_shift_result    = (io.in << io.shamt).asUInt
  val logical_right_shift_result    = (io.in >> io.shamt).asUInt
  val arithmetic_right_shift_result = (io.in.asSInt >> io.shamt).asUInt

  io.out := Mux(io.left,
    logical_left_shift_result,
    Mux(io.arith, arithmetic_right_shift_result, logical_right_shift_result))
}
