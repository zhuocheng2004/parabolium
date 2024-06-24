package org.parabola2004.parabolium.pab1.lsu

import chisel3._
import org.parabola2004.parabolium.pab1.Defines.XLEN
import org.parabola2004.parabolium.std.AXI5LiteIO

/**
 * the load-store unit
 *
 * only supports aligned data read/write
 *
 * It transforms virtual address to physical address.
 */
class LoadStoreUnit extends Module {
  val io = IO(new Bundle {
    // load/store request from upstream
    val up    = Flipped(new AXI5LiteIO(XLEN, XLEN))

    // load/store request to downstream, using physical address
    val down  = new AXI5LiteIO(XLEN, XLEN)
  })

  // TODO: MMU support
  io.up <> io.down
}
