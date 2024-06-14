package org.parabola2004.parabolium.raw

import chisel3._
import org.parabola2004.parabolium.Defines.{ XLEN, XLEN_WIDTH }

/**
 * a raw module that informs the simulator of the commit of an instruction
 *
 * This is especially important for diff-test.
 */
class CommitRaw extends BlackBox {
  val io = IO(new Bundle {
    // Rising-edge of this signal means commit of an instruction
    val commit    = Input(Bool())

    // PC of the commited instruction
    val commit_pc = Input(UInt(XLEN.W))

    // PC of the next instruction
    val next_pc   = Input(UInt(XLEN.W))

    // whether the instruction writes to register file
    val rf_w      = Input(Bool())

    // write destination register
    val rf_waddr  = Input(UInt(XLEN_WIDTH.W))

    // data written to register
    val rf_wdata  = Input(UInt(XLEN.W))
  })
}
