package io.github.mandar2812.daedalus.turing

import io.github.mandar2812.daedalus.turing.CellState._
import io.github.mandar2812.daedalus.turing.Tape.Finite


/**
 * Some useful implicit conversions.
 */
trait Implicits{
	/**
	 * Parses the list of [[CellState.CellState]].
	 * @param source a serialized list of [[CellState.CellState]].
	 *      The string must match regex `^[0-1]*$`.
	 * @return The list of the parsed cell states.
	 *         Each source character is parsed to a [[CellState.CellState]]:
	 *              `'0'` -> [[CellState.Zero]],
	 *                   `'1'` -> [[CellState.One]].
	 * @throws IllegalArgumentException if `source` does not match regex `^[0-1]*$`.
	 * @example {{{cellStatesFromString("") == Nil}}}
	 * @example {{{cellStatesFromString("01") == List(Zero, One)}}}
	 */
	implicit def cellStatesFromString(source: String): List[CellState] = source.toCharArray map {
		case '0' => Zero
		case '1' => One
		case _ => throw new IllegalArgumentException("Only `0` and `1` digits can be used")
	} toList

	/**
	 * Creates a [[Tape]]
	 * from the serialized list of [[CellState.CellState]].
	 * @param source a serialized list of [[CellState.CellState]].
	 *      The string must match regex `^[0-1]*$`.
	 * @return The list of the parsed cell states. See [[cellStatesFromString]].
	 * @throws IllegalArgumentException if `source` does not match regex `^[0-1]*$`.
	 */
	implicit def tapeFromString(source: String): Tape = new Tape(cellStatesFromString(source):_*)

	/**
	 * Creates a [[Tape.Finite]]
	 * from the serialized list of [[CellState.CellState]].
	 * @param source a serialized list of [[CellState.CellState]].
	 *      The string must match regex `^[0-1]+$`.
	 * @return The list of the parsed cell states. See [[cellStatesFromString]].
	 * @throws IllegalArgumentException if `source` does not match regex `^[0-1]+$`.
	 */
	implicit def finiteTapeFromString(source: String): Tape.Finite = cellStatesFromString(source) match {
		case c :: rest => new Finite(c, rest:_*)
		case Nil => throw new IllegalArgumentException("Empty string is not allowed here")
	}

	/**
	 * A helper class to create instances of [[Tape]] and its subclasses.
	 * @param source a serialized list of [[CellState.CellState]].
	 */
	implicit class TapeBuilder(source: String){
		/**
		 * A [[Tape]] created from `source` string
		 * via [[tapeFromString]].
		 * The string must match regex `^[0-1]*$`.
		 * @throws IllegalArgumentException if `source` does not match regex `^[0-1]*$`.
		 * @example {{{
		 * import com.github.skozlov.turing.Implicits._
		 * val tape = "01".tape
		 * }}}
		 */
		lazy val tape: Tape = tapeFromString(source)

		/**
		 * A [[Tape.Finite]] created from `source` string
		 * via [[finiteTapeFromString]].
		 * The string must match regex `^[0-1]+$`.
		 * @throws IllegalArgumentException if `source` does not match regex `^[0-1]+$`.
		 * @example
		 * {{{
		 * import com.github.skozlov.turing.Implicits._
		 * val tape = "01".finiteTape
		 * }}}
		 */
		lazy val finiteTape: Tape.Finite = finiteTapeFromString(source)
	}
}

object Implicits extends Implicits