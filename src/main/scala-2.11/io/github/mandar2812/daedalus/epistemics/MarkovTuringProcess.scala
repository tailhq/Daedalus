package io.github.mandar2812.daedalus.epistemics

import breeze.linalg.DenseVector
import breeze.stats.distributions.Multinomial
import io.github.mandar2812.daedalus.utils.TupleIntegerEncoding
import io.github.mandar2812.dynaml.pipes.DataPipe
import io.github.mandar2812.dynaml.probability.RandomVariable

/**
  * Created by mandar on 06/10/2016.
  */
class MarkovTuringProcess(num_states: Int, conditional: ((Int, Int)) => DenseVector[Double], cellStates: Int = 2)
  extends DataPipe[(Int, Int), RandomVariable[(Int, Int, Int)]] {

  assert(conditional(0,0).length == cellStates*3*num_states,
    "Product of bit cardinality * direction cardinality * state cardinality "+
      "must match with Multinomial parameter vector length")

  val encoding = new TupleIntegerEncoding(List(2, 3, num_states))

  def _num_states = num_states

  override def run(data: (Int, Int)): RandomVariable[(Int, Int, Int)] = {
    MeasurableFunction(RandomVariable(new Multinomial(conditional(data))))(
      DataPipe((n: Int) => {
        val l = encoding.i(n)
        (l.head, l(1), l(2))
      })
    )
  }
}
