package io.github.mandar2812.daedalus.turing.build

import io.github.mandar2812.daedalus.turing.CellState._
import io.github.mandar2812.daedalus.turing.Direction.Direction
import io.github.mandar2812.daedalus.turing.State.{Id => StateId}
import io.github.mandar2812.daedalus.turing.build.ProgramBuilder.State
import io.github.mandar2812.daedalus.turing.{Direction, Program, State => RealState}

/**
 * A helper class for building turing machine programs.
 */
case class ProgramBuilder private(private val initialStateId: StateId, private val states: Map[StateId, State]){
	require(states contains initialStateId)

	/**
	 * @return a new instance of [[ProgramBuilder]] containing all states of the source builder plus `state`.
	 * If this builder already contains a state with the same ID, an exception is thrown.
	 */
	def +(state: State): ProgramBuilder = {
		require(!states.contains(state.id), s"State `${state.id}` is duplicated")
		ProgramBuilder(initialStateId, states + (state.id -> state))
	}

	/**
	 * @throws InvalidProgramException if this builder cannot be converted to the correct program.
	 * It is not guaranteed for the program to be executed successfully even if this method has not thrown an exception.
	 * In the current version, this exception is never thrown by this method, but is reserved for the future releases.
	 * @return A program built from the states specified in this builder.
	 */
	@throws[InvalidProgramException]
	lazy val toProgram: Program = {
		val realStates: Map[StateId, RealState.NonTerminal] = states map {
			case (stateId, state) =>
				(stateId,
					RealState(stateId, zeroCommand = state.zeroCommand.toReal, oneCommand = state.oneCommand.toReal))
		}
		for(
			(stateId, state) <- realStates;
			stateDescriptor = states(stateId);
			zeroNextStateId = stateDescriptor.zeroCommand.nextStateId;
			oneNextStateId = stateDescriptor.oneCommand.nextStateId
		){
			state.zeroCommand.asInstanceOf[io.github.mandar2812.daedalus.turing.Command.Mutable].nextState =
				realStates.getOrElse(zeroNextStateId, RealState.Terminal(zeroNextStateId))
			state.oneCommand.asInstanceOf[io.github.mandar2812.daedalus.turing.Command.Mutable].nextState =
				realStates.getOrElse(oneNextStateId, RealState.Terminal(oneNextStateId))
		}
		Program(initialState = realStates(initialStateId))
	}

	/**
	 * A string representation of this builder, similar to [[io.github.mandar2812.daedalus.turing.Program.toString]]
	 */
	override lazy val toString: String = {
		val sortedStates: List[State] = states(initialStateId) :: (states - initialStateId).values.toList
		sortedStates mkString "\n"
	}
}

object ProgramBuilder{
	def apply(initialState: State, otherStates: State*): ProgramBuilder = otherStates.toList match {
		case Nil => ProgramBuilder(initialState.id, Map(initialState.id -> initialState))
		case state2 :: rest =>ProgramBuilder(initialState, rest:_*) + state2
	}


	/**
		* A specification of the [[io.github.mandar2812.daedalus.turing.Command]]
		* to be used in [[ProgramBuilder]].
		* @param cellNewState State to set current state to. If `None`, don't change the state.
		* @param movement Direction to move the caret to. If `None`, don't move anywhere.
		* @param nextStateId ID of the state to set the machine to.
		*/
	case class Command(cellNewState: Option[CellState] = None, movement: Option[Direction] = None, nextStateId: StateId){
		/**
			* A string representation of this command, similar to [[io.github.mandar2812.daedalus.turing.Command.toString]]
			*/
		override lazy val toString: String = {
			val setCellStr = cellNewState map {_.toString + ' '} getOrElse ""
			val moveStr = movement map {_.toString} getOrElse "N"
			s"$setCellStr$moveStr $nextStateId"
		}

		/**
			* @return A mutable command specified by this instance.
			* The returned command has {{{nextState == null}}}
			*/
		def toReal: io.github.mandar2812.daedalus.turing.Command.Mutable = {
			val command = new io.github.mandar2812.daedalus.turing.Command.Mutable
			command.cellNewState = cellNewState
			command.movement = movement
			command
		}

		/**
			* This command.
			* It is typically used with [[Dsl.commandFromNextStateId]]
			* to implicitly convert a state ID to the command.
			* @example {{{
			* import io.github.mandar2812.build.Dsl.commandFromNextStateId
			* val command = "q1".c
			* }}}
			*/
		val c = this
	}

