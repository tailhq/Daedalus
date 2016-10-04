package io.github.mandar2812.daedalus.turing.math00

import io.github.mandar2812.daedalus.turing.build.Dsl._
import org.scalatest.{FlatSpec, Matchers}

class DecrementTest extends FlatSpec with Matchers {
	"Decrement" should "return 1 for 2" in {
		val tape = "0110".finiteTape
		tape(Decrement)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("01")
	}

	it should "return 0 for 1" in {
		val tape = "010".finiteTape
		tape(Decrement)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("0")
	}

	it should "return 0 for 0" in {
		val tape = "00".finiteTape
		tape(Decrement)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("0")
	}
}