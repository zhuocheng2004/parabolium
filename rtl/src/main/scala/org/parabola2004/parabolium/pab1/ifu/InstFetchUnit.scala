package org.parabola2004.parabolium.pab1.ifu

import chisel3._
import chisel3.util._
import org.parabola2004.parabolium.pab1.Config
import org.parabola2004.parabolium.pab1.Defines.{ILEN, XLEN}
import org.parabola2004.parabolium.pab1.port.IFU2IDUData
import org.parabola2004.parabolium.raw.ErrorRaw
import org.parabola2004.parabolium.std.AXI5LiteIO

/**
 * instruction fetch unit
 */
class InstFetchUnit(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    // action signal for fetching instruction
    val action    = Flipped(Decoupled())

    // PC to fetch instruction
    val pc        = Input(UInt(XLEN.W))

    // memory access with LSU (AXI5-Lite)
    val lsu       = new AXI5LiteIO(XLEN, ILEN)

    // data passed to IDU
    val ifu2idu   = Decoupled(new IFU2IDUData)
  })

  io.lsu.setManagerDefaultOutput()

  val idle :: wait_lsu_addr :: wait_lsu_data :: wait_idu :: Nil = Enum(4)
  val state = RegInit(idle)
  state := MuxLookup(state, idle)(Seq(
    idle            -> Mux(io.action.valid, wait_lsu_addr, idle),
    wait_lsu_addr   -> Mux(io.lsu.arready, wait_lsu_data, wait_lsu_addr),
    wait_lsu_data   -> Mux(io.lsu.rvalid, wait_idu, wait_lsu_data),
    wait_idu        -> Mux(io.ifu2idu.ready, idle, wait_idu)))

  io.action.ready   := state === idle
  io.lsu.arvalid    := state === wait_lsu_addr
  io.lsu.rready     := state === wait_lsu_data
  io.ifu2idu.valid  := state === wait_idu

  // PC to LSU
  io.lsu.araddr     := io.pc

  // data from LSU
  val lsu_data  = RegEnable(io.lsu.rdata, io.lsu.r_fire)

  val inst_addr_misaligned = io.pc(1, 0) =/= "b00".U

  // report unsuccessful read to simulator
  if (config.sim) {
    val errorRaw = Module(new ErrorRaw)
    errorRaw.io.error_type := ErrorRaw.ERROR_IFU.U
    errorRaw.io.error := (io.action.fire && inst_addr_misaligned) || (io.lsu.r_fire && io.lsu.rresp =/= AXI5LiteIO.OKAY)
    errorRaw.setDefaultInfo()
    errorRaw.io.info0 := Mux(inst_addr_misaligned, 0.U, 1.U)
    errorRaw.io.info1 := io.pc
  }

  // data to IDU
  io.ifu2idu.bits.inst  := lsu_data
  io.ifu2idu.bits.pc    := io.pc
}
