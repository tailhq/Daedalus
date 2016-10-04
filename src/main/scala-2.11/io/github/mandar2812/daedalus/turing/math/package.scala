package io.github.mandar2812.daedalus.turing

import io.github.mandar2812.daedalus.turing.build.ProgramBuilder
import CellState._
import Direction._
import io.github.mandar2812.daedalus.turing.build.Dsl._

package object math{
	/**
	 * Adds `1` to the number.
	 * @example `0` -> `01`
	 * @example `01` -> `011`
	 * @example `011` -> `0111`
	 */
	val Increment: Program = ProgramBuilder(
		"q1" -> R~"q2",
		"q2" -> (`1` -> R.c, `0` -> `1`~L~"q3"),
		"q3" -> (`1` -> L.c, `0` -> "q0".c)
	).toProgram
}