package io.github.mandar2812.daedalus.turing

import io.github.mandar2812.daedalus.turing.State.{NonTerminal, Terminal}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * A program that can be executed on a turing machine.
 * @param initialState a state of the turing machine when starting the execution of the program.
 */
class Program(val initialState: State.NonTerminal) extends Equals{
	private lazy val states2Indexes: Map[State.NonTerminal, Int] = {
		val buf = new mutable.HashMap[State.NonTerminal, Int]()
		val statesQueue = new ListBuffer[State.NonTerminal]
		statesQueue append initialState

		def process(state: State.NonTerminal): Unit = {
			if(!buf.contains(state)){
				buf += state -> (buf.size + 1)
				val commands: List[Command] = state.commands.toList sortBy {_._1} map {_._2}
				val nextStates: List[State.NonTerminal] = commands collect {
					case Command(_, _, s: State.NonTerminal) => s
				}
				nextStates foreach {s => if(!buf.contains(s)) statesQueue append s}
			}
		}

		do{
			process(statesQueue.remove(0))
		} while (statesQueue.nonEmpty)
		buf.toMap
	}

	private lazy val states: List[State.NonTerminal] = states2Indexes.toList sortBy {_._2} map {_._1}

	private lazy val normalizedString = {
		def commandToString(command: Command): String = {
			val setCellStr = command.cellNewState map {_.toString} getOrElse ""
			val moveStr = command.movement map {_.toString} getOrElse (if(command.cellNewState.isDefined) "N" else "")
			val nextStateIndex = command.nextState match {
				case _: Terminal => 0
				case nextState: NonTerminal => states2Indexes(nextState)
			}
			s"$setCellStr$moveStr$nextStateIndex"
		}

		def stateToString(state: State.NonTerminal): String =
			s"${commandToString(state.zeroCommand)} ${commandToString(state.oneCommand)}"

		states map stateToString mkString "\n"
	}

	/**
	 * A string representation of the program.
	 * Each line respects to a state of the program.
	 * @see [[State.NonTerminal.toString]]
	 */
	override lazy val toString: String = states mkString "\n"

	override lazy val hashCode: Int = normalizedString.hashCode

	/**
	 * @return `true` only if the following conditions are true:
	 * <br />- `obj` is an instance of [[Program]]
	 * <br />- {{{obj.asInstanceOf[Program].canEqual(this)}}}
	 * <br />- `this` and `obj` are semantically equivalent programs,
	 * i.e. always have equal results for the equal initial data.
	 * It is not guaranteed for this method to return `true` for two semantically equivalent programs.
	 */
	override def equals(obj: scala.Any): Boolean = obj match {
		case that: Program => (that != null) && (that canEqual this) && (this.normalizedString == that.normalizedString)
		case _ => false
	}

	/**
	 * @return `true` if `that` is an instance of [[Program]], `false` otherwise.
	 */
	override def canEqual(that: Any): Boolean = (that != null) && that.isInstanceOf[Program]
}

object Program{
	/**
	 * Creates a program with the given initial state
	 */
	def apply(initialState: State.NonTerminal): Program = new Program(initialState)
}