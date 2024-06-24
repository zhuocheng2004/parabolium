package org.parabola2004.parabolium.pab1.mau

import chisel3._
import chisel3.util._
import org.parabola2004.parabolium.pab1.Config
import org.parabola2004.parabolium.pab1.Defines.XLEN
import org.parabola2004.parabolium.pab1.port.{EXU2MAUData, MAU2WBUData}
import org.parabola2004.parabolium.raw.ErrorRaw
import org.parabola2004.parabolium.std.AXI5LiteIO

/**
 * memory access unit
 */
class MemAccessUnit(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    val exu2mau   = Flipped(Decoupled(new EXU2MAUData))

    // memory access with LSU (AXI5-Lite)
    val lsu       = new AXI5LiteIO(XLEN, XLEN)

    val mau2wbu   = Decoupled(new MAU2WBUData)
  })

  io.lsu.setManagerDefaultOutput()

  val idle :: wait_lsu_waddr :: wait_lsu_wdata :: wait_lsu_wresp :: wait_lsu_raddr :: wait_lsu_rdata :: wait_wbu :: Nil = Enum(7)
  val state = RegInit(idle)
  state := MuxLookup(state, idle)(Seq(
    idle            -> Mux(io.exu2mau.valid, MuxLookup(io.exu2mau.bits.action, idle)(Seq(
      MAUAction.NONE.U    -> wait_wbu,
      MAUAction.LOAD.U    -> wait_lsu_raddr,
      MAUAction.STORE.U   -> wait_lsu_waddr
    )), idle),
    wait_lsu_raddr  -> Mux(io.lsu.arready, wait_lsu_rdata, wait_lsu_raddr),
    wait_lsu_rdata  -> Mux(io.lsu.rvalid, wait_wbu, wait_lsu_rdata),
    wait_lsu_waddr  -> Mux(io.lsu.awready, wait_lsu_wdata, wait_lsu_waddr),
    wait_lsu_wdata  -> Mux(io.lsu.wready, wait_lsu_wresp, wait_lsu_wdata),
    wait_lsu_wresp  -> Mux(io.lsu.bvalid, wait_wbu, wait_lsu_wresp)
  ))

  io.exu2mau.ready  := state === idle

  io.lsu.arvalid    := state === wait_lsu_raddr
  io.lsu.rready     := state === wait_lsu_rdata
  io.lsu.awvalid    := state === wait_lsu_waddr
  io.lsu.wvalid     := state === wait_lsu_wdata
  io.lsu.bready     := state === wait_lsu_wresp

  io.mau2wbu.valid  := state === wait_wbu

  val exu2mau_reg   = RegEnable(io.exu2mau.bits, io.exu2mau.fire)

  // build mem access action data
  val addr          = exu2mau_reg.addr
  val addr_aligned4 = addr(XLEN - 1, 2) ## 0.U(2.W)
  val addr_offset   = addr(1, 0)
  val width_shift   = exu2mau_reg.width_shift
  val width_ge2     = width_shift === 2.U || width_shift === 1.U
  val width_ge4     = width_shift === 2.U
  val wdata         = exu2mau_reg.data

  io.lsu.awaddr := addr_aligned4
  io.lsu.araddr := addr_aligned4

  // shift write data according to offset inside 4-byte range
  io.lsu.wdata := MuxLookup(addr_offset, 0.U)(Seq(
    0.U   -> wdata,
    1.U   -> wdata(23, 0) ## 0.U(8.W),
    2.U   -> wdata(15, 0) ## 0.U(16.W),
    3.U   -> wdata(7, 0) ## 0.U(24.W)
  ))

  // set corresponding write mask
  io.lsu.wstrb := MuxLookup(addr_offset, 0.U)(Seq(
    0.U   -> width_ge4 ## width_ge4 ## width_ge2 ## true.B,
    1.U   -> width_ge4 ## width_ge2 ##    true.B ## false.B,
    2.U   -> width_ge2 ##    true.B ##   false.B ## false.B,
    3.U   -> true.B    ##   false.B ##   false.B ## false.B
  ))

  val lsu_data_reg  = RegEnable(io.lsu.rdata, io.lsu.r_fire)
  // shift original data to right place
  val load_data_raw = MuxLookup(addr_offset, 0.U)(Seq(
    0.U   -> lsu_data_reg,
    1.U   -> 0.U(8.W)   ## lsu_data_reg(31, 8),
    2.U   -> 0.U(16.W)  ## lsu_data_reg(31, 16),
    3.U   -> 0.U(24.W)  ## lsu_data_reg(31, 24)
  ))
  val load_signed   = exu2mau_reg.load_signed
  // sign extend if necessary
  val load_data     = MuxLookup(width_shift, 0.U)(Seq(
    0.U   -> Fill(24, load_data_raw(7) & load_signed)  ## load_data_raw(7, 0),
    1.U   -> Fill(16, load_data_raw(15) & load_signed) ## load_data_raw(15, 0),
    2.U   -> load_data_raw
  ))

  val data_to_wbu = Wire(new MAU2WBUData)
  data_to_wbu.pc        := exu2mau_reg.pc
  data_to_wbu.rf_waddr  := exu2mau_reg.rf_waddr
  data_to_wbu.rf_wdata  := Mux(exu2mau_reg.action === MAUAction.LOAD.U, load_data, exu2mau_reg.rf_wdata)
  data_to_wbu.rf_wen    := exu2mau_reg.rf_wen
  data_to_wbu.pc_next   := exu2mau_reg.pc_next
  io.mau2wbu.bits := data_to_wbu

  val misaligned = exu2mau_reg.action =/= MAUAction.NONE.U &&
    ((width_shift === "b01".U && addr(0)) || (width_shift === "b10".U && addr(1, 0) =/= 0.U))
  val error_load = io.lsu.r_fire && io.lsu.rresp =/= AXI5LiteIO.OKAY
  val error_store = io.lsu.b_fire && io.lsu.bresp =/= AXI5LiteIO.OKAY

  // report load/store error to simulator
  if (config.sim) {
    val errorRaw = Module(new ErrorRaw)
    errorRaw.io.error_type := Mux(exu2mau_reg.action === MAUAction.NONE.U, ErrorRaw.ERROR_NONE.U, ErrorRaw.ERROR_MAU.U)
    errorRaw.io.error := misaligned || error_load || error_store
    errorRaw.setDefaultInfo()
    errorRaw.io.info0 := Mux(misaligned, 0.U, Mux(error_load, false.B, true.B) ## true.B)
    errorRaw.io.info1 := exu2mau_reg.pc
  }
}
