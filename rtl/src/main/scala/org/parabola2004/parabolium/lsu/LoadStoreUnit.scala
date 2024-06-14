package org.parabola2004.parabolium.lsu

import chisel3._
import org.parabola2004.parabolium.std.AXI5LiteIO

/**
 * the load-store unit
 *
 * It transforms virtual address to physical address.
 */
class LoadStoreUnit extends Module {
  val io = IO(new Bundle {
    // load/store request from upstream
    val up    = Flipped(new AXI5LiteIO())

    // load/store request to downstream, using physical address
    val down  = new AXI5LiteIO()
  })

  // TODO: MMU support
  io.up <> io.down
}
