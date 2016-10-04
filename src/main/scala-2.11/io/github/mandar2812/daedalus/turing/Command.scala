package io.github.mandar2812.daedalus.turing

import java.util.Objects

import io.github.mandar2812.daedalus.turing.CellState.CellState
import io.github.mandar2812.daedalus.turing.Direction.Direction


/**
 * A structure describing a transition performed in some turing machine state
 * in case of the particular state of the current cell.
 */
trait Command extends Equals{
	/**
	 * State to set current cell to. If [[http://www.scala-lang.org/api/2.11.7/#scala.None$]], don't change the state.
	 */
	def cellNewState: Option[CellState]

	/**
	 * Direction to move the caret to. If [[http://www.scala-lang.org/api/2.11.7/#scala.None$]], don't move anywhere.
	 * The movement is performed after changing the state of the current cell if it's needed.
	 */
	def movement: Option[Direction]

	/**
	 * The state to set the machine to. This can be the current or the other state.
	 * This transition is performed after changing the state and moving the caret, if any of them is needed.
	 */
	def nextState: State

	/**
	 * The string representation of the command.
	 * The grammar:
	 * `(cellNewState movement nextStateId)|(movement nextStateId)`.
	 * `cellNewState := (0|1)` - see [[cellNewState]]. Missing if `cellNewState == None`.
	 * `movement := (L|R|N)` - `L` if `movement == Some(Left)`, `R` if `movement == Some(Right)`,
	 * `N` if `movement == None`.
	 * `nextStateId` - the id of the next state. See [[nextState]] and [[State.id]].
	 */
	override def toString: String = {
		val setCellStr = cellNewState map {_.toString + ' '} getOrElse ""
		val moveStr = movement map {_.toString} getOrElse "N"
		s"$setCellStr$moveStr ${nextState.id}"
	}

	/**
	 * @return true if `obj` is an instance of [[Command]]
	 *      and `(obj canEqual this)` is `true` and all 3 attributes are equal for `this`
	 *      and `obj` ([[cellNewState]], [[movement]], [[nextState]]).
	 */
	override def equals(obj: scala.Any): Boolean = obj match {
		case that: Command =>
			(that != null) &&
				(that canEqual this) &&
				(this.cellNewState == that.cellNewState) &&
				(this.movement == that.movement) &&
				(this.nextState == that.nextState)
		case _ => false
	}

	override def hashCode(): Int = Objects.hash(cellNewState, movement,nextState)

	/**
	 * @return `true` if `that` is an instance of [[Command]], `false` otherwise.
	 */
	override def canEqual(that: Any): Boolean = (that != null) && that.isInstanceOf[Command]
}

object Command{
	/**
	 * An extractor to use pattern matching:
	 * {{{
	 *     command match {
	 *          case Command(cellNewState, movement, stateId) => ???
	 *     }
	 * }}}
	 */
	def unapply(command: Command): Option[(Option[CellState], Option[Direction], State)] =
		Some((command.cellNewState, command.movement, command.nextState))

	/**
	 * An immutable implementation of [[Command]].
	 */
	case class Immutable(
		                    override val cellNewState: Option[CellState],
		                    override val movement: Option[Direction],
		                    override val nextState: State) extends Command

	def apply(cellNewState: Option[CellState] = None, movement: Option[Direction] = None, nextState: State): Immutable =
		Immutable(cellNewState, movement, nextState)

	/**
	 * A mutable implementation of [[Command]].
	 * [[Command.cellNewState]],
	 * [[Command.movement]]
	 * and [[Command.nextState]] can be changed.
	 */
	class Mutable extends Command {
		private var _cellNewState: Option[CellState] = None
		private var _movement: Option[Direction] = None
		private var _nextState: Option[State] = None

		override def cellNewState: Option[CellState] = _cellNewState

		def cellNewState_=(state: Option[CellState]): Unit ={
			_cellNewState = state
		}

		override def nextState: State = _nextState.get

		def nextState_=(state: State): Unit ={
			_nextState = Some(state)
		}

		override def movement: Option[Direction] = _movement

		def movement_=(m: Option[Direction]): Unit ={
			_movement = m
		}
	}
}