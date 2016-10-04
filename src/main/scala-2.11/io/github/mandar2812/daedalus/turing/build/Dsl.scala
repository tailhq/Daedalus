package io.github.mandar2812.daedalus.turing.build

import io.github.mandar2812.daedalus.turing.CellState._
import io.github.mandar2812.daedalus.turing.Direction.Direction
import io.github.mandar2812.daedalus.turing.Implicits
import io.github.mandar2812.daedalus.turing.State.{Id => StateId}
import io.github.mandar2812.daedalus.turing.build.ProgramBuilder.{Command, State}

/**
 * Implicit conversions to use DSL for creating turing machine programs.
 */
trait Dsl extends Implicits{
	/**
	 * Creates a command specification containing the given state ID with no modification of the cell and movement.
	 */
	implicit val commandFromNextStateId: StateId => Command = Command(None, None, _)

	/**
	 * Creates a command builder containing the given state to be set a cell to.
	 */
	implicit val modifyingCommandFromCellNewState: CellState => Command.Incomplete.Modifying =
		Command.Incomplete.Modifying

	/**
	 * Creates a command builder containing the given direction to move the caret.
	 */
	implicit val movingCommandFromMovement: Direction => Command.Incomplete.Moving = Command.Incomplete.Moving

	/**
	 * @param params A pair of the state ID and the command.
	 * @return A specification of the state with the given ID and command.
	 */
	implicit def stateFromIdAndCommand(params: (StateId, Command)): State = {
		val (id, command) = params
		State(id, command, command)
	}

	/**
	 * @param params A triple containing the state ID and 2 pairs of cell states and commands.
	 *               The cell states must be different, otherwise an exception is thrown.
	 * @return A specification of the state with the given id and commands.
	 */
	implicit def stateFromIdAndCommands(params: (StateId, ((CellState, Command), (CellState, Command)))): State = {
		val (id, ((cellState1, command1), (cellState2, command2))) = params
		require(cellState1 != cellState2, s"Cell state `$cellState1` is duplicated")
		val (zeroCommand, oneCommand) = if(cellState1 == Zero) (command1, command2) else (command2, command1)
		State(id, zeroCommand = zeroCommand, oneCommand = oneCommand)
	}

	/**
	 * @param params A triple containing the state ID,
	 *               the pair of the cell state and the command,
	 *               and the pair of the cell state and the command builder.
	 *               The command that will be built from the command builder will not transit to another state.
	 * @return A specification of the state with the given id and commands.
	 */
	implicit def stateFromIdAndCommandAndIncompleteCommand(
                              params: (StateId, ((CellState, Command), (CellState, Command.Incomplete)))): State = {

		val (id, (cellStateAndCommand1, (cellState2, command2))) = params
		stateFromIdAndCommands(id -> (cellStateAndCommand1, cellState2 -> command2 ~ id))
	}

	/**
	 * @param params A triple containing the state ID,
	 *               the pair of the cell state and the command builder,
	 *               and the pair of the cell state and the command.
	 *               The command that will be built from the command builder will not transit to another state.
	 * @return A specification of the state with the given id and commands.
	 */
	implicit def stateFromIdAndIncompleteCommandAndCommand(
                              params: (StateId, ((CellState, Command.Incomplete), (CellState, Command)))): State = {

		val (id, (cellStateAndCommand1, cellStateAndCommand2)) = params
		stateFromIdAndCommandAndIncompleteCommand(id -> (cellStateAndCommand2, cellStateAndCommand1))
	}
}

object Dsl extends Dsl