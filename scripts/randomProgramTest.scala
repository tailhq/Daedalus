import breeze.linalg.DenseVector
import breeze.stats.distributions.{Dirichlet, Multinomial}
import io.github.mandar2812.daedalus.epistemics._
import io.github.mandar2812.dynaml.probability.RandomVariable
import io.github.mandar2812.dynaml.analysis.VectorField
import breeze.stats.distributions._

import scala.util.Random


val states = 5

implicit val ev = VectorField(6*states)

val baseVec = () => DenseVector.tabulate[Double](6*states)(i => math.abs(2.0*Random.nextGaussian()))

val keys = for(state <- 0 until states; cell <- 0 to 1) yield (state, cell)

val dirichlet = RandomVariable(new Dirichlet(baseVec()))

val stateProbMap = keys.map(i => {
  (i, dirichlet.sample())
}).toMap


val cond = (c: (Int, Int)) => {
  stateProbMap(c)
}

val dP = new MarkovTuringProcess(states, cond)

val progRV = new RandomProgram(states, dP)


val res: Seq[Option[String]] = (1 to 10000).map(_ => progRV.sample()).map(p => {
  val tape1 = "01010101".finiteTape
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
}).toSeq.filterNot(c => c == None || c == Some(">01010101"))


val stPrior = MeasurableFunction(RandomVariable(new Poisson(4.5)))(DataPipe((x: Int) => x+2))

val dirModel = new DirichletTuringModel(stPrior)


val res: Seq[(Int, Option[String])] = (1 to 50).map(_ => {
  val s = dirModel.sample()

  (1 to 2000).map(_ => {
    (s._1, s._2.sample())
  }).toSeq

}).flatten.map(p => {
  val tape1 = "00000101".finiteTape
  println("Tape Before: ")
  println(tape1)
  try {
    tape1(p._2)
    println("Result")
    println(tape1)
    (p._1, Some(tape1.toString))
  } catch {
    case e: StackOverflowError => (p._1, None)
    case e: Exception => (p._1, None)
  } finally {
    println("\n")
  }
}).toSeq.filterNot(c => c._2 == None)
