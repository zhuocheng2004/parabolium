package org.parabola2004.parabolium.pab1.reg

import chisel3._
import org.parabola2004.parabolium.Defines.{XLEN, XLEN_WIDTH}

/**
 * register file for RV32I
 */
class RegisterFile extends Module {
  val io = IO(new Bundle {
    val raddr1  = Input(UInt(XLEN_WIDTH.W))
    val raddr2  = Input(UInt(XLEN_WIDTH.W))
    val rdata1  = Output(UInt(XLEN.W))
    val rdata2  = Output(UInt(XLEN.W))

    val wen     = Input(Bool())
    val waddr   = Input(UInt(XLEN_WIDTH.W))
    val wdata   = Input(UInt(XLEN.W))
  })

  val reg_file = Mem(XLEN, UInt(XLEN.W))

  io.rdata1 := Mux(io.raddr1 === 0.U, 0.U, reg_file(io.raddr1))
  io.rdata2 := Mux(io.raddr2 === 0.U, 0.U, reg_file(io.raddr2))

  when (io.wen) {
    reg_file(io.waddr) := io.wdata
  }
}
