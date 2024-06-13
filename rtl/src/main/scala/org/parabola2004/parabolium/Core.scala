package org.parabola2004.parabolium

import chisel3._

/**
 * The Processor Core Module
 *
 * It contains the essential function units and L1-cache.
 */
class Core(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {})
}
