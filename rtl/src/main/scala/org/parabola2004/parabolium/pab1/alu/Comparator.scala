package org.parabola2004.parabolium.pab1.alu

import chisel3._
import org.parabola2004.parabolium.pab1.Defines.XLEN

/**
 * the core part of [[Comparator]]
 *
 * It outputs whether `in1 < in2`
 *
 * If `signed` is true, perform signed comparison (i.e. view inputs as signed numbers),
 * otherwise perform unsigned comparison.
 *
 * Comparison of two inputs can be determined just by using their MSBs
 * and the adder output when subtracting them.
 *
 * Sometimes an adder is already present, so we can re-use the adder with this module.
 */
class ComparatorCore extends Module {
  val io = IO(new Bundle {
    val in1_sign  = Input(Bool())   // sign bit of in1
    val in2_sign  = Input(Bool())   // sign bit of in2
    val carry     = Input(Bool())   // carry output of adder when computing (in1 - in2)
    val signed    = Input(Bool())   // whether we are performing signed comparison

    val lt        = Output(Bool())  // whether in1 < in2
  })

  io.lt :=  Mux(io.signed,
              Mux(io.in1_sign === io.in2_sign, !io.carry, io.in1_sign),
              !io.carry)
}

/**
 * a comparator that outputs whether `in1 == in2` and whether `in1 < in2`
 *
 * If `signed` is true, perform signed comparison (i.e. view inputs as signed numbers),
 * otherwise perform unsigned comparison.
 */
class Comparator extends Module {
  val io = IO(new Bundle {
    val in1     = Input(UInt(XLEN.W))
    val in2     = Input(UInt(XLEN.W))
    val signed  = Input(Bool())         // whether we are performing signed comparison

    val eq      = Output(Bool())        // whether in1 == in2
    val lt      = Output(Bool())        // whether in1 < in2
  })

  val adder = Module(new Adder)
  adder.io.sub := true.B
  adder.io.in1 := io.in1
  adder.io.in2 := io.in2
  val diff  = adder.io.sum
  val carry = adder.io.carry

  val cmp_core = Module(new ComparatorCore)
  cmp_core.io.in1_sign  := io.in1(XLEN - 1)
  cmp_core.io.in2_sign  := io.in2(XLEN - 1)
  cmp_core.io.carry     := carry
  cmp_core.io.signed    := io.signed

  io.eq := diff === 0.U
  io.lt := cmp_core.io.lt
}
