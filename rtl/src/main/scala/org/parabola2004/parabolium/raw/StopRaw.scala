package org.parabola2004.parabolium.raw

import chisel3._

/**
 * a raw module that stops simulation normally
 */
class StopRaw extends BlackBox {
  val io = IO(new Bundle {
    val stop  = Input(Bool())
  })
}
