package org.parabola2004.parabolium.pab1.idu

import chisel3._
import chisel3.util._
import org.parabola2004.parabolium.Defines.{XLEN, XLEN_WIDTH}
import org.parabola2004.parabolium.pab1.Config
import org.parabola2004.parabolium.pab1.inst.OpCode
import org.parabola2004.parabolium.pab1.port.{IDU2EXUData, IFU2IDUData}
import org.parabola2004.parabolium.raw.ErrorRaw

/**
 * instruction decode unit
 */
class InstDecodeUnit(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    val ifu2idu     = Flipped(Decoupled(new IFU2IDUData))

    val rf_raddr1   = Output(UInt(XLEN_WIDTH.W))
    val rf_raddr2   = Output(UInt(XLEN_WIDTH.W))
    val rf_rdata1   = Input(UInt(XLEN.W))
    val rf_rdata2   = Input(UInt(XLEN.W))

    val idu2exu     = Decoupled(new IDU2EXUData)
  })

  val idle :: wait_exu :: Nil = Enum(2)
  val state = RegInit(idle)
  state := MuxLookup(state, idle)(Seq(
    idle      -> Mux(io.ifu2idu.valid, wait_exu, idle),
    wait_exu  -> Mux(io.idu2exu.ready, idle, wait_exu)))

  io.ifu2idu.ready  := state === idle
  io.idu2exu.valid  := state === wait_exu

  // data from IFU
  val ifu2idu_reg = RegEnable(io.ifu2idu.bits, io.ifu2idu.fire)

  // decode inst into parts
  val decoder = Module(new InstDecoder)
  decoder.io.inst   := ifu2idu_reg.inst

  // read from register file
  io.rf_raddr1  := decoder.io.rs1
  io.rf_raddr2  := decoder.io.rs2

  // data to EXU
  val data_to_exu = Wire(new IDU2EXUData)
  data_to_exu.inst    := ifu2idu_reg.inst
  data_to_exu.pc      := ifu2idu_reg.pc
  data_to_exu.opcode  := decoder.io.opcode
  data_to_exu.funct3  := decoder.io.funct3
  data_to_exu.funct7  := decoder.io.funct7
  data_to_exu.rd      := decoder.io.rd
  data_to_exu.imm     := decoder.io.imm
  data_to_exu.data1   := io.rf_rdata1
  data_to_exu.data2   := io.rf_rdata2
  io.idu2exu.bits := data_to_exu

  // report invalid instruction to simulator
  if (config.sim) {
    val opcode = decoder.io.opcode
    val errorRaw = Module(new ErrorRaw)
    errorRaw.io.error_type := ErrorRaw.ERROR_EXU_INVALID.U
    errorRaw.io.error := state === wait_exu && !(opcode === OpCode.OP_IMM || opcode === OpCode.OP ||
      opcode === OpCode.LUI || opcode === OpCode.AUIPC ||
      opcode === OpCode.JAL || opcode === OpCode.JALR || opcode === OpCode.BRANCH ||
      opcode === OpCode.LOAD || opcode === OpCode.STORE)
    errorRaw.setDefaultInfo()
    errorRaw.io.info0 := ifu2idu_reg.pc
  }
}
