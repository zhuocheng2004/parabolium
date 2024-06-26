package org.parabola2004.parabolium.test.util

import chisel3._
import chiseltest._
import org.parabola2004.parabolium.test.{AbstractTests, Helper}
import org.parabola2004.parabolium.util.ByteShiftOut

class ByteShiftTests extends AbstractTests {
  behavior of "ByteShiftOut"

  it should "keep data if no shift signal" in {
    test(new ByteShiftOut) { c =>
      c.io.shift.poke(false.B)
      c.io.data.poke(0x37.U)
      c.io.set.poke(true.B)
      c.clock.step()
      c.io.set.poke(false.B)

      for (_ <- 0 until 256) {
        c.clock.step()
        c.io.out.expect(true.B)
      }
    }
  }

  it should "shift right one bit when each shift signal" in {
    test(new ByteShiftOut) { c =>
      c.io.shift.poke(false.B)
      c.io.data.poke(0x57.U)
      c.io.set.poke(true.B)
      c.clock.step()
      c.io.set.poke(false.B)
      c.clock.step()

      c.io.out.expect(true.B)
      c.clock.step()
      c.io.out.expect(true.B)

      // enable shifting
      c.io.shift.poke(true.B)
      c.clock.step()
      c.io.out.expect(true.B)
      c.clock.step()
      c.io.out.expect(true.B)
      c.clock.step()
      c.io.out.expect(false.B)

      // disable shifting
      c.io.shift.poke(false.B)
      c.clock.step()
      c.io.out.expect(false.B)
      c.clock.step()
      c.io.out.expect(false.B)
      c.clock.step()
      c.io.out.expect(false.B)

      // enable shifting
      c.io.shift.poke(true.B)
      c.clock.step()
      c.io.out.expect(true.B)
      c.clock.step()
      c.io.out.expect(false.B)
      c.clock.step()
      c.io.out.expect(true.B)
      c.clock.step()
      c.io.out.expect(false.B)

      // shifting out
      c.clock.step()
      c.io.out.expect(false.B)
      c.clock.step()
      c.io.out.expect(false.B)

      for (_ <- 0 until 256) {
        c.io.shift.poke(false.B)

        var v = Helper.randomU8Int()
        c.io.set.poke(true.B)
        c.io.data.poke(v.U)
        c.clock.step()
        c.io.set.poke(false.B)
        c.clock.step()
        c.io.out.expect((v & 0x1).U)

        for (_ <- 0 until 32) {
          if (Helper.randomBool()) {
            c.io.shift.poke(true.B)
            c.clock.step()
            v >>= 1
          } else {
            c.io.shift.poke(false.B)
            c.clock.step()
          }
          c.io.out.expect((v & 0x1).U)
        }
      }
    }
  }
}
