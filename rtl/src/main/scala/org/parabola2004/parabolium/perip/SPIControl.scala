package org.parabola2004.parabolium.perip

import chisel3._
import chisel3.util.{Enum, MuxLookup, RegEnable}
import org.parabola2004.parabolium.util.ByteShiftOut

/**
 * simple SPI master controller
 *
 * 8-bit; one slave; configurable clock division factor, clock polarity, clock phase
 *
 * Typically, a communication goes through the following steps
 *   1. set configurations
 *   2. write data to
 *
 * @param defaultClkDivShift    default clk_div_shift value
 *                              (one SLCK period is 2**(clk_div_shift+1) many clocks)
 */
class SPIControl(val defaultClkDivShift: Int = 0x1) extends Module {

  val io = IO(new Bundle {
    // config registers write enable
    val config_en         = Input(Bool())

    // config: clock division factor shift
    val clk_div_shift     = Input(UInt(4.W))

    // config: clock polarity
    //   0 -> clock idle low
    //   1 -> clock idle high
    val cpol              = Input(Bool())

    // config: clock phase
    val cpha              = Input(Bool())

    // SPI communication enable
    val en                = Input(Bool())

    // master-to-slave output data
    val out_data          = Input(UInt(8.W))

    // master-to-slave output data write enable
    val out_data_en       = Input(Bool())

    // whether master-to-slave output data has been transferred to the shift register,
    // and we can write next data
    val out_ok            = Output(Bool())

    // slave-to-master input data
    val in_data           = Output(UInt(8.W))

    // whether slave-to-master input data has been transferred from shift register,
    // and we can read it
    val in_ok             = Output(UInt(8.W))

    // whether a communication is going on
    val busy              = Output(Bool())


    // SPI signals
    val cs                = Output(Bool())
    val sck               = Output(Bool())
    val mosi              = Output(Bool())
    val miso              = Input(Bool())
  })

  // configurations
  val clk_div_shift = RegEnable(io.clk_div_shift, defaultClkDivShift.U(4.W), io.config_en)
  val cpol = RegEnable(io.cpol, false.B, io.config_en)
  val cpha = RegEnable(io.cpha, false.B, io.config_en)

  val out_data = RegEnable(io.out_data, 0.U, io.out_data_en)

  val clk_counter = RegInit(0.U(16.W))
  val new_cycle   = !clk_counter(clk_div_shift)

  val bit_counter_next = Wire(UInt(3.W))
  val bit_counter = RegEnable(bit_counter_next, 0.U, new_cycle)

  val shift_reg = Module(new ByteShiftOut)
  shift_reg.io.set  := new_cycle && bit_counter === 7.U
  shift_reg.io.data := out_data

  val idle :: prepare :: working :: stop :: Nil = Enum(4)
  val state = RegInit(idle)
  state := MuxLookup(state, idle)(Seq(
    idle        -> Mux(io.en, prepare, stop),
    prepare     -> Mux(io.en, working, stop),
    working     -> Mux(io.en, working, stop),
    stop        -> idle
  ))

  clk_counter := Mux(state === working, Mux(new_cycle, 0.U, clk_counter + 1.U), 0.U)
  bit_counter_next := Mux(state === working, bit_counter + 1.U, 0.U)

  shift_reg.io.shift := state === working && new_cycle

  io.cs     := state =/= idle
  io.sck    := cpol ^ (state === working && !clk_counter(clk_div_shift))
}
