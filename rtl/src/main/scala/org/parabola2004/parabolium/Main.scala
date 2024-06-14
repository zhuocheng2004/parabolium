package org.parabola2004.parabolium

import chisel3._
import circt.stage.FirtoolOption
import org.parabola2004.parabolium.idu.InstDecodeUnit

object Main extends App {
  // to make verilator, iverilog, and yosys all happy
  private val firtoolOptions = Seq(FirtoolOption("--lowering-options=disallowLocalVariables,disallowPackedArrays,noAlwaysComb"))

  // for synthesis: yosys
  emitVerilogForSynth()

  // for simulation: verilator and iverilog
  emitVerilogForSim()

  println(getVerilogString(new InstDecodeUnit))

  private def emitVerilogForSynth() = {
    implicit val config: Config = Config()
    emitVerilog(new Core, Array(
      "--target-dir", "generated/synth"
    ), firtoolOptions)
  }

  private def emitVerilogForSim() = {
    implicit val config: Config = Config(sim = true)
    emitVerilog(new Core, Array(
      "--target-dir", "generated/sim"
    ), firtoolOptions)
  }
}
