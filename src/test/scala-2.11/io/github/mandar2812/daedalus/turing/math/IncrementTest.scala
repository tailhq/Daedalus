package io.github.mandar2812.daedalus.turing.math

import io.github.mandar2812.daedalus.turing.build.Dsl._
import org.scalatest.{FlatSpec, Matchers}

class IncrementTest extends FlatSpec with Matchers{
	"Increment" should "return 1 for 0" in {
		val tape = "00".finiteTape
		tape(Increment)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("01")
	}

	it should "return 2 for 1" in {
		val tape = "010".finiteTape
		tape(Increment)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("011")
	}

	it should "return 3 for 2" in {
		val tape = "0110".finiteTape
		tape(Increment)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("0111")
	}
}