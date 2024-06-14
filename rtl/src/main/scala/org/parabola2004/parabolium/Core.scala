package org.parabola2004.parabolium

import chisel3._

/**
 * The Processor Core Module
 *
 * It contains the essential function units and L1-cache.
 */
class Core(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    // 8-bit LED output data
    val led     = Output(UInt(8.W))

    // LED output enable
    val led_en  = Output(Bool())
  })

  io.led      := 0.U
  io.led_en   := false.B
}
