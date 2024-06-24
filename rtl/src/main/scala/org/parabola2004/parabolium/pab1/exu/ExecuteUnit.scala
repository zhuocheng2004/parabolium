package org.parabola2004.parabolium.pab1.exu

import chisel3._
import chisel3.util._
import org.parabola2004.parabolium.inst.{Funct12, Funct3, OpCode}
import org.parabola2004.parabolium.pab1.Config
import org.parabola2004.parabolium.pab1.Defines.XLEN
import org.parabola2004.parabolium.pab1.alu.{ALU, Comparator}
import org.parabola2004.parabolium.pab1.mau.MAUAction
import org.parabola2004.parabolium.pab1.port.{EXU2MAUData, IDU2EXUData}
import org.parabola2004.parabolium.raw.{ErrorRaw, StopRaw}

/**
 * execution unit
 */
class ExecuteUnit(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    val idu2exu   = Flipped(Decoupled(new IDU2EXUData))

    val exu2mau   = Decoupled(new EXU2MAUData)
  })

  val idle :: wait_mau :: Nil = Enum(2)
  val state = RegInit(idle)
  state := MuxLookup(state, idle)(Seq(
    idle      -> Mux(io.idu2exu.valid, wait_mau, idle),
    wait_mau  -> Mux(io.exu2mau.ready, idle, wait_mau)))

  io.idu2exu.ready  := state === idle
  io.exu2mau.valid  := state === wait_mau

  val idu2exu_reg = RegEnable(io.idu2exu.bits, io.idu2exu.fire)
  val pc      = idu2exu_reg.pc
  val opcode  = idu2exu_reg.opcode
  val funct3  = idu2exu_reg.funct3
  val funct7  = idu2exu_reg.funct7
  val funct12 = idu2exu_reg.funct12
  val imm     = idu2exu_reg.imm
  val data1   = idu2exu_reg.data1
  val data2   = idu2exu_reg.data2

  // arithmetic/logical computation
  val alu = Module(new ALU)
  alu.io.in1 := MuxLookup(opcode, data1)(Seq(
    OpCode.AUIPC    -> pc,
    OpCode.JAL      -> pc,
    OpCode.BRANCH   -> pc
  ))
  alu.io.in2 := MuxLookup(opcode, data2)(Seq(
    OpCode.OP_IMM   -> imm,
    OpCode.AUIPC    -> imm,
    OpCode.JAL      -> imm,
    OpCode.JALR     -> imm,
    OpCode.BRANCH   -> imm,
    OpCode.LOAD     -> imm,
    OpCode.STORE    -> imm
  ))
  alu.io.funct3       := Mux(opcode === OpCode.OP_IMM || opcode === OpCode.OP, funct3, Funct3.ADD)
  alu.io.sub          := Mux(opcode === OpCode.OP, funct7(5), false.B)
  alu.io.shift_arith  := funct7(5)
  val alu_out = alu.io.out

  // branch processing
  val cmp = Module(new Comparator)
  cmp.io.in1    := data1
  cmp.io.in2    := data2
  cmp.io.signed := !funct3(1)
  val eq = cmp.io.eq
  val lt = cmp.io.lt

  // branch or not
  val branch = MuxLookup(funct3, false.B)(Seq(
    Funct3.BEQ    -> eq,
    Funct3.BNE    -> !eq,
    Funct3.BLT    -> lt,
    Funct3.BLTU   -> lt,
    Funct3.BGE    -> !lt,
    Funct3.BGEU   -> !lt
  ))

  // data to MAU
  val data_to_mau = Wire(new EXU2MAUData)
  data_to_mau.pc    := pc

  data_to_mau.action  := MuxLookup(opcode, MAUAction.NONE.U)(Seq(
    OpCode.LOAD   -> MAUAction.LOAD.U,
    OpCode.STORE  -> MAUAction.STORE.U
  ))
  data_to_mau.addr  := alu_out
  data_to_mau.width_shift := funct3(1, 0)
  data_to_mau.load_signed := ~funct3(2)
  data_to_mau.data  := data2

  val pc_static_next = pc + 4.U

  data_to_mau.rf_waddr  := idu2exu_reg.rd
  data_to_mau.rf_wdata  := MuxLookup(opcode, 0.U)(Seq(
    OpCode.OP_IMM   -> alu_out,
    OpCode.OP       -> alu_out,
    OpCode.LUI      -> imm,
    OpCode.AUIPC    -> alu_out,
    OpCode.JAL      -> pc_static_next,
    OpCode.JALR     -> pc_static_next
  ))
  // store instructions and conditional branch instructions do not write back to register file
  data_to_mau.rf_wen  := opcode =/= OpCode.STORE && opcode =/= OpCode.BRANCH

  val pc_next = MuxLookup(opcode, pc_static_next)(Seq(
    OpCode.JAL    -> alu_out,
    OpCode.JALR   -> alu_out(XLEN - 1, 1) ## 0.U(1.W),    // set the least-significant bit to zero
    OpCode.BRANCH -> Mux(branch, alu_out, pc_static_next)
  ))
  data_to_mau.pc_next := pc_next
  io.exu2mau.bits := data_to_mau

  // `ebreak` instruction stops simulation abnormally
  if (config.sim) {
    val stopRaw = Module(new StopRaw)
    stopRaw.io.stop := io.idu2exu.fire && opcode === OpCode.SYSTEM && funct12 === Funct12.EBREAK
    stopRaw.io.ebreak := true.B
  }

  // report invalid instruction to simulator
  if (config.sim) {
    val errorRaw = Module(new ErrorRaw)
    errorRaw.io.error_type := ErrorRaw.ERROR_EXU.U
    errorRaw.io.error := state === wait_mau &&
      ((opcode === OpCode.JAL || opcode === OpCode.JALR || opcode === OpCode.BRANCH) && pc_next(1, 0) =/= "b00".U)
    errorRaw.setDefaultInfo()
    errorRaw.io.info0 := 0.U
    errorRaw.io.info1 := pc
  }
}
