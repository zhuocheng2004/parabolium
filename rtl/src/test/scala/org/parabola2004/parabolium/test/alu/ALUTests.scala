package org.parabola2004.parabolium.test.alu

import chisel3._
import chiseltest._
import org.parabola2004.parabolium.pab1.alu.{ALU, Adder, Comparator, Shifter}
import org.parabola2004.parabolium.pab1.inst.Funct3
import org.parabola2004.parabolium.test.{AbstractTests, Helper}

class ALUTests extends AbstractTests {
  behavior of "Adder"

  it should "produce correct sum and carry for addition" in {
    test(new Adder) { c =>
      c.io.sub.poke(false)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(0x3456)
      c.io.sum.expect(0x468A)
      c.io.carry.expect(false)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.sum.expect(0x1233)
      c.io.carry.expect(true)

      c.io.in1.poke(Helper.castS32IntToU32Long(-2))
      c.io.in2.poke(Helper.castS32IntToU32Long(-3))
      c.io.sum.expect(Helper.castS32IntToU32Long(-5))
      c.io.carry.expect(true)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        val sum_orig = in1 + in2
        val sum = Helper.truncateLongToU32(sum_orig)
        val carry = (sum_orig >> 32) != 0

        c.io.in1.poke(in1)
        c.io.in2.poke(in2)
        c.io.sum.expect(sum)
        c.io.carry.expect(carry)
      }
    }
  }

  it should "produce correct sum and carry for subtraction" in {
    test(new Adder) { c =>
      c.io.sub.poke(true)

      c.io.in1.poke(0x9876)
      c.io.in2.poke(0x1234)
      c.io.sum.expect(0x8642)
      c.io.carry.expect(true)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.sum.expect(0x1235)
      c.io.carry.expect(false)

      c.io.in1.poke(Helper.castS32IntToU32Long(-2))
      c.io.in2.poke(Helper.castS32IntToU32Long(-3))
      c.io.sum.expect(1)
      c.io.carry.expect(true)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        val sum_orig = in1 - in2
        val sum = Helper.truncateLongToU32(sum_orig)
        val carry = (sum_orig >> 32) == 0

        c.io.in1.poke(in1)
        c.io.in2.poke(in2)
        c.io.sum.expect(sum)
        c.io.carry.expect(carry)
      }
    }
  }


  behavior of "Shifter"

  it should "perform correct logical right shift" in {
    test(new Shifter) { c =>
      c.io.left.poke(false)
      c.io.arith.poke(false)

      c.io.in.poke(0x1234)
      c.io.shamt.poke(3)
      c.io.out.expect(0x246)

      c.io.in.poke(Helper.castS32IntToU32Long(-2))
      c.io.shamt.poke(3)
      c.io.out.expect(0x1FFFFFFF)

      for (_ <- 0 until 256) {
        val in = Helper.randomU32Long()
        val shamt = Helper.randomU5Int()
        c.io.in.poke(in)
        c.io.shamt.poke(shamt)
        c.io.out.expect(in >> shamt)
      }
    }
  }

  it should "perform correct arithmetic right shift" in {
    test(new Shifter) { c =>
      c.io.left.poke(false)
      c.io.arith.poke(true)

      c.io.in.poke(0x1234)
      c.io.shamt.poke(3)
      c.io.out.expect(0x246)

      c.io.in.poke(Helper.castS32IntToU32Long(-37))
      c.io.shamt.poke(3)
      c.io.out.expect(Helper.castS32IntToU32Long(-5))

      for (_ <- 0 until 256) {
        val in = Helper.randomS32Int()
        val shamt = Helper.randomU5Int()
        c.io.in.poke(Helper.castS32IntToU32Long(in))
        c.io.shamt.poke(shamt)
        c.io.out.expect(Helper.castS32IntToU32Long(in >> shamt))
      }
    }
  }

  it should "perform correct logical left shift" in {
    test(new Shifter) { c =>
      c.io.left.poke(true)

      c.io.in.poke(0x1234)
      c.io.shamt.poke(5)
      c.io.arith.poke(Helper.randomBool())
      c.io.out.expect(0x24680)

      c.io.in.poke(Helper.castS32IntToU32Long(-5))
      c.io.shamt.poke(3)
      c.io.arith.poke(Helper.randomBool())
      c.io.out.expect(Helper.castS32IntToU32Long(-40))

      for (_ <- 0 until 256) {
        val in = Helper.randomU32Long()
        val shamt = Helper.randomU5Int()
        c.io.arith.poke(Helper.randomBool())
        c.io.in.poke(in)
        c.io.shamt.poke(shamt)
        c.io.out.expect(Helper.truncateLongToU32(in << shamt))
      }
    }
  }


  behavior of "Comparator"

  it should "perform correct unsigned comparison" in {
    test(new Comparator) { c =>
      c.io.signed.poke(false)

      c.io.in1.poke(0)
      c.io.in2.poke(0)
      c.io.eq.expect(true)
      c.io.lt.expect(false)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(0x1234)
      c.io.eq.expect(true)
      c.io.lt.expect(false)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1))
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.eq.expect(true)
      c.io.lt.expect(false)

      c.io.in1.poke(0)
      c.io.in2.poke(1)
      c.io.eq.expect(false)
      c.io.lt.expect(true)

      c.io.in1.poke(1)
      c.io.in2.poke(0)
      c.io.eq.expect(false)
      c.io.lt.expect(false)

      c.io.in1.poke(0)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.eq.expect(false)
      c.io.lt.expect(true)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1))
      c.io.in2.poke(0)
      c.io.eq.expect(false)
      c.io.lt.expect(false)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        c.io.in1.poke(in1)
        c.io.in2.poke(in2)
        c.io.eq.expect(in1 == in2)
        c.io.lt.expect(in1 < in2)
      }
    }
  }

  it should "perform correct signed comparison" in {
    test(new Comparator) { c =>
      c.io.signed.poke(true)

      c.io.in1.poke(0)
      c.io.in2.poke(0)
      c.io.eq.expect(true)
      c.io.lt.expect(false)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(0x1234)
      c.io.eq.expect(true)
      c.io.lt.expect(false)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1))
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.eq.expect(true)
      c.io.lt.expect(false)

      c.io.in1.poke(0)
      c.io.in2.poke(1)
      c.io.eq.expect(false)
      c.io.lt.expect(true)

      c.io.in1.poke(1)
      c.io.in2.poke(0)
      c.io.eq.expect(false)
      c.io.lt.expect(false)

      c.io.in1.poke(0)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.eq.expect(false)
      c.io.lt.expect(false)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1))
      c.io.in2.poke(0)
      c.io.eq.expect(false)
      c.io.lt.expect(true)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomS32Int()
        val in2 = Helper.randomS32Int()
        c.io.in1.poke(Helper.castS32IntToU32Long(in1))
        c.io.in2.poke(Helper.castS32IntToU32Long(in2))
        c.io.eq.expect(in1 == in2)
        c.io.lt.expect(in1 < in2)
      }
    }
  }


  behavior of "ALU"

  it should "perform correct addition/subtraction" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.ADD)

      c.io.shift_arith.poke(Helper.randomBool())
      c.io.in1.poke(0x9876)
      c.io.in2.poke(0x1234)
      c.io.sub.poke(false)
      c.io.out.expect(0xAAAA)
      c.io.sub.poke(true)
      c.io.out.expect(0x8642)

      c.io.shift_arith.poke(Helper.randomBool())
      c.io.in1.poke(Helper.castS32IntToU32Long(-4))
      c.io.in2.poke(Helper.castS32IntToU32Long(-7))
      c.io.sub.poke(false)
      c.io.out.expect(Helper.castS32IntToU32Long(-11))
      c.io.sub.poke(true)
      c.io.out.expect(3)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        c.io.in1.poke(in1)
        c.io.in2.poke(in2)
        c.io.shift_arith.poke(Helper.randomBool())

        c.io.sub.poke(false)
        c.io.out.expect(Helper.truncateLongToU32(in1 + in2))

        c.io.sub.poke(true)
        c.io.out.expect(Helper.truncateLongToU32(in1 - in2))
      }
    }
  }

  it should "perform correct unsigned comparison" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.SLTU)

      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())

      c.io.in1.poke(0)
      c.io.in2.poke(0)
      c.io.out.expect(0)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(0x1234)
      c.io.out.expect(0)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1))
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.out.expect(0)

      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())

      c.io.in1.poke(0)
      c.io.in2.poke(1)
      c.io.out.expect(1)

      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())

      c.io.in1.poke(1)
      c.io.in2.poke(0)
      c.io.out.expect(0)

      c.io.in1.poke(0)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.out.expect(1)

      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())

      c.io.in1.poke(Helper.castS32IntToU32Long(-1))
      c.io.in2.poke(0)
      c.io.out.expect(0)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        c.io.in1.poke(in1)
        c.io.in2.poke(in2)
        c.io.sub.poke(Helper.randomBool())
        c.io.shift_arith.poke(Helper.randomBool())
        c.io.out.expect((in1 < in2).B)
      }
    }
  }

  it should "perform correct signed comparison" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.SLT)

      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())

      c.io.in1.poke(0)
      c.io.in2.poke(0)
      c.io.out.expect(0)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(0x1234)
      c.io.out.expect(0)

      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())

      c.io.in1.poke(Helper.castS32IntToU32Long(-1))
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.out.expect(0)

      c.io.in1.poke(0)
      c.io.in2.poke(1)
      c.io.out.expect(1)

      c.io.in1.poke(1)
      c.io.in2.poke(0)
      c.io.out.expect(0)

      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())

      c.io.in1.poke(0)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.out.expect(0)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1))
      c.io.in2.poke(0)
      c.io.out.expect(1)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomS32Int()
        val in2 = Helper.randomS32Int()
        c.io.in1.poke(Helper.castS32IntToU32Long(in1))
        c.io.in2.poke(Helper.castS32IntToU32Long(in2))
        c.io.sub.poke(Helper.randomBool())
        c.io.shift_arith.poke(Helper.randomBool())
        c.io.out.expect((in1 < in2).B)
      }
    }
  }

  it should "perform correct logical left/right shift" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.SRL)
      c.io.shift_arith.poke(false)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(3 + (0x12345 << 5))
      c.io.sub.poke(Helper.randomBool())
      c.io.out.expect(0x246)

      c.io.in1.poke(Helper.castS32IntToU32Long(-2))
      c.io.in2.poke(3 + (0x54321 << 5))
      c.io.sub.poke(Helper.randomBool())
      c.io.out.expect(0x1FFFFFFF)

      c.io.funct3.poke(Funct3.SLL)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(5 + (0xAb642 << 5))
      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())
      c.io.out.expect(0x24680)

      c.io.in1.poke(Helper.castS32IntToU32Long(-5))
      c.io.in2.poke(3 + (0x2468A << 5))
      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())
      c.io.out.expect(Helper.castS32IntToU32Long(-40))

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        val shamt = (in2 & 0x1f).toInt
        c.io.in1.poke(in1)
        c.io.in2.poke(in2)

        c.io.funct3.poke(Funct3.SLL)
        c.io.shift_arith.poke(Helper.randomBool())
        c.io.sub.poke(Helper.randomBool())
        c.io.out.expect(Helper.truncateLongToU32(in1 << shamt))

        c.io.funct3.poke(Funct3.SRL)
        c.io.shift_arith.poke(false)
        c.io.sub.poke(Helper.randomBool())
        c.io.out.expect(in1 >> shamt)
      }
    }
  }

  it should "perform correct arithmetic right shift" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.SRL)
      c.io.shift_arith.poke(true)

      c.io.in1.poke(0x1234)
      c.io.in2.poke(3 + (0x12345 << 5))
      c.io.sub.poke(Helper.randomBool())
      c.io.out.expect(0x246)

      c.io.in1.poke(Helper.castS32IntToU32Long(-37))
      c.io.in2.poke(3 + (0xA8642 << 5))
      c.io.sub.poke(Helper.randomBool())
      c.io.out.expect(Helper.castS32IntToU32Long(-5))

      for (_ <- 0 until 256) {
        val in1 = Helper.randomS32Int()
        val in2 = Helper.randomU32Long()
        val shamt = (in2 & 0x1f).toInt
        c.io.in1.poke(Helper.castS32IntToU32Long(in1))
        c.io.in2.poke(in2)
        c.io.sub.poke(Helper.randomBool())
        c.io.out.expect(Helper.castS32IntToU32Long(in1 >> shamt))
      }
    }
  }

  it should "perform correct logical actions" in {
    test(new ALU) { c =>
      c.io.sub.poke(Helper.randomBool())
      c.io.shift_arith.poke(Helper.randomBool())

      c.io.in1.poke(0x1234)
      c.io.in2.poke(0x9876)

      c.io.funct3.poke(Funct3.AND)
      c.io.out.expect(0x1034)

      c.io.funct3.poke(Funct3.OR)
      c.io.out.expect(0x9A76)

      c.io.funct3.poke(Funct3.XOR)
      c.io.out.expect(0x8A42)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        c.io.in1.poke(in1)
        c.io.in2.poke(in2)
        c.io.sub.poke(Helper.randomBool())
        c.io.shift_arith.poke(Helper.randomBool())

        c.io.funct3.poke(Funct3.AND)
        c.io.out.expect(in1 & in2)

        c.io.funct3.poke(Funct3.OR)
        c.io.out.expect(in1 | in2)

        c.io.funct3.poke(Funct3.XOR)
        c.io.out.expect(in1 ^ in2)
      }
    }
  }
}
