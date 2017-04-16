import breeze.linalg.DenseVector
import breeze.stats.distributions.{Dirichlet, Multinomial}
import io.github.mandar2812.daedalus.epistemics._
import io.github.mandar2812.dynaml.probability.{DiscreteDistrRV, MeasurableFunction, RandomVariable}
import io.github.mandar2812.dynaml.analysis.VectorField
import breeze.stats.distributions._
import io.github.mandar2812.daedalus.turing.Tape

import scala.util.Random


val states = 5

implicit val ev = VectorField(6*states)

val baseVec = () => DenseVector.tabulate[Double](6*states)(i => scala.math.abs(2.0*Random.nextGaussian()))

val keys = for(state <- 0 until states; cell <- 0 to 1) yield (state, cell)

val dirichlet = RandomVariable(new Dirichlet(baseVec()))

val stateProbMap = keys.map(i => {
  (i, dirichlet.draw)
}).toMap


val cond = (c: (Int, Int)) => {
  stateProbMap(c)
}

val dP = new MarkovTuringProcess(states, cond)

val progRV = new RandomProgram(states, dP)


val res1 : Seq[Option[String]] = (1 to 10000).map(_ => progRV.sample()).map(p => {
  val tape1: Tape.Finite = "01010101".finiteTape
  println("Tape Before: ")
  println(tape1)
  try {
    tape1(p)
    println("Result")
    println(tape1)
    Some(tape1.toString)
  } catch {
    case e: StackOverflowError => None
    case e: Exception => None
  } finally {
    println("\n")
  }
}).filterNot(c => c.isEmpty || c.contains(">01010101"))


val stPrior = MeasurableFunction[Int, Int, DiscreteDistrRV[Int]](RandomVariable(new Poisson(3.5)))((x: Int) => x+2)

val dirModel = new DirichletTuringModel(stPrior)


val str = "00101"

val res2 = (1 to 10).flatMap(_ => {
  val s = dirModel.draw

  (1 to 4000).map(_ => {
    (s._1, s._2.draw)
  })

}).map(p => {
  val tape1: Tape.Finite = str.finiteTape
  println("Tape Before: ")
  println(tape1)
  try {
    tape1(p._2)
    println("Result")
    println(tape1)
    (p._1, p._2, Some(tape1.toString))
  } catch {
    case e: StackOverflowError => (p._1, p._2, None)
    case e: Exception => (p._1, p._2, None)
  } finally {
    println("\n")
  }
}).filterNot(c => c._3.isEmpty)
