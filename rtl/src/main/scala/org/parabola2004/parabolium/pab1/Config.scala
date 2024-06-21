package org.parabola2004.parabolium.pab1

/*
 * Some Notes:
 *   - Faster substitution implementations for simulator case should pass formal equivalence verification.
 *     Only then can we be confident about simulation results.
 */

/**
 * Configurations that control the generation of verilog
 *
 * @param sim whether we are generating verilog for a simulator. Setting `true` for faster simulation
 * @param test whether we are in test environment
 *             (this will replace black boxes with Chisel modules, enable assert statements for formal test)
 */
case class Config(sim: Boolean = false, test: Boolean = false)
