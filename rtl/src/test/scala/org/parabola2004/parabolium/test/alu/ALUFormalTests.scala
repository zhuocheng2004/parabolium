package org.parabola2004.parabolium.test.alu

import chiseltest._
import chiseltest.formal._
import org.parabola2004.parabolium.alu.Shifter
import org.scalatest.flatspec.AnyFlatSpec

class ALUFormalTests extends AnyFlatSpec with ChiselScalatestTester with Formal {
  "Two implementations of Shifter" should "be equivalent" in {
    verify(new Shifter, Seq(BoundedCheck(1)))
  }
}
