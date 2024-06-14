package org.parabola2004.parabolium.port

import chisel3._
import org.parabola2004.parabolium.Defines.XLEN

/**
 * data passed from IFU to IDU
 */
class IFU2IDUData extends Bundle {
  /** the fetched instruction */
  val inst  = UInt(XLEN.W)

  /** PC of the fetched instruction */
  val pc    = UInt(XLEN.W)
}
