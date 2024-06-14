package org.parabola2004.parabolium

import chisel3._
import chisel3.util.RegEnable
import org.parabola2004.parabolium.Defines.RESET_PC
import org.parabola2004.parabolium.exu.ExecuteUnit
import org.parabola2004.parabolium.idu.InstDecodeUnit
import org.parabola2004.parabolium.ifu.InstFetchUnit
import org.parabola2004.parabolium.lsu.{LSUArbiter, LoadStoreUnit}
import org.parabola2004.parabolium.mau.MemAccessUnit
import org.parabola2004.parabolium.mem.CrossBar
import org.parabola2004.parabolium.perip.UARTControl
import org.parabola2004.parabolium.reg.RegisterFile
import org.parabola2004.parabolium.std.AXI5LiteIO
import org.parabola2004.parabolium.wbu.WriteBackUnit

/**
 * The Processor Core Module
 *
 * It contains the essential function units (TODO: and L1-cache).
 */
class Core(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    // RAM access (AXI5-Lite)
    val ram     = new AXI5LiteIO

    // 8-bit LED output data
    val led     = Output(UInt(8.W))

    // LED output change enable
    val led_en  = Output(Bool())

    // UART TX signal output
    val uart_tx = Output(Bool())
  })

  val ifu = Module(new InstFetchUnit)
  val idu = Module(new InstDecodeUnit)
  val exu = Module(new ExecuteUnit)
  val mau = Module(new MemAccessUnit)
  val wbu = Module(new WriteBackUnit)

  val lsu_arbiter = Module(new LSUArbiter)

  val lsu = Module(new LoadStoreUnit)

  val xbar = Module(new CrossBar)

  val reg_file = Module(new RegisterFile)

  val pc = RegEnable(wbu.io.pc_next, RESET_PC.U, wbu.io.pc_wen)
  ifu.io.pc := pc

  ifu.io.ifu2idu <> idu.io.ifu2idu
  idu.io.idu2exu <> exu.io.idu2exu
  exu.io.exu2mau <> mau.io.exu2mau
  mau.io.mau2wbu <> wbu.io.mau2wbu

  // start of instruction fetch is immediately after WBU finish
  wbu.io.finish <> ifu.io.action

  // IDU & reg file
  reg_file.io.raddr1  := idu.io.rf_raddr1
  reg_file.io.raddr2  := idu.io.rf_raddr2
  idu.io.rf_rdata1    := reg_file.io.rdata1
  idu.io.rf_rdata2    := reg_file.io.rdata2

  // WBU & reg file
  reg_file.io.wen     := wbu.io.rf_wen
  reg_file.io.waddr   := wbu.io.rf_waddr
  reg_file.io.wdata   := wbu.io.rf_wdata

  // IFU, MAU & LSU arbiter
  ifu.io.lsu <> lsu_arbiter.io.ifu
  mau.io.lsu <> lsu_arbiter.io.mau

  // LSU arbiter & LSU
  lsu_arbiter.io.lsu <> lsu.io.up

  // LSU & XBar
  lsu.io.down <> xbar.io.up

  // XBar & RAM
  xbar.io.ram <> io.ram

  // XBar & LED
  io.led    := xbar.io.led
  io.led_en := xbar.io.led_en

  // XBar & UART
  val uart_control = Module(new UARTControl)
  uart_control.io.clk_div     := xbar.io.uart_clk_div
  uart_control.io.clk_div_en  := xbar.io.uart_clk_div_en
  xbar.io.uart_tx_data <> uart_control.io.tx_data
  io.uart_tx  := uart_control.io.tx
}
