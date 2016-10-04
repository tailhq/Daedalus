package io.github.mandar2812.daedalus.turing

import io.github.mandar2812.daedalus.turing.CellState._
import io.github.mandar2812.daedalus.turing.Direction._
import io.github.mandar2812.daedalus.turing.State.NonTerminal
import io.github.mandar2812.daedalus.turing.OutOfBoundsException._

import scala.collection.mutable.ArrayBuffer

/**
 * A sequence of the cells considered a component of a turing machine.
 * Tapes represented by the instances of this class can be expanded to the right
 * or shortened from the right if necessary, but extended classes are allowed to change this behavior.
 * Actually, an instance does not store the cells containing `0`
 * and being to the right of the caret position and the most right cell containing `1`.
 * @param initData a sequence of the initial states of the cells.
 *                 The cells being to the right of the first cell and the most right cell containing `1` are ignored.
 *                 For example,
 *                 the instance created via `new Tape(Zero, One, One, Zero, Zero)` will contain only the first 3 cells.
 */
class Tape(initData: CellState*) extends Equals{
	private val _cells: ArrayBuffer[CellState] = {
		val size: Int = Math.max(1, initData.lastIndexOf(One) + 1)
		ArrayBuffer((if(initData.isEmpty) Seq(Zero) else initData take size):_*)
	}

	private var _caretIndex: Int = 0

	/**
	 * @return a state of the cell at the current position of the caret.
	 */
	def currentCell: CellState = _cells(_caretIndex)

	/**
	 * Executes the program on the current tape until the terminal state is reached.
	 * @throws OutOfBoundsException if the program tries to move caret out of the permitted bounds of the tape.
	 * @throws Left if `caretIndex == 0` and the program tries to move the caret left.
	 */
	def apply(program: Program): Unit = apply(program.initialState)

	/**
	 * Executes a program starting from the provided state until the terminal state is reached.
	 * @throws OutOfBoundsException if the program tries to move caret out of the permitted bounds of the tape.
	 * @throws Left if `caretIndex == 0` and the program tries to move the caret left.
	 */
	protected def apply(state: State): Unit = state match {
		case NonTerminal(_, commands) =>
			val command = commands(currentCell)
			apply(command)
			apply(command.nextState)
		case _ =>
	}

	/**
	 * Performs the command on the current tape.
	 * @throws OutOfBoundsException if the command tries to move caret out of the permitted bounds of the tape.
	 * @throws Left if `caretIndex == 0` and the command tries to move the caret left.
	 */
	protected def apply(command: Command): Unit = {
		command.cellNewState foreach {_cells(_caretIndex) = _}
		command.movement foreach moveCaret
	}

	/**
	 * Moves the caret to the direction.
	 * @throws OutOfBoundsException if it is tried to move caret out of the permitted bounds of the tape.
	 * @throws Left if `direction == Direction.Left && caretIndex == 0`.
	 */
	protected def moveCaret(direction: Direction): Unit = {
		if(direction == Direction.Left && _caretIndex == 0){
			throw new Left()
		}
		if(_caretIndex == _cells.size - 1){
			if(direction == Direction.Right){
				_cells append Zero
			}
			else if(currentCell == Zero){
				_cells.remove(_caretIndex)
			}
		}
		_caretIndex = if(direction == Direction.Left) _caretIndex - 1 else _caretIndex + 1
	}

	/**
	 * @return a 0-based index representing the current position of the caret.
	 */
	def caretIndex: Int = _caretIndex

	/**
	 * @return currently stored cells.
	 * The cells containing `0` and being to the right of the caret position
	 * and the most right cell containing `1` are not stored.
	 */
	def cells: Seq[CellState] = _cells

	/**
	 * @return a string representation of the tape.
	 * @example `"01>1"` is returned for the tape containing (`0`, `1`, `1`) with `caretIndex == 2`
	 */
	override def toString: String = {
		val prefix: String = if(_caretIndex == 0) "" else _cells.take(_caretIndex - 1).mkString
		val suffix: String = _cells.takeRight(_cells.size - _caretIndex).mkString
		s"$prefix>$suffix"
	}

	override def hashCode(): Int = (cells, _caretIndex).hashCode()

	/**
	 * @return true if and only if the following conditions are true:
	 * <br />- `obj` is an instance of [[Tape]];
	 * <br />- {{{obj.asInstanceOf[Tape] canEqual this}}}
	 * <br />- {{{(this._cells == that._cells) && (this._caretIndex == that._caretIndex)}}}
	 */
	override def equals(obj: scala.Any): Boolean = obj match {
		case that: Tape =>
			(that != null) && (that canEqual this) &&
				(this._cells == that._cells) && (this._caretIndex == that._caretIndex)
		case _ => false
	}

	/**
	 * @return `true` if and only if `that` is an instance of [[Tape]].
	 */
	override def canEqual(that: Any): Boolean = that != null && that.isInstanceOf[Tape]
}

object Tape{

	/**
	 * A right-bounded tape.
	 * `caretIndex` must always be less than `otherCells.size+1`.
	 * @param cell1 the first cell.
	 * @param otherCells the 2nd, 3rd, etc. cells, if present.
	 */
	class Finite(cell1: CellState, otherCells: CellState*) extends Tape(cell1 +: otherCells:_*) {
		/**
		 * Moves the caret to the direction.
		 * @throws OutOfBoundsException if it is tried to move caret out of the permitted bounds of the tape.
		 * @throws Left if {{{direction == Direction.Left && caretIndex == 0}}}
		 * @throws Right if
		 *                                         {{{direction == Direction.Right && caretIndex == otherCells.size}}}
		 */
		override protected def moveCaret(direction: Direction): Unit = {
			if(direction == Direction.Right && caretIndex == otherCells.size){
				throw new Right
			} else super.moveCaret(direction)
		}
	}

}

/**
  * An exception being thrown when a program tries to move the caret out of the permitted bounds of the tape.
  */
class OutOfBoundsException(message: String = null, cause: Option[Throwable] = None)
  extends RuntimeException(message){

  cause foreach initCause
}

object OutOfBoundsException{
  /**
    * An exception being thrown when a program tries to move the caret left from the left bound of the tape.
    */
  class Left(message: String = "Cannot move left", cause: Option[Throwable] = None)
    extends OutOfBoundsException(message, cause)

  /**
    * An exception being thrown when a program tries to move the caret right from the right bound of the tape.
    */
  class Right(message: String = "Cannot move right", cause: Option[Throwable] = None)
    extends OutOfBoundsException(message, cause)
}
