package org.parabola2004.parabolium

/*
 * Some Notes:
 *   Faster substitution modules for simulator case should pass formal equivalence verification.
 */

/**
 * Configurations that control the generation of verilog
 *
 * @param sim Whether we are generating verilog for a simulator. Setting `true` for faster simulation.
 */
case class Config(sim: Boolean = false)
