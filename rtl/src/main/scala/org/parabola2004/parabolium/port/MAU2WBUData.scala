package org.parabola2004.parabolium.port

import chisel3._
import org.parabola2004.parabolium.Defines.{XLEN, XLEN_WIDTH}

/**
 * data passed from MAU to WBU
 */
class MAU2WBUData extends Bundle {
  val pc            = UInt(XLEN.W)

  val rf_waddr      = UInt(XLEN_WIDTH.W)
  val rf_wdata      = UInt(XLEN.W)
  val rf_wen        = Bool()        // write back to register file?
  val pc_next       = UInt(XLEN.W)  // PC of the next instruction
}
