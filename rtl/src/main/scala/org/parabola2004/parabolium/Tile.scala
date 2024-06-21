package org.parabola2004.parabolium

import chisel3._
import chisel3.util.RegEnable
import org.parabola2004.parabolium.mem.CrossBar
import org.parabola2004.parabolium.pab1.Core
import org.parabola2004.parabolium.perip.UARTControl
import org.parabola2004.parabolium.std.AXI5LiteIO

/**
 * The SoC tile with PAB1 core, L2/L3 caches, XBar, peripherals.
 *
 * This module is synthesizable.
 */
class Tile extends Module {
  val io = IO(new Bundle {
    // MEM access
    val ram     = new AXI5LiteIO

    // 8-bit LED data output
    val led     = Output(UInt(8.W))

    // UART TX signal output
    val uart_tx = Output(Bool())
  })

  // PAB1 core
  val core = Module(new Core)

  // cross bar
  val xbar = Module(new CrossBar)

  // core and xbar
  core.io.mem <> xbar.io.up

  // external RAM
  xbar.io.ram <> io.ram

  // xbar and LED output
  io.led := RegEnable(xbar.io.led, 0.U(8.W), xbar.io.led_en)

  // UART controller
  val uart_ctrl = Module(new UARTControl)

  uart_ctrl.io.tx_data <> xbar.io.uart_tx_data

  uart_ctrl.io.clk_div      := xbar.io.uart_clk_div
  uart_ctrl.io.clk_div_en   := xbar.io.uart_clk_div_en

  io.uart_tx                := uart_ctrl.io.tx
}
