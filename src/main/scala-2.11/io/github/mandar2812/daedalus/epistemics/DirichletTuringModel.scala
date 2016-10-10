package io.github.mandar2812.daedalus.epistemics

import breeze.linalg.DenseVector
import breeze.stats.distributions.Dirichlet
import io.github.mandar2812.dynaml.analysis.VectorField
import io.github.mandar2812.dynaml.pipes.DataPipe
import io.github.mandar2812.dynaml.probability.{ProbabilityModel, RandomVariable}

import scala.util.Random

/**
  * Created by mandar on 10/10/2016.
  */
class DirichletTuringModel(priorNumStates: RandomVariable[Int], cellStates: Int = 2)
  extends EpistemicTuringModel[Int](
    priorNumStates,
    DataPipe((states) => {
      implicit val ev = VectorField(3*cellStates*states)
      val baseVec = () => DenseVector.tabulate[Double](3*cellStates*states)(i => math.abs(2.0*Random.nextGaussian()))
      val dirichlet = RandomVariable(new Dirichlet(baseVec()))
      val keys = for(state <- 0 until states; cell <- 0 until cellStates) yield (state, cell)
      val stateProbMap = keys.map(i => {
        (i, dirichlet.sample())
      }).toMap

      val cond = (c: (Int, Int)) => {
        stateProbMap(c)
      }

      new MarkovTuringProcess(states, cond, cellStates)
  }))
