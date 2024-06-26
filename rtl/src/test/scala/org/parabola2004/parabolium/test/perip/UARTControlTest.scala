package org.parabola2004.parabolium.test.perip

import chisel3._
import chiseltest._
import org.parabola2004.parabolium.perip.UARTControl
import org.parabola2004.parabolium.test.AbstractTests

class UARTControlTest extends AbstractTests {
  behavior of "UARTControl"

  it should "keep TX high when there is no data" in {
    test(new UARTControl) { c =>
      for (_ <- 0 until 256) {
        c.clock.step()
        c.io.tx.expect(true.B)
      }
    }
  }

  it should "output one byte correctly" in {
    test(new UARTControl) { c =>
      c.io.tx_data.initSource()

      c.io.clk_div.poke(2.U)
      c.io.clk_div_en.poke(true.B)
      c.clock.step()
      c.io.clk_div_en.poke(false.B)

      for (_ <- 0 until 256) {
        c.io.tx.expect(true.B)
        c.clock.step()
      }

      c.io.tx_data.enqueueNow(0x57.U)
      c.clock.step()
      c.io.tx.expect(false.B)   // start
      c.clock.step()
      c.io.tx.expect(false.B)   // start
      c.clock.step()
      c.io.tx.expect(false.B)   // start

      c.clock.step()
      c.io.tx.expect(true.B)    // bit 0
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 0
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 0
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 1
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 1
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 1
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 2
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 2
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 2
      c.clock.step()
      c.io.tx.expect(false.B)   // bit 3
      c.clock.step()
      c.io.tx.expect(false.B)   // bit 3
      c.clock.step()
      c.io.tx.expect(false.B)   // bit 3

      c.clock.step()
      c.io.tx.expect(true.B)    // bit 4
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 4
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 4
      c.clock.step()
      c.io.tx.expect(false.B)   // bit 5
      c.clock.step()
      c.io.tx.expect(false.B)   // bit 5
      c.clock.step()
      c.io.tx.expect(false.B)   // bit 5
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 6
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 6
      c.clock.step()
      c.io.tx.expect(true.B)    // bit 6
      c.clock.step()
      c.io.tx.expect(false.B)   // bit 7
      c.clock.step()
      c.io.tx.expect(false.B)   // bit 7
      c.clock.step()
      c.io.tx.expect(false.B)   // bit 7


      c.clock.step()
      c.io.tx.expect(true.B)   // stop
      c.clock.step()
      c.io.tx.expect(true.B)   // stop
      c.clock.step()
      c.io.tx.expect(true.B)   // stop

      for (_ <- 0 until 16) {
        c.clock.step()
        c.io.tx.expect(true.B)  // idle
      }
    }
  }
}
