package org.parabola2004.parabolium.pab1.port

import chisel3._
import org.parabola2004.parabolium.pab1.Defines.{REG_ADDR_WIDTH, XLEN}

/**
 * data passed from EXU to MAU
 */
class EXU2MAUData extends Bundle {
  val pc            = UInt(XLEN.W)

  val action        = UInt(2.W)     // MAU action. See [[MAUAction]]
  val addr          = UInt(XLEN.W)  // load/store address
  val width_shift   = UInt(2.W)     // (1 << width_shift) is the length (in bytes) of written data
  val load_signed   = Bool()        // should we extend sign of the read data (when read length (in bytes) < 4)
  val data          = UInt(XLEN.W)  // data to store

  // these will be passed to WBU
  val rf_waddr      = UInt(REG_ADDR_WIDTH.W)
  val rf_wdata      = UInt(XLEN.W)
  val rf_wen        = Bool()        // write back to register file?
  val pc_next       = UInt(XLEN.W)  // PC of the next instruction
}
