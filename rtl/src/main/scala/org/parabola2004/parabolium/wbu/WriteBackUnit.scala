package org.parabola2004.parabolium.wbu

import chisel3._
import chisel3.util._
import org.parabola2004.parabolium.Config
import org.parabola2004.parabolium.Defines.{XLEN, XLEN_WIDTH}
import org.parabola2004.parabolium.port.MAU2WBUData
import org.parabola2004.parabolium.raw.CommitRaw

/**
 * write back unit
 */
class WriteBackUnit(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    val mau2wbu = Flipped(Decoupled(new MAU2WBUData))

    val rf_waddr  = Output(UInt(XLEN_WIDTH.W))
    val rf_wdata  = Output(UInt(XLEN.W))
    val rf_wen    = Output(Bool())

    val pc_next   = Output(UInt(XLEN.W))
    val pc_wen    = Output(Bool())

    val finish    = Decoupled()
  })

  val idle :: wait_ready :: Nil = Enum(2)
  val state = RegInit(wait_ready)
  state := MuxLookup(state, idle)(Seq(
    idle        -> Mux(io.mau2wbu.valid, wait_ready, idle),
    wait_ready  -> Mux(io.finish.ready, idle, wait_ready)
  ))

  io.mau2wbu.ready  := state === idle
  io.finish.valid   := state === wait_ready

  io.rf_waddr := io.mau2wbu.bits.rf_waddr
  io.rf_wdata := io.mau2wbu.bits.rf_wdata
  io.rf_wen   := io.mau2wbu.fire && io.mau2wbu.bits.rf_wen

  io.pc_next  := io.mau2wbu.bits.pc_next
  io.pc_wen   := io.mau2wbu.fire

  // inform simulator the commit of an instruction
  if (config.sim) {
    val commitRaw = Module(new CommitRaw)
    // we do not use finish.fire, because finish.fire is true during reset
    commitRaw.io.commit     := io.mau2wbu.fire
    commitRaw.io.commit_pc  := io.mau2wbu.bits.pc
    commitRaw.io.next_pc    := io.pc_next
    commitRaw.io.rf_wen     := io.pc_wen
    commitRaw.io.rf_waddr   := io.rf_waddr
    commitRaw.io.rf_wdata   := io.rf_wdata
  }
}
