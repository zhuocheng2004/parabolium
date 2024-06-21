package org.parabola2004.parabolium.test.alu

import chiseltest.formal._
import org.parabola2004.parabolium.pab1.alu.Shifter
import org.parabola2004.parabolium.test.AbstractTests

class ALUFormalTests extends AbstractTests with Formal {
  "Two implementations of Shifter" should "be equivalent" in {
    verify(new Shifter, Seq(BoundedCheck(1)))
  }
}
