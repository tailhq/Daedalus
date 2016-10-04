package io.github.mandar2812.daedalus.turing

import State._
import io.github.mandar2812.daedalus.turing.CellState._

/**
 * A state of the turing machine during the execution of a program.
 */
sealed trait State{
	/**
	 * An identifier of the state.
	 */
	val id: Id
}

object State{
	type Id = String

	/**
	 * A terminal state of a program.
	 * No actions are performed after the turing machine reaches the terminal state.
	 * @param id an identifier of the state
	 */
	case class Terminal(override val id: Id = "q0") extends State with Equals {
		override val hashCode: Int = 0

		/**
		 * A string representation of the identifier of the state.
		 */
		override val toString: String = id.toString

		/**
		 * @return `true` if and only if the following conditions are true:
		 * <br />- `obj` is an instance of [[Terminal]];
		 * <br />- {{{obj.asInstanceOf[State.Terminal] canEqual this}}}.
		 */
		override def equals(obj: scala.Any): Boolean = obj match {
			case that: Terminal => (that != null) && (that canEqual this)
			case _ => false
		}

		/**
		 * @return `true` if and only if `that` is an instance of [[Terminal]]
		 */
		override def canEqual(that: Any): Boolean = (that != null) && that.isInstanceOf[Terminal]
	}

	/**
	 * A terminal state with the default identifier.
	 */
	val terminal = Terminal()

	/**
	 * A non-terminal state of a program.
	 * @param id an identifier of the state.
	 * @param zeroCommand a command being executed if the current cell contains
	 *                    [[CellState.Zero]]
	 * @param oneCommand a command being executed if the current cell contains
	 *                   [[CellState.One]]
	 */
	class NonTerminal(override val id: Id, val zeroCommand: Command, val oneCommand: Command) extends State with Equals{
		/**
		 * A map containing states of the current cell and corresponding commands.
		 */
		val commands = Map(Zero -> zeroCommand, One -> oneCommand)

		/**
		 * A string representation of the state.
		 * Grammar: `id: 0 -> zeroCommand, 1 -> oneCommand`, where
		 * `id` - a string representation of the identifier of the state,
		 * `zeroCommand` - a string representation of the command being executed if the current cell contains
		 *                    [[CellState.Zero]],
		 * `oneCommand` - a string representation of the command being executed if the current cell contains
		 *                    [[CellState.One]].
		 */
		override lazy val toString: String = s"$id: 0 -> $zeroCommand, 1 -> $oneCommand"

		override lazy val hashCode: Int = id.hashCode

		/**
		 * @return `true` if and only if the following conditions are true:
		 * <br />- `obj` is an instance of [[NonTerminal]];
		 * <br />- {{{obj.asInstanceOf[NonTerminal] canEqual this}}}
		 * <br />- this object and `obj` have the same `id`.
		 */
		override def equals(obj: scala.Any): Boolean = obj match {
			case that: NonTerminal => (that != null) && (that canEqual this) && (this.id == that.id)
			case _ => false
		}

		/**
		 * @return `true` if and only if {{{that.asInstanceOf[NonTerminal] canEqual this}}}
		 */
		override def canEqual(that: Any): Boolean = (that != null) && that.isInstanceOf[NonTerminal]
	}

	object NonTerminal{
		/**
		 * @example
		 * {{{
		 * obj match {
		 *      case State.NonTerminal(id, commands) =>
		 *          val zeroCommand: Command = commands(CellState.Zero)
		 *          val oneCommand: Command = commands(CellState.One)
		 * }
		 * }}}
		 */
		def unapply(obj: Any): Option[(Id, Map[CellState, Command])] = obj match {
			case that: NonTerminal => Some((that.id, that.commands))
			case _ => None
		}
	}

	/**
	 * @param id an identifier of the command.
	 * @param command a command executed in the state.
	 */
	def apply(id: Id, command: Command): NonTerminal = new NonTerminal(id, command, command)

	/**
	 * @param id an identifier of the command.
	 * @param zeroCommand a command being executed if the current cell contains
	 *                    [[CellState.Zero]]
	 * @param oneCommand a command being executed if the current cell contains
	 *                    [[CellState.One]]
	 */
	def apply(id: Id, zeroCommand: Command, oneCommand: Command): NonTerminal =
		new NonTerminal(id, zeroCommand = zeroCommand, oneCommand = oneCommand)

	/**
	 * @param id an identifier of the command.
	 * @param cellStates2Commands a map containing states of the current cell and corresponding commands.
	 * The size of the map must equal either `0` or `2`, otherwise an exception is thrown.
	 * @return a terminal state if `cellStates2Commands` is empty;
	 *         a non-terminal one if `cellStates2Commands` contains two commands.
	 */
	def apply(id: Id, cellStates2Commands: Map[CellState, Command]): State = {
		require(cellStates2Commands.isEmpty || cellStates2Commands.size == 2)
		if(cellStates2Commands.isEmpty){
			Terminal(id)
		} else {
			new NonTerminal(id, zeroCommand = cellStates2Commands(Zero), oneCommand = cellStates2Commands(One))
		}
	}
}