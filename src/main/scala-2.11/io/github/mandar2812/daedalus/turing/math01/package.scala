package io.github.mandar2812.daedalus.turing

import io.github.mandar2812.daedalus.turing.build.ProgramBuilder
import CellState._
import Direction._
import io.github.mandar2812.daedalus.turing.build.Dsl._

package object math01{
	/**
	 * Returns `(x-1)` for a number `x`; `0` for `0`.
	 * @example `0111` -> `011`
	 * @example `011` -> `01`
	 * @example `01` -> `01`
	 */
	val Decrement: Program = ProgramBuilder(
		"q1" -> (`0` -> R.c, `1` -> R~"q2"),
		"q2" -> (`0` -> L~"q5", `1` -> R~"q3"),
		"q3" -> (`1` -> R.c, `0` -> L~"q4"),
		"q4" -> `0`~L~"q5",
		"q5" -> (`1` -> L.c, `0` -> "q0".c)
	).toProgram

	/**
	 * Returns the sum of 2 numbers.
	 * @example `01010` -> `01`
	 * @example `010110` -> `011`
	 * @example `011010` -> `011`
	 * @example `0110110` -> `0111`
	 */
	val Sum: Program = ProgramBuilder(
		"q1" -> (`0` -> R~"q2", `1` -> `0`~L~"q5"),
		"q2" -> (`1` -> R.c, `0` -> `1`~R~"q3"),
		"q3" -> (`1` -> R.c, `0` -> L~"q4"),
		"q4" -> `0`~L~"q1",
		"q5" -> (`1` -> L.c, `0` -> "q0".c)
	).toProgram
}