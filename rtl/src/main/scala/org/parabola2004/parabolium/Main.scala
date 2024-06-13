package org.parabola2004.parabolium

import chisel3._
import circt.stage.FirtoolOption
import org.parabola2004.parabolium.alu.ALU

object Main extends App {
  // to make verilator, iverilog, and yosys all happy
  private val firtoolOptions = Seq(FirtoolOption("--lowering-options=disallowLocalVariables,disallowPackedArrays,noAlwaysComb"))

  // for synthesis: yosys
  emitVerilogForSynth()

  // for simulation: verilator and iverilog
  emitVerilogForSim()

  implicit val config: Config = Config()
  println(getVerilogString(new ALU))

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
