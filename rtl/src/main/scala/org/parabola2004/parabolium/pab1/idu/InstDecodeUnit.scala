package org.parabola2004.parabolium.pab1.idu

import chisel3._
import chisel3.util._
import org.parabola2004.parabolium.inst.{Funct12, Funct3, OpCode}
import org.parabola2004.parabolium.pab1.Config
import org.parabola2004.parabolium.pab1.Defines.{REG_ADDR_WIDTH, XLEN}
import org.parabola2004.parabolium.pab1.port.{IDU2EXUData, IFU2IDUData}
import org.parabola2004.parabolium.raw.ErrorRaw


/**
 * instruction decode unit
 */
class InstDecodeUnit(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    val ifu2idu     = Flipped(Decoupled(new IFU2IDUData))

    val rf_raddr1   = Output(UInt(REG_ADDR_WIDTH.W))
    val rf_raddr2   = Output(UInt(REG_ADDR_WIDTH.W))
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

  val inst = ifu2idu_reg.inst

  // decode inst into parts
  val decoder = Module(new InstDecoder)
  decoder.io.inst   := inst

  val opcode  = decoder.io.opcode
  val funct3  = decoder.io.funct3
  val funct7  = decoder.io.funct7
  val funct12 = decoder.io.funct12

  // read from register file
  io.rf_raddr1  := decoder.io.rs1
  io.rf_raddr2  := decoder.io.rs2

  // data to EXU
  val data_to_exu = Wire(new IDU2EXUData)
  data_to_exu.inst    := inst
  data_to_exu.pc      := ifu2idu_reg.pc
  data_to_exu.opcode  := opcode
  data_to_exu.funct3  := funct3
  data_to_exu.funct7  := funct7
  data_to_exu.funct12 := funct12
  data_to_exu.rd      := decoder.io.rd
  data_to_exu.imm     := decoder.io.imm
  data_to_exu.data1   := io.rf_rdata1
  data_to_exu.data2   := io.rf_rdata2
  io.idu2exu.bits := data_to_exu

  // report invalid instruction to simulator
  if (config.sim) {
    val invalid_opcode = !(opcode === OpCode.OP_IMM || opcode === OpCode.OP ||
      opcode === OpCode.LUI || opcode === OpCode.AUIPC ||
      opcode === OpCode.JAL || opcode === OpCode.JALR || opcode === OpCode.BRANCH ||
      opcode === OpCode.LOAD || opcode === OpCode.STORE ||
      opcode === OpCode.MISC_MEM || opcode === OpCode.SYSTEM)

    val invalid_shift_imm = opcode === OpCode.OP_IMM &&
      (funct3 === Funct3.SLL || funct3 === Funct3.SRL) && (inst(31) || inst(29, 25) =/= 0.U)

    val invalid_funct7 = opcode === OpCode.OP && (funct7(6) || funct7(4, 0) =/= 0.U ||
      (funct7(5) && funct3 =/= Funct3.ADD && funct3 =/= Funct3.SRL))

    val invalid_load_store_funct3 = (opcode === OpCode.LOAD || opcode === OpCode.STORE) &&
      (funct3(1, 0) === "b11".U || funct3 === "b110".U /* no LWU instruction */)

    val invalid_system = opcode === OpCode.SYSTEM &&
      (/*funct12 =/= Funct12.ECALL || */funct12 =/= Funct12.EBREAK ||
        (funct3 =/= Funct3.PRIV || decoder.io.rs1 =/= 0.U || decoder.io.rd =/= 0.U))

    val invalid_misc_mem = opcode === OpCode.MISC_MEM &&
      !(funct3 === Funct3.FENCE || funct3 === Funct3.FENCE_I)

    val errorRaw = Module(new ErrorRaw)
    errorRaw.io.error_type := ErrorRaw.ERROR_IDU.U
    errorRaw.io.error := state === wait_exu &&
      (invalid_opcode || invalid_shift_imm || invalid_funct7 || invalid_load_store_funct3 ||
        invalid_system || invalid_misc_mem)
    errorRaw.setDefaultInfo()
    errorRaw.io.info1 := ifu2idu_reg.pc
  }
}
