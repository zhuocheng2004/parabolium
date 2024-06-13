package org.parabola2004.parabolium.std

import chisel3._

/**
 * AXI5-Lite (almost compatible) IO
 *
 * Note:
 *    may be incomplete,
 *    uses active-high reset.
 */
class AXI5LiteIO(addrWidth: Int, dataWidth: Int) extends Bundle {
  require(dataWidth % 8 == 0)

  // Write Address Channel Signals
  val awaddr  = Output(UInt(addrWidth.W))
  val awvalid = Output(Bool())
  val awready = Input(Bool())

  // Write Data Channel Signals
  val wdata   = Output(UInt(dataWidth.W))
  val wstrb   = Output(UInt((dataWidth / 8).W))
  val wvalid  = Output(Bool())
  val wready  = Input(Bool())

  // Write Response Channel Signals
  val bresp   = Input(UInt(2.W))
  val bvalid  = Input(Bool())
  val bready  = Output(Bool())

  // Read Address Channel Signals
  val araddr  = Output(UInt(addrWidth.W))
  val arvalid = Output(Bool())
  val arready = Input(Bool())

  // Read Data Channel Signals
  val rdata   = Input(UInt(dataWidth.W))
  val rresp   = Input(UInt(2.W))
  val rvalid  = Input(Bool())
  val rready  = Output(Bool())
}

object AXI5LiteIO {
  val OKAY    = "b00".U
  val SLVERR  = "b10".U
  val a       = "b11".U
}
