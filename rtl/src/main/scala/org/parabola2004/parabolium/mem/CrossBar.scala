package org.parabola2004.parabolium.mem

import chisel3._
import chisel3.util.{Decoupled, MuxCase, RegEnable}
import org.parabola2004.parabolium.Defines.XLEN
import org.parabola2004.parabolium.raw.StopRaw
import org.parabola2004.parabolium.std.AXI5LiteIO
import org.parabola2004.parabolium.{Config, Defines}

/**
 * cross bar that handles memory operations at different addresses
 */
class CrossBar(implicit config: Config = Config()) extends Module {
  val io = IO(new Bundle {
    // memory access from upstream
    val up              = Flipped(new AXI5LiteIO)

    // 8-bit LED output
    val led             = Output(UInt(8.W))
    val led_en          = Output(Bool())

    // UART clock division register
    val uart_clk_div    = Output(UInt(16.W))
    val uart_clk_div_en = Output(Bool())

    // UART TX data
    val uart_tx_data    = Decoupled(UInt(8.W))

    // physical ram
    val ram             = new AXI5LiteIO
  })

  // whether the address lies in the region of physical RAM
  def inRAMRange(addr: UInt) = addr(XLEN - 1, 14) ## 0.U(14.W) === 0x80000000L.U

  /*
   * read cross bar
   */

  val araddr_in_aligned = io.up.araddr(XLEN - 1, 2) ## 0.U(2.W)
  val araddr  = Mux(io.up.arvalid, araddr_in_aligned, RegEnable(araddr_in_aligned, io.up.arvalid))

  val r_ram           = inRAMRange(araddr)

  io.up.arready   := MuxCase(false.B, Seq(
    r_ram           -> io.ram.arready
  ))

  io.ram.arvalid  := r_ram && io.up.arvalid
  io.ram.araddr   := io.up.araddr

  io.up.rdata     := io.ram.rdata

  io.ram.rready   := r_ram && io.up.rready

  io.up.rvalid    := MuxCase(false.B, Seq(
    r_ram           -> io.ram.rvalid
  ))

  io.up.rresp     := MuxCase(AXI5LiteIO.DECERR, Seq(
    r_ram           -> io.ram.rresp
  ))

  /*
   * write cross bar
   */

  val awaddr_in_aligned = io.up.awaddr(XLEN - 1, 2) ## 0.U(2.W)
  val awaddr  = Mux(io.up.awvalid, awaddr_in_aligned, RegEnable(awaddr_in_aligned, io.up.awvalid))
  val wdata   = RegEnable(io.up.wdata, io.up.w_fire)
  val wstrb   = RegEnable(io.up.wstrb, io.up.w_fire)

  val w_stop          = awaddr === (Defines.MISC + Defines.STOP).U && wstrb(0)

  val w_led           = awaddr === (Defines.MISC + Defines.LED).U && wstrb(0)

  val w_uart_clk_div  = awaddr === (Defines.UART + Defines.UART_CLK_DIV).U && wstrb(0) && wstrb(1)
  val w_uart_tx_data  = awaddr === (Defines.UART + Defines.UART_TX).U && wstrb(0)

  val w_ram           = inRAMRange(awaddr)

  io.up.awready := MuxCase(false.B, Seq(
    w_ram           -> io.ram.awready,
    w_stop          -> true.B,
    w_led           -> true.B,
    w_uart_clk_div  -> true.B,
    w_uart_tx_data  -> true.B
  ))

  io.up.wready  := MuxCase(false.B, Seq(
    w_ram           -> io.ram.wready,
    w_stop          -> true.B,
    w_led           -> true.B,
    w_uart_clk_div  -> true.B,
    w_uart_tx_data  -> io.uart_tx_data.ready
  ))

  // informs simulator to stop simulating
  if (config.sim) {
    val stopRaw = Module(new StopRaw)
    stopRaw.io.stop := w_stop && io.up.w_fire && wdata(7, 0) =/= 0.U
  }

  io.led              := wdata(7, 0)
  io.led_en           := w_led && io.up.w_fire

  io.uart_clk_div     := wdata(15, 0)
  io.uart_clk_div_en  := w_uart_clk_div && io.up.w_fire

  io.uart_tx_data.valid := w_uart_tx_data && io.up.wvalid
  io.uart_tx_data.bits  := wdata(7, 0)

  io.ram.awvalid  := w_ram && io.up.awvalid
  io.ram.awaddr   := awaddr
  io.ram.wvalid   := w_ram && io.up.wvalid
  io.ram.wdata    := wdata
  io.ram.wstrb    := wstrb
  io.ram.bready   := w_ram && io.up.bready

  io.up.bvalid    := MuxCase(true.B, Seq(
    w_ram           -> io.ram.bvalid
  ))

  io.up.bresp     := MuxCase(AXI5LiteIO.DECERR, Seq(
    w_ram           -> io.ram.bresp,
    w_led           -> AXI5LiteIO.OKAY,
    w_uart_clk_div  -> AXI5LiteIO.OKAY,
    w_uart_tx_data  -> AXI5LiteIO.OKAY
  ))
}
