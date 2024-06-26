package org.parabola2004.parabolium.perip

import chisel3._
import chisel3.util.{Decoupled, Enum, MuxLookup, Queue, RegEnable}
import org.parabola2004.parabolium.util.ByteShiftOut

/**
 * simple UART controller (currently only support TX output)
 *
 * @param clkDivWidth     width for clk_div register
 * @param defaultClkDiv   default clk_div value (how many clocks per bit sent/receive)
 * @param txQueueSize     TX data queue size
 */
class UARTControl(val clkDivWidth: Int = 16,
                  val defaultClkDiv: Int = 0x100,
                  val txQueueSize: Int = 0x10) extends Module {
  val io = IO(new Bundle {
    // clock division factor (will plus one)
    val clk_div     = Input(UInt(clkDivWidth.W))

    // clock division factor register write enable
    val clk_div_en  = Input(Bool())

    // write to tx data queue
    val tx_data     = Flipped(Decoupled(UInt(8.W)))


    // UART TX signal output
    val tx          = Output(Bool())
  })

  val clk_div = RegEnable(io.clk_div, defaultClkDiv.U(clkDivWidth.W), io.clk_div_en)

  val clk_counter = RegInit(0.U(clkDivWidth.W))
  val new_cycle   = clk_counter === clk_div

  val bit_counter_next = Wire(UInt(3.W))
  val bit_counter = RegEnable(bit_counter_next, 0.U, new_cycle)

  val tx_queue = Module(new Queue(UInt(8.W), txQueueSize))
  tx_queue.io.enq <> io.tx_data

  val shift_reg = Module(new ByteShiftOut)
  shift_reg.io.set  := tx_queue.io.deq.fire
  shift_reg.io.data := tx_queue.io.deq.bits

  val idle :: start :: working :: stop :: Nil = Enum(4)
  val state = RegInit(idle)
  state := MuxLookup(state, idle)(Seq(
    idle        -> Mux(tx_queue.io.deq.valid, start, idle),
    start       -> Mux(new_cycle, working, start),
    working     -> Mux(new_cycle && bit_counter === 7.U, stop, working),
    stop        -> Mux(new_cycle, idle, stop),
  ))

  clk_counter := Mux(state === idle, 0.U, Mux(new_cycle, 0.U, clk_counter + 1.U))
  bit_counter_next := Mux(state === working, bit_counter + 1.U, 0.U)

  tx_queue.io.deq.ready := state === idle

  shift_reg.io.shift  := state === working && new_cycle

  io.tx := MuxLookup(state, true.B)(Seq(
    idle        -> true.B,
    start       -> false.B,
    working     -> shift_reg.io.out,
    stop        -> true.B))
}
