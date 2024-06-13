package org.parabola2004.parabolium.test.alu

import chisel3._
import chiseltest._
import org.parabola2004.parabolium.alu.{ALU, Adder, Comparator, Shifter}
import org.parabola2004.parabolium.inst.Funct3
import org.parabola2004.parabolium.test.Helper
import org.scalatest.flatspec.AnyFlatSpec

class ALUTests extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Adder"

  it should "produce correct sum and carry for addition" in {
    test(new Adder) { c =>
      c.io.sub.poke(false.B)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke(0x3456.U)
      c.io.sum.expect(0x468A.U)
      c.io.carry.expect(false.B)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.sum.expect(0x1233.U)
      c.io.carry.expect(true.B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-2))
      c.io.in2.poke(Helper.castS32IntToU32Long(-3))
      c.io.sum.expect(Helper.castS32IntToU32Long(-5))
      c.io.carry.expect(true.B)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        val sum_orig = in1 + in2
        val sum = Helper.truncateLongToU32(sum_orig)
        val carry = (sum_orig >> 32) != 0

        c.io.in1.poke(in1.U)
        c.io.in2.poke(in2.U)
        c.io.sum.expect(sum.U)
        c.io.carry.expect(carry.B)
      }
    }
  }

  it should "produce correct sum and carry for subtraction" in {
    test(new Adder) { c =>
      c.io.sub.poke(true.B)

      c.io.in1.poke(0x9876.U)
      c.io.in2.poke(0x1234.U)
      c.io.sum.expect(0x8642.U)
      c.io.carry.expect(true.B)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1))
      c.io.sum.expect(0x1235.U)
      c.io.carry.expect(false.B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-2))
      c.io.in2.poke(Helper.castS32IntToU32Long(-3))
      c.io.sum.expect(1.U)
      c.io.carry.expect(true.B)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        val sum_orig = in1 - in2
        val sum = Helper.truncateLongToU32(sum_orig)
        val carry = (sum_orig >> 32) == 0

        c.io.in1.poke(in1.U)
        c.io.in2.poke(in2.U)
        c.io.sum.expect(sum.U)
        c.io.carry.expect(carry.B)
      }
    }
  }


  behavior of "Shifter"

  it should "perform correct logical right shift" in {
    test(new Shifter) { c =>
      c.io.left.poke(false.B)
      c.io.arith.poke(false.B)

      c.io.in.poke(0x1234.U)
      c.io.shamt.poke(3.U)
      c.io.out.expect(0x246.U)

      c.io.in.poke(Helper.castS32IntToU32Long(-2).U)
      c.io.shamt.poke(3.U)
      c.io.out.expect(0x1FFFFFFF.U)

      for (_ <- 0 until 256) {
        val in = Helper.randomU32Long()
        val shamt = Helper.randomU5Int()
        c.io.in.poke(in.U)
        c.io.shamt.poke(shamt.U)
        c.io.out.expect((in >> shamt).U)
      }
    }
  }

  it should "perform correct arithmetic right shift" in {
    test(new Shifter) { c =>
      c.io.left.poke(false.B)
      c.io.arith.poke(true.B)

      c.io.in.poke(0x1234.U)
      c.io.shamt.poke(3.U)
      c.io.out.expect(0x246.U)

      c.io.in.poke(Helper.castS32IntToU32Long(-37).U)
      c.io.shamt.poke(3.U)
      c.io.out.expect(Helper.castS32IntToU32Long(-5).U)

      for (_ <- 0 until 256) {
        val in = Helper.randomS32Int()
        val shamt = Helper.randomU5Int()
        c.io.in.poke(Helper.castS32IntToU32Long(in).U)
        c.io.shamt.poke(shamt.U)
        c.io.out.expect(Helper.castS32IntToU32Long(in >> shamt).U)
      }
    }
  }

  it should "perform correct logical left shift" in {
    test(new Shifter) { c =>
      c.io.left.poke(true.B)

      c.io.in.poke(0x1234.U)
      c.io.shamt.poke(5.U)
      c.io.arith.poke(Helper.randomBool().B)
      c.io.out.expect(0x24680.U)

      c.io.in.poke(Helper.castS32IntToU32Long(-5).U)
      c.io.shamt.poke(3.U)
      c.io.arith.poke(Helper.randomBool().B)
      c.io.out.expect(Helper.castS32IntToU32Long(-40).U)

      for (_ <- 0 until 256) {
        val in = Helper.randomU32Long()
        val shamt = Helper.randomU5Int()
        c.io.arith.poke(Helper.randomBool().B)
        c.io.in.poke(in.U)
        c.io.shamt.poke(shamt.U)
        c.io.out.expect(Helper.truncateLongToU32(in << shamt).U)
      }
    }
  }


  behavior of "Comparator"

  it should "perform correct unsigned comparison" in {
    test(new Comparator) { c =>
      c.io.signed.poke(false.B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(0.U)
      c.io.eq.expect(true.B)
      c.io.lt.expect(false.B)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke(0x1234.U)
      c.io.eq.expect(true.B)
      c.io.lt.expect(false.B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.eq.expect(true.B)
      c.io.lt.expect(false.B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(1.U)
      c.io.eq.expect(false.B)
      c.io.lt.expect(true.B)

      c.io.in1.poke(1.U)
      c.io.in2.poke(0.U)
      c.io.eq.expect(false.B)
      c.io.lt.expect(false.B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.eq.expect(false.B)
      c.io.lt.expect(true.B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.in2.poke(0.U)
      c.io.eq.expect(false.B)
      c.io.lt.expect(false.B)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        c.io.in1.poke(in1.U)
        c.io.in2.poke(in2.U)
        c.io.eq.expect((in1 == in2).B)
        c.io.lt.expect((in1 < in2).B)
      }
    }
  }

  it should "perform correct signed comparison" in {
    test(new Comparator) { c =>
      c.io.signed.poke(true.B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(0.U)
      c.io.eq.expect(true.B)
      c.io.lt.expect(false.B)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke(0x1234.U)
      c.io.eq.expect(true.B)
      c.io.lt.expect(false.B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.eq.expect(true.B)
      c.io.lt.expect(false.B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(1.U)
      c.io.eq.expect(false.B)
      c.io.lt.expect(true.B)

      c.io.in1.poke(1.U)
      c.io.in2.poke(0.U)
      c.io.eq.expect(false.B)
      c.io.lt.expect(false.B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.eq.expect(false.B)
      c.io.lt.expect(false.B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.in2.poke(0.U)
      c.io.eq.expect(false.B)
      c.io.lt.expect(true.B)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomS32Int()
        val in2 = Helper.randomS32Int()
        c.io.in1.poke(Helper.castS32IntToU32Long(in1).U)
        c.io.in2.poke(Helper.castS32IntToU32Long(in2).U)
        c.io.eq.expect((in1 == in2).B)
        c.io.lt.expect((in1 < in2).B)
      }
    }
  }


  behavior of "ALU"

  it should "perform correct addition/subtraction" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.ADD)

      c.io.shift_arith.poke(Helper.randomBool().B)
      c.io.in1.poke(0x9876.U)
      c.io.in2.poke(0x1234.U)
      c.io.sub.poke(false.B)
      c.io.out.expect(0xAAAA.U)
      c.io.sub.poke(true.B)
      c.io.out.expect(0x8642.U)

      c.io.shift_arith.poke(Helper.randomBool().B)
      c.io.in1.poke(Helper.castS32IntToU32Long(-4).U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-7).U)
      c.io.sub.poke(false.B)
      c.io.out.expect(Helper.castS32IntToU32Long(-11).U)
      c.io.sub.poke(true.B)
      c.io.out.expect(3.U)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        c.io.in1.poke(in1.U)
        c.io.in2.poke(in2.U)
        c.io.shift_arith.poke(Helper.randomBool().B)

        c.io.sub.poke(false.B)
        c.io.out.expect(Helper.truncateLongToU32(in1 + in2).U)

        c.io.sub.poke(true.B)
        c.io.out.expect(Helper.truncateLongToU32(in1 - in2).U)
      }
    }
  }

  it should "perform correct unsigned comparison" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.SLTU)

      c.io.sub.poke(Helper.randomBool().B)
      c.io.shift_arith.poke(Helper.randomBool().B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(0.U)
      c.io.out.expect(0.B)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke(0x1234.U)
      c.io.out.expect(0.B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.out.expect(0.B)

      c.io.sub.poke(Helper.randomBool().B)
      c.io.shift_arith.poke(Helper.randomBool().B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(1.U)
      c.io.out.expect(1.B)

      c.io.sub.poke(Helper.randomBool().B)
      c.io.shift_arith.poke(Helper.randomBool().B)

      c.io.in1.poke(1.U)
      c.io.in2.poke(0.U)
      c.io.out.expect(0.B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.out.expect(1.B)

      c.io.sub.poke(Helper.randomBool().B)
      c.io.shift_arith.poke(Helper.randomBool().B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.in2.poke(0.U)
      c.io.out.expect(0.B)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        c.io.in1.poke(in1.U)
        c.io.in2.poke(in2.U)
        c.io.sub.poke(Helper.randomBool().B)
        c.io.shift_arith.poke(Helper.randomBool().B)
        c.io.out.expect((in1 < in2).B)
      }
    }
  }

  it should "perform correct signed comparison" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.SLT)

      c.io.sub.poke(Helper.randomBool().B)
      c.io.shift_arith.poke(Helper.randomBool().B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(0.U)
      c.io.out.expect(0.B)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke(0x1234.U)
      c.io.out.expect(0.B)

      c.io.sub.poke(Helper.randomBool().B)
      c.io.shift_arith.poke(Helper.randomBool().B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.out.expect(0.B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(1.U)
      c.io.out.expect(1.B)

      c.io.in1.poke(1.U)
      c.io.in2.poke(0.U)
      c.io.out.expect(0.B)

      c.io.sub.poke(Helper.randomBool().B)
      c.io.shift_arith.poke(Helper.randomBool().B)

      c.io.in1.poke(0.U)
      c.io.in2.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.out.expect(0.B)

      c.io.in1.poke(Helper.castS32IntToU32Long(-1).U)
      c.io.in2.poke(0.U)
      c.io.out.expect(1.B)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomS32Int()
        val in2 = Helper.randomS32Int()
        c.io.in1.poke(Helper.castS32IntToU32Long(in1).U)
        c.io.in2.poke(Helper.castS32IntToU32Long(in2).U)
        c.io.sub.poke(Helper.randomBool().B)
        c.io.shift_arith.poke(Helper.randomBool().B)
        c.io.out.expect((in1 < in2).B)
      }
    }
  }

  it should "perform correct logical left/right shift" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.SRL)
      c.io.shift_arith.poke(false.B)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke((3 + (0x12345 << 5)).U)
      c.io.sub.poke(Helper.randomBool().B)
      c.io.out.expect(0x246.U)

      c.io.in1.poke(Helper.castS32IntToU32Long(-2).U)
      c.io.in2.poke((3 + (0x54321 << 5)).U)
      c.io.sub.poke(Helper.randomBool().B)
      c.io.out.expect(0x1FFFFFFF.U)

      c.io.funct3.poke(Funct3.SLL)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke((5 + (0xAb642 << 5)).U)
      c.io.sub.poke(Helper.randomBool().B)
      c.io.shift_arith.poke(Helper.randomBool())
      c.io.out.expect(0x24680.U)

      c.io.in1.poke(Helper.castS32IntToU32Long(-5).U)
      c.io.in2.poke((3 + (0x2468A << 5)).U)
      c.io.sub.poke(Helper.randomBool().B)
      c.io.shift_arith.poke(Helper.randomBool())
      c.io.out.expect(Helper.castS32IntToU32Long(-40).U)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomU32Long()
        val in2 = Helper.randomU32Long()
        val shamt = (in2 & 0x1f).toInt
        c.io.in1.poke(in1.U)
        c.io.in2.poke(in2.U)

        c.io.funct3.poke(Funct3.SLL)
        c.io.shift_arith.poke(Helper.randomBool())
        c.io.sub.poke(Helper.randomBool().B)
        c.io.out.expect(Helper.truncateLongToU32(in1 << shamt).U)

        c.io.funct3.poke(Funct3.SRL)
        c.io.shift_arith.poke(false.B)
        c.io.sub.poke(Helper.randomBool().B)
        c.io.out.expect((in1 >> shamt).U)
      }
    }
  }

  it should "perform correct arithmetic right shift" in {
    test(new ALU) { c =>
      c.io.funct3.poke(Funct3.SRL)
      c.io.shift_arith.poke(true.B)

      c.io.in1.poke(0x1234.U)
      c.io.in2.poke((3 + (0x12345 << 5)).U)
      c.io.sub.poke(Helper.randomBool().B)
      c.io.out.expect(0x246.U)

      c.io.in1.poke(Helper.castS32IntToU32Long(-37).U)
      c.io.in2.poke((3 + (0xA8642 << 5)).U)
      c.io.sub.poke(Helper.randomBool().B)
      c.io.out.expect(Helper.castS32IntToU32Long(-5).U)

      for (_ <- 0 until 256) {
        val in1 = Helper.randomS32Int()
        val in2 = Helper.randomU32Long()
        val shamt = (in2 & 0x1f).toInt
        c.io.in1.poke(Helper.castS32IntToU32Long(in1).U)
        c.io.in2.poke(in2.U)
        c.io.sub.poke(Helper.randomBool().B)
        c.io.out.expect(Helper.castS32IntToU32Long(in1 >> shamt).U)
      }
    }
  }
}
