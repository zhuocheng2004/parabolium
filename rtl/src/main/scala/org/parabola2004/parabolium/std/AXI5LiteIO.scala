package org.parabola2004.parabolium.std

import chisel3._

/*
 * Notes:
 * - During rest, manager must drive AWVALID, WVALID, and ARVALID LOW,
 *   subordinate must drive BVALID and RVALID LOW.
 */

/**
 * AXI5-Lite IO
 *
 * Note:
 *    may be incomplete,
 *    uses active-high reset.
 */
class AXI5LiteIO(addrWidth: Int = 32, dataWidth: Int) extends Bundle {
  require(addrWidth > 0)
  require(dataWidth > 0)
  require(dataWidth % 8 == 0)

  // write request channel
  val awvalid = Output(Bool())
  val awready = Input(Bool())
  val awaddr  = Output(UInt(addrWidth.W))

  // write data channel
  val wvalid  = Output(Bool())
  val wready  = Input(Bool())
  val wdata   = Output(UInt(dataWidth.W))
  val wstrb   = Output(UInt((dataWidth / 8).W))

  // write response channel
  val bvalid  = Input(Bool())
  val bready  = Output(Bool())
  val bresp   = Input(UInt(2.W))

  // read address channel
  val arvalid = Output(Bool())
  val arready = Input(Bool())
  val araddr  = Output(UInt(addrWidth.W))

  // read data channel
  val rvalid  = Input(Bool())
  val rready  = Output(Bool())
  val rdata   = Input(UInt(dataWidth.W))
  val rresp   = Input(UInt(2.W))
}

/**
 * values of bresp and rresp
 */
object AXI5LiteIO {
  val OKAY    = "b00".U
  val SLVERR  = "b10".U   // unsupported read/write; failed action; device powered down
  val DECERR  = "b11".U   // illegal address

  implicit class AddMethodsToAXI5LiteIO(io: AXI5LiteIO) {
    /** write request ready && valid */
    def aw_fire: Bool = io.awready && io.awvalid

    /** write data ready && valid */
    def w_fire: Bool  = io.wready && io.wvalid

    /** write response ready && valid */
    def b_fire: Bool  = io.bready && io.bvalid

    /** read request ready && valid */
    def ar_fire: Bool = io.arready && io.arvalid

    /** read response ready && valid */
    def r_fire: Bool  = io.rready && io.rvalid

    def setManagerDefaultOutput() = {
      io.awvalid  := false.B
      io.awaddr   := 0.U

      io.wvalid   := false.B
      io.wdata    := 0.U
      io.wstrb    := 0.U

      io.bready   := false.B

      io.arvalid  := false.B
      io.araddr   := 0.U

      io.rready   := false.B
    }

    def setSubordinateDefaultOutput() = {
      io.awready  := false.B

      io.wready   := false.B

      io.bvalid   := false.B
      io.bresp    := AXI5LiteIO.DECERR

      io.arready  := false.B

      io.rvalid   := false.B
      io.rdata    := 0.U
      io.rresp    := AXI5LiteIO.DECERR
    }
  }
}
