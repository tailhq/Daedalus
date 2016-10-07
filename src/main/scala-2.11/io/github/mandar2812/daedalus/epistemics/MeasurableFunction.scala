package io.github.mandar2812.daedalus.epistemics

import io.github.mandar2812.dynaml.pipes.DataPipe
import io.github.mandar2812.dynaml.probability.RandomVariable

/**
  * Created by mandar on 06/10/2016.
  */
class MeasurableFunction[
Domain1, Domain2](baseRV: RandomVariable[Domain1])(func: DataPipe[Domain1, Domain2])
  extends RandomVariable[Domain2] {
  override val sample: DataPipe[Unit, Domain2] = baseRV.sample > func
}

object MeasurableFunction {
  def apply[Domain1, Domain2](baseRV: RandomVariable[Domain1])(func: DataPipe[Domain1, Domain2])
  : MeasurableFunction[Domain1, Domain2] = new MeasurableFunction[Domain1, Domain2](baseRV)(func)
}