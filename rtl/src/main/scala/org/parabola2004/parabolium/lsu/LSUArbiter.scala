package org.parabola2004.parabolium.lsu

import chisel3._
import chisel3.util._
import org.parabola2004.parabolium.std.AXI5LiteIO

/**
 * an arbiter for the accesses of IFU and MAU to LSU
 *
 * Note: read/write is handled independently. It's the user's responsibility to avoid read/write conflict.
 */
class LSUArbiter extends Module {
  val io = IO(new Bundle {
    val ifu   = Flipped(new AXI5LiteIO)
    val mau   = Flipped(new AXI5LiteIO)

    val lsu   = new AXI5LiteIO
  })

  /*
   * read arbitration
   */

  val ar_fire  = io.ifu.ar_fire || io.mau.ar_fire

  // 0 -> IFU, 1 -> MAU
  val r_chosen    = RegEnable(Mux(io.ifu.ar_fire, 0.U, 1.U), ar_fire)

  val r_idle :: wait_lsu_araddr :: wait_lsu_rdata :: r_wait_ready :: Nil = Enum(4)
  val r_state = RegInit(r_idle)
  r_state := MuxLookup(r_state, r_idle)(Seq(
    r_idle            -> Mux(ar_fire, wait_lsu_araddr, r_idle),
    wait_lsu_araddr   -> Mux(io.lsu.arready, wait_lsu_rdata, wait_lsu_araddr),
    wait_lsu_rdata    -> Mux(io.lsu.rvalid, r_wait_ready, wait_lsu_rdata),
    r_wait_ready      -> Mux(MuxLookup(r_chosen, false.B)(Seq(
      0.U   -> io.ifu.rready,
      1.U   -> io.mau.rready
    )), r_idle, r_wait_ready)
  ))

  io.ifu.arready  := r_state === r_idle
  io.mau.arready  := r_state === r_idle && !io.ifu.arvalid

  io.ifu.rvalid   := r_state === r_wait_ready && r_chosen === 0.U
  io.mau.rvalid   := r_state === r_wait_ready && r_chosen === 1.U

  io.lsu.arvalid  := r_state === wait_lsu_araddr
  io.lsu.rready   := r_state === wait_lsu_rdata

  io.lsu.araddr   := RegEnable(Mux(io.ifu.ar_fire, io.ifu.araddr, io.mau.araddr), ar_fire);

  val rdata_reg   = RegEnable(io.lsu.rdata, io.lsu.r_fire)
  val rresp_reg   = RegEnable(io.lsu.rresp, io.lsu.r_fire)
  io.ifu.rdata    := rdata_reg
  io.mau.rdata    := rdata_reg
  io.ifu.rresp    := rresp_reg
  io.mau.rresp    := rresp_reg

  /*
   * write arbitration
   */

  val aw_fire = io.ifu.aw_fire || io.mau.aw_fire

  // 0 -> IFU,  1 -> MAU
  val w_chosen    = RegEnable(Mux(io.ifu.aw_fire, 0.U, 1.U), aw_fire)

  val w_idle :: wait_lsu_awaddr :: wait_up_wdata :: wait_lsu_wdata :: wait_lsu_bresp :: w_wait_ready :: Nil = Enum(6)
  val w_state = RegInit(w_idle)
  w_state := MuxLookup(w_state, w_idle)(Seq(
    w_idle            -> Mux(aw_fire, wait_lsu_awaddr, w_idle),
    wait_lsu_awaddr   -> Mux(io.lsu.awready, wait_up_wdata, wait_lsu_awaddr),
    wait_up_wdata     -> Mux(MuxLookup(w_chosen, false.B)(Seq(
      0.U   -> io.ifu.wvalid,
      1.U   -> io.mau.wvalid
    )), wait_lsu_wdata, wait_up_wdata),
    wait_lsu_wdata    -> Mux(io.lsu.wready, wait_lsu_bresp, wait_lsu_wdata),
    wait_lsu_bresp    -> Mux(io.lsu.bvalid, w_wait_ready, wait_lsu_bresp),
    w_wait_ready      -> Mux(MuxLookup(w_chosen, false.B)(Seq(
      0.U   -> io.ifu.bready,
      1.U   -> io.mau.bready
    )), w_idle, w_wait_ready)
  ))

  io.ifu.awready  := w_state === w_idle
  io.mau.awready  := w_state === w_idle && !io.ifu.awvalid

  io.ifu.wready   := w_state === wait_lsu_wdata && w_chosen === 0.U
  io.mau.wready   := w_state === wait_lsu_wdata && w_chosen === 1.U

  io.ifu.bvalid   := w_state === w_wait_ready && w_chosen === 0.U
  io.mau.bvalid   := w_state === w_wait_ready && w_chosen === 1.U

  io.lsu.awvalid  := w_state === wait_lsu_awaddr
  io.lsu.wvalid   := w_state === wait_lsu_wdata
  io.lsu.bready   := w_state === wait_lsu_bresp

  io.lsu.awaddr   := RegEnable(Mux(io.ifu.aw_fire, io.ifu.awaddr, io.mau.awaddr), aw_fire)
  io.lsu.wdata    := MuxLookup(w_chosen, 0.U)(Seq(
    0.U   -> io.ifu.wdata,
    1.U   -> io.mau.wdata
  ))
  io.lsu.wstrb    := MuxLookup(w_chosen, 0.U)(Seq(
    0.U   -> io.ifu.wstrb,
    1.U   -> io.mau.wstrb
  ))

  val bresp_reg   = RegEnable(io.lsu.bresp, io.lsu.b_fire)
  io.ifu.bresp    := bresp_reg
  io.mau.bresp    := bresp_reg
}
