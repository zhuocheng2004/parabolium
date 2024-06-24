package org.parabola2004.parabolium.test.pab1

import chiseltest._
import org.parabola2004.parabolium.pab1.Config
import org.scalatest.flatspec.AnyFlatSpec

abstract class AbstractTests extends AnyFlatSpec with ChiselScalatestTester {
  implicit val config: Config = Config(test = true)
}
