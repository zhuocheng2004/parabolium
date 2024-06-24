package org.parabola2004.parabolium

import chisel3._
import circt.stage.FirtoolOption
import org.parabola2004.parabolium.pab1.{Config, Core}
import org.parabola2004.parabolium.tile1000.Tile

/**
 * Run this to generate verilog files.
 */
object Main extends App {
  // to make verilator, iverilog, and yosys all happy
  private val firtoolOptions = Seq(FirtoolOption("--lowering-options=disallowLocalVariables,disallowPackedArrays,noAlwaysComb"))

  // for synthesis: FPGA tools or yosys
  emitVerilogForSynth()

  // for simulation: verilator
  emitVerilogForSim()

  private def emitVerilogForSynth() = {
    emitVerilog(new Tile, Array(
      "--target-dir", "generated/synth"
    ), firtoolOptions)
  }

  private def emitVerilogForSim() = {
    implicit val config: Config = Config(sim = true, resetPC = tile1000.Defines.RESET_PC)
    emitVerilog(new Core, Array(
      "--target-dir", "generated/sim"
    ), firtoolOptions)
  }
}
