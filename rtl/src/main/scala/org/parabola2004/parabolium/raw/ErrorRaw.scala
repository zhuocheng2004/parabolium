package org.parabola2004.parabolium.raw

import chisel3._
import org.parabola2004.parabolium.Defines.XLEN
import org.parabola2004.parabolium.raw.ErrorRaw.ERROR_TYPE_WIDTH

/**
 * a raw module that informs the simulator of an unexpected error
 */
class ErrorRaw extends BlackBox {
  val io = IO(new Bundle {
    val error       = Input(Bool())
    val error_type  = Input(UInt(ERROR_TYPE_WIDTH.W))
    val info0       = Input(UInt(XLEN.W))
  })
}

object ErrorRaw {
  val ERROR_TYPE_WIDTH    = 3

  /**
   * no error
   */
  val ERROR_NONE          = 0x0

  /**
   * error fetching instruction from LSU
   *
   * info1: PC that was used to fetch
   */
  val ERROR_IFU           = 0x1

  /**
   * error executing instruction: invalid instruction
   *
   * info0: PC of the instruction
   */
  val ERROR_EXU_INVALID   = 0x2

  /**
   * error loading data from memory
   *
   * info0: PC of the instruction
   */
  val ERROR_MAU_LOAD      = 0x3

  /**
   * error storing data to memory
   *
   * info0: PC of the instruction
   */
  val ERROR_MAU_STORE     = 0x4

  implicit class AddMethodsToErrorRaw(module: ErrorRaw) {
    def setDefaultInfo() = {
      module.io.info0 := 0.U
    }
  }
}
