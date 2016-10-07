import breeze.linalg.DenseVector
import io.github.mandar2812.daedalus.utils._
import io.github.mandar2812.daedalus.epistemics._

import scala.util.Random


val states = 4

val baseVec = () => DenseVector.tabulate[Double](6*states)(i => Random.nextDouble())

val keys = for(state <- 0 until states; cell <- 0 to 1) yield (state, cell)

val stateProbMap = keys.map(i => {
  val v1 = baseVec()
  (i, v1:/norm(v1, 1))
}).toMap


val cond = (c: (Int, Int)) => {
  stateProbMap(c)
}

val dP = new MarkovTuringProcess(states, cond)

val progRV = new RandomProgram(states, dP)


val res: Seq[Option[String]] = (1 to 1000).map(_ => progRV.sample()).map(p => {
  val tape1 = "01010101".tape
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
}).toSeq
