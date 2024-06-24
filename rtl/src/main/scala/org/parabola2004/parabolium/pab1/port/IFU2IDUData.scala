package org.parabola2004.parabolium.pab1.port

import chisel3._
import org.parabola2004.parabolium.pab1.Defines.{ILEN, XLEN}

/**
 * data passed from IFU to IDU
 */
class IFU2IDUData extends Bundle {
  /** the fetched instruction */
  val inst  = UInt(ILEN.W)

  /** PC of the fetched instruction */
  val pc    = UInt(XLEN.W)
}
