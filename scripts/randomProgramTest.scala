import breeze.linalg.DenseVector
import io.github.mandar2812.daedalus.utils._
import io.github.mandar2812.daedalus.epistemics._

import scala.util.Random


val states = 2

val baseVec = () => DenseVector.tabulate[Double](6*states)(i => Random.nextDouble())

val v1 = baseVec()
v1:/=norm(v1, 1)

val v2 = baseVec()
v1:/=norm(v1, 1)

val v3 = baseVec()
v1:/=norm(v1, 1)

val cond = (c: (Int, Int)) => {
  if (c._1 == 1) (v1+v2):/2.0 else (v2+v3):/2.0
}

val dP = new MarkovTuringProcess(states, cond)

val progRV = new RandomProgram(states, dP)


(1 to 1000).map(_ => progRV.sample()).map(p => {
  val tape1 = "0101111".tape
  println("Tape Before: ")
  println(tape1)
  try {
    tape1(p)
    println("Result")
    println(tape1)
    tape1.toString
  } catch {
    case e: StackOverflowError =>
      println(e.getMessage)
      ""
    case e: Throwable =>
      println(e.getMessage)
      ""
  }
  println("\n")
})
