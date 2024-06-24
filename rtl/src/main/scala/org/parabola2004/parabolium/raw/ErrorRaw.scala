package org.parabola2004.parabolium.raw

import chisel3._
import org.parabola2004.parabolium.raw.ErrorRaw.ERROR_TYPE_WIDTH

/**
 * a raw module that informs the simulator of an unexpected error
 */
class ErrorRaw extends BlackBox {
  val io = IO(new Bundle {
    val error       = Input(Bool())
    val error_type  = Input(UInt(ERROR_TYPE_WIDTH.W))
    val info0       = Input(UInt(32.W))
    val info1       = Input(UInt(32.W))
  })
}

object ErrorRaw {
  val ERROR_TYPE_WIDTH    = 3

  /**
   * no error
   */
  val ERROR_NONE          = 0x0

  /**
   * IFU error
   *
   * info0[0]:
   *   0 -> misaligned address (PC not aligned to IALIGN),
   *   1 -> failed to access memory
   *
   * info1:
   *   PC that was used to fetch
   */
  val ERROR_IFU           = 0x1

  /**
   * IDU error: invalid instruction
   *
   * info1:
   *   PC of the instruction
   */
  val ERROR_IDU           = 0x2

  /**
   * EXU error: execution error
   *
   * info0[0]:
   *   0 -> jump instruction-address-misaligned exception (target PC not aligned to IALIGN)
   *
   * info1:
   *   PC of the instruction
   */
  val ERROR_EXU           = 0x3

  /**
   * MAU error
   *
   * info0[0]:
   *   0 -> misaligned load/store,
   *   1 -> failed to access memory
   *
   * info0[1]:
   *   when(info[0] == 1):
   *     0 -> failed to load
   *     1 -> failed to store
   *
   * info1:
   *   PC of the instruction
   */
  val ERROR_MAU           = 0x4

  implicit class AddMethodsToErrorRaw(module: ErrorRaw) {
    def setDefaultInfo() = {
      module.io.info0 := 0.U
      module.io.info1 := 0.U
    }
  }
}
