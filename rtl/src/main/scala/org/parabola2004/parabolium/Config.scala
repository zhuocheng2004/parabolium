package org.parabola2004.parabolium

/*
 * Some Notes:
 *   - Faster substitution implementations for simulator case should pass formal equivalence verification.
 *     Only then can we be confident about simulation results.
 */

/**
 * Configurations that control the generation of verilog
 *
 * @param sim whether we are generating verilog for a simulator. Setting `true` for faster simulation
 * @param formalTest whether to enable assert statements for formal tests
 */
case class Config(sim: Boolean = false, formalTest: Boolean = false)
