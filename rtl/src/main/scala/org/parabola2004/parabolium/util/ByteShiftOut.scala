package org.parabola2004.parabolium.util

import chisel3._
import chisel3.util.RegEnable

/**
 * 8-bit shift output register
 *
 * useful for serial output (UART, SPI, I2C etc.)
 */
class ByteShiftOut extends Module {
  val io = IO(new Bundle {
    // reset register (higher priority than shift)
    val set   = Input(Bool())

    // reset data
    val data  = Input(UInt(8.W))

    // synchronize right shift by one bit
    val shift = Input(Bool())

    // output bit
    val out   = Output(Bool())
  })

  val data_new = Wire(UInt(8.W))
  val data = RegEnable(data_new, io.set || io.shift)

  data_new := Mux(io.set, io.data, false.B ## data(7, 1))

  io.out := data(0)
}
