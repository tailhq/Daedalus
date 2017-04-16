package io.github.mandar2812.daedalus.epistemics

import io.github.mandar2812.dynaml.pipes.DataPipe
import io.github.mandar2812.dynaml.probability.RandomVariable

/**
  * Created by mandar on 07/10/2016.
  */
class EpistemicTuringModel[Source](
  prior: RandomVariable[Source],
  generator: DataPipe[Source, MarkovTuringProcess])
  extends RandomVariable[(Source, RandomProgram)]{

  override val sample: DataPipe[Unit, (Source, RandomProgram)] = DataPipe(() => {
    val p = prior.draw
    val mkv_tp = generator(p)

    (p, new RandomProgram(mkv_tp._num_states, mkv_tp))
  })
}
