package io.github.mandar2812.daedalus.epistemics

import io.github.mandar2812.daedalus.turing.Program
import io.github.mandar2812.dynaml.probability.RandomVariable
import io.github.mandar2812.daedalus.turing.build.ProgramBuilder
import io.github.mandar2812.daedalus.turing.CellState._
import io.github.mandar2812.daedalus.turing.Direction._
import io.github.mandar2812.daedalus.turing.build.ProgramBuilder.{Command, State}
import io.github.mandar2812.dynaml.pipes.DataPipe

/**
  * Created by mandar on 05/10/2016.
  */
class RandomProgram(
  num_states: Int,
  transitionModel: DataPipe[(Int, Int), RandomVariable[(Int, Int, Int)]])
  extends RandomVariable[Program]{

  val stateEncoding: Map[Int, String] = (0 until num_states).map(i => (i, "q"+i)).toMap

  val bitEncoding = Map(0 -> Some(`0`), 1 -> Some(`1`), 2 -> None)

  val directionEncoding = Map(0 -> None, 1 -> Some(`L`), 2 -> Some(`R`))

  override val sample: DataPipe[Unit, Program] = DataPipe(() => {

    val commands = (1 until num_states).map(stateIndex => {

      //For each state generate a command
      val (zerocommand, onecommand) = {
        val sampleZero = transitionModel((stateIndex, 0)).sample()

        val (cellState1, direction1, newState1) = {
          (bitEncoding(sampleZero._1), directionEncoding(sampleZero._2), stateEncoding(sampleZero._3))
        }

        val sampleOne = transitionModel((stateIndex, 1)).sample()

        val (cellState2, direction2, newState2) = {
          (bitEncoding(sampleOne._1), directionEncoding(sampleOne._2), stateEncoding(sampleOne._3))
        }

        (Command(cellState1, direction1, newState1), Command(cellState2, direction2, newState2))
      }

      State(stateEncoding(stateIndex), zerocommand, onecommand)
    })

    ProgramBuilder(
      commands.head, commands.tail:_*
    ).toProgram

  })
}
