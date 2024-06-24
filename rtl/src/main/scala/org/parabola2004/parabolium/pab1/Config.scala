package org.parabola2004.parabolium.pab1

import chisel3._
import org.parabola2004.parabolium.pab1.Defines.XLEN

/*
 * Some Notes:
 *   - Faster substitution implementations for simulator case should pass formal equivalence verification.
 */

/**
 * Configurations that control the generation of verilog
 *
 * @param sim whether we are generating verilog for a simulator. Setting `true` for faster simulation
 * @param test whether we are in test environment
 *             (this will replace black boxes with Chisel modules, enable assert statements for formal test)
 * @param resetPC the PC reset value
 */
case class Config(sim: Boolean = false, test: Boolean = false, resetPC: UInt = 0.U(XLEN.W))
