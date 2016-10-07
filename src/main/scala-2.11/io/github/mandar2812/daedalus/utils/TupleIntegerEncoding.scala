package io.github.mandar2812.daedalus.utils

import io.github.mandar2812.dynaml.pipes.DataPipe

/**
  * Created by mandar on 06/10/2016.
  */
class TupleIntegerEncoding(arities: List[Int]) extends DataPipe[List[Int], Int] {

  assert(arities.forall(_ >= 2), "The arities must be greater than 1")

  def encodeAcc(l: List[(Int, Int)], productAcc: Int, acc: Int): Int = l match {
    case List() => acc
    case e::tail => encodeAcc(tail, productAcc*e._2, acc+e._1*productAcc)
  }

  def decodeAcc(n: (Int, List[Int]), modAcc: Int, acc: List[Int]): List[Int] = n match {
    case (v, List()) => acc++List(v)
    case (0, _) => List.fill[Int](arities.length)(0)
    case (v, h::tail) => decodeAcc((v/modAcc, tail), h, acc++List(v%modAcc))
  }

  override def run(data: List[Int]): Int = {
    val dataZipped = data zip arities
    assert(
      dataZipped.forall(c => c._1 < c._2),
      "Elements must be from 0 to p-1 where p is aritiy for the element")
    encodeAcc(dataZipped, 1, 0)
  }

  val i: DataPipe[Int, List[Int]] = DataPipe((n: Int) => decodeAcc((n, arities.tail), arities.head, List()))
}