	object Command{

		/**
			* A base helper to build commands via DSL.
			*/
		trait Incomplete{
			/**
				* Always equals to `this`.
				* It is typically used to implicitly convert
				* [[io.github.mandar2812.daedalus.turing.Direction.Direction]] or [[io.github.mandar2812.daedalus.turing.CellState.CellState]]
				* to [[Incomplete]].
				* See [[io.github.mandar2812.daedalus.turing.build.ProgramBuilder.Command.Incomplete.Moving]]
				* and [[io.github.mandar2812.daedalus.turing.build.ProgramBuilder.Command.Incomplete.Modifying]] for examples.
				*/
			val c: this.type = this

			/**
				* @return A command built from the attributes of this instance and the given `nextStateId`.
				* @example
				* {{{
				* import io.github.mandar2812.build.Dsl.movingCommandFromMovement
				* import io.github.mandar2812.Direction._
				* val command = L~"q2"
				* }}}
				*/
			def ~(nextStateId: StateId): Command
		}

		object Incomplete{

			/**
				* A helper class to build commands containing the specified `movement` via DSL.
				* @example {{{
				* import io.github.mandar2812.build.Dsl.movingCommandFromMovement
				* import io.github.mandar2812.Direction._
				* val command = L.c
				* }}}
				*/
			implicit class Moving(movement: Direction) extends Incomplete {
				/**
					* See [[Command]].
					* The default value is `None`.
					*/
				protected val cellNewState: Option[CellState] = None

				override def ~(nextStateId: StateId): Command = Command(cellNewState, Some(movement), nextStateId)
			}

			/**
				* A helper class to build commands containing the specified `cellNewState` via DSL.
				* @example {{{
				* import io.github.mandar2812.build.Dsl.modifyingCommandFromCellNewState
				* import io.github.mandar2812.CellState._
				* val command = Zero.c
				* }}}
				*/
			implicit class Modifying(cellNewState: CellState) extends Incomplete {
				override def ~(nextStateId: StateId): Command = Command(Some(cellNewState), None, nextStateId)

				/**
					* @return A [[Moving]] containing `cellNewState` of this and the given `movement`.
					*/
				def ~(movement: Direction): Moving = new Moving(movement){
					override protected val cellNewState: Option[CellState] = Some(Modifying.this.cellNewState)
				}

				/**
					* @return A [[Command]] containing `cellNewState` of this,
					*      `Some(Direction.Left)` and the given `nextStateId`.
					*/
				def left(nextStateId: StateId): Command = this ~ Direction.Left ~ nextStateId

				/**
					* @return A [[Command]] containing `cellNewState` of this,
					*      `Some(Direction.Right)` and the given `nextStateId`.
					*/
				def right(nextStateId: StateId): Command = this ~ Direction.Right ~ nextStateId

				/**
					* See [[left]]
					*/
				val L: StateId => Command = left

				/**
					* See [[right]]
					*/
				val R: StateId => Command = right
			}
		}
	}

	/**
		* A specification of the [[io.github.mandar2812.daedalus.turing.State.NonTerminal]]
		* to be used in [[ProgramBuilder]].
		* @param id An identifier of the state.
		* @param zeroCommand A command being executed if the current cell contains
		*                    [[io.github.mandar2812.daedalus.turing.CellState.Zero]]
		* @param oneCommand A command being executed if the current cell contains
		*                    [[io.github.mandar2812.daedalus.turing.CellState.One]]
		*/
	case class State(id: StateId, zeroCommand: Command, oneCommand: Command){
		/**
			* A string representation of this state,
			* similar to [[io.github.mandar2812.daedalus.turing.State.NonTerminal.toString]].
			*/
		override lazy val toString: String = s"$id: 0 -> $zeroCommand, 1 -> $oneCommand"

		override lazy val hashCode: Int = id.hashCode

		/**
			* @return `true` if and only if the following conditions are true:
			* <br />- `obj` is an instance of [[State]];
			* <br />- `obj.asInstanceOf[State] canEqual this`;
			* <br />- this and `obj` have the same `id`.
			*/
		override def equals(obj: scala.Any): Boolean = obj match {
			case that: State => (that != null) && (that canEqual this) && (this.id == that.id)
			case _ => false
		}
	}


}

