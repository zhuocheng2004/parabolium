package org.parabola2004.parabolium.raw

import chisel3._

/**
 * a raw module that informs the simulator of an unexpected error
 */
class ErrorRaw extends BlackBox {
  val io = IO(new Bundle {
    val error   = Input(Bool())
  })
}
