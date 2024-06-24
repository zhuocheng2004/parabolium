package org.parabola2004.parabolium.pab1.reg

import chisel3._
import org.parabola2004.parabolium.pab1.Defines.{REG_ADDR_WIDTH, REG_COUNT, XLEN}

/*
 * The standard software calling convention uses:
 *   `x1` to hold the return address for a call,
 *   `x2` as the stack pointer.
 *
 * TODO: use different hardware implementations for frequently accessed registers and
 *   those that are not frequently accessed, in order to reduce access energy.
 */

/**
 * register file
 *
 * It has single write port and double read ports.
 */
class RegisterFile extends Module {
  val io = IO(new Bundle {
    val raddr1  = Input(UInt(REG_ADDR_WIDTH.W))
    val raddr2  = Input(UInt(REG_ADDR_WIDTH.W))
    val rdata1  = Output(UInt(XLEN.W))
    val rdata2  = Output(UInt(XLEN.W))

    val wen     = Input(Bool())
    val waddr   = Input(UInt(REG_ADDR_WIDTH.W))
    val wdata   = Input(UInt(XLEN.W))
  })

  // `REG_COUNT` many `XLEN`-width registers
  val reg_file = Mem(REG_COUNT, UInt(XLEN.W))

  /* reading `x0` always gets zero */
  io.rdata1 := Mux(io.raddr1 === 0.U, 0.U, reg_file(io.raddr1))
  io.rdata2 := Mux(io.raddr2 === 0.U, 0.U, reg_file(io.raddr2))

  when (io.wen) {
    reg_file(io.waddr) := io.wdata
  }
}
