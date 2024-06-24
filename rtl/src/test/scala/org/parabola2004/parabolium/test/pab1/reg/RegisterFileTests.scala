package org.parabola2004.parabolium.test.pab1.reg

import chisel3._
import chiseltest._
import org.parabola2004.parabolium.pab1.reg.RegisterFile
import org.parabola2004.parabolium.test.Helper
import org.parabola2004.parabolium.test.pab1.AbstractTests

class DiffRegisterFile {
  private val reg = new Array[Long](32)

  def write(addr: Int, data: Long): Unit = {
    require(0 <= addr && addr < 32)
    require(0 <= data && data < (1L << 32))
    if (addr != 0) reg(addr) = data
  }

  def read(addr: Int): Long = {
    require(0 <= addr && addr < 32)
    if (addr == 0) 0 else reg(addr)
  }
}

class RegisterFileTests extends AbstractTests {
  behavior of "RegisterFile"

  it should "always output zero when reading x0" in {
    test(new RegisterFile) { c =>
      c.io.waddr.poke(0.U)
      c.io.wdata.poke(0x1234.U)
      c.io.wen.poke(true.B)

      c.clock.step()

      c.io.wen.poke(false.B)

      c.io.raddr1.poke(0.U)
      c.io.rdata1.expect(0.U)

      c.io.raddr2.poke(0.U)
      c.io.rdata2.expect(0.U)

      for (_ <- 0 until 256) {
        c.io.waddr.poke(0.U)
        c.io.wdata.poke(Helper.randomU32Long().U)
        c.io.wen.poke(true.B)

        c.clock.step()

        c.io.wen.poke(false.B)

        c.io.raddr1.poke(0.U)
        c.io.rdata1.expect(0.U)

        c.io.raddr2.poke(0.U)
        c.io.rdata2.expect(0.U)
      }
    }
  }

  it should "behave correctly" in {
    test(new RegisterFile) { c =>
      val diff_rf = new DiffRegisterFile

      c.io.waddr.poke(13.U)
      c.io.wdata.poke(0x1234.U)
      c.io.wen.poke(true.B)
      c.clock.step()
      c.io.wen.poke(false.B)

      c.io.raddr1.poke(13.U)
      c.io.rdata1.expect(0x1234.U)
      c.io.raddr2.poke(13.U)
      c.io.rdata2.expect(0x1234.U)

      c.io.wen.poke(true.B)
      for (i <- 0 until 32) {
        diff_rf.write(i, 0)
        c.io.waddr.poke(i.U)
        c.io.wdata.poke(0.U)
        c.clock.step()
      }
      c.io.wen.poke(false.B)

      for (_ <- 0 until 256) {
        val addr = Helper.randomU5Int()
        if (Helper.randomBool()) {
          // read
          val v = diff_rf.read(addr)
          c.io.raddr1.poke(addr.U)
          c.io.rdata1.expect(v.U)
          c.io.raddr2.poke(addr.U)
          c.io.rdata2.expect(v.U)
        } else {
          // write
          val v = Helper.randomU32Long()
          diff_rf.write(addr, v)
          c.io.waddr.poke(addr.U)
          c.io.wdata.poke(v.U)
          c.io.wen.poke(true.B)
          c.clock.step()
          c.io.wen.poke(false.B)
        }
      }
    }
  }
}
