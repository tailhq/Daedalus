package io.github.mandar2812.daedalus.turing.math00

import org.scalatest.{Matchers, FlatSpec}
import io.github.mandar2812.daedalus.turing.build.Dsl._

class SumTest extends FlatSpec with Matchers {
	"Sum" should "be 0 for 0 and 0" in {
		val tape = "000".finiteTape
		tape(Sum)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("0")
	}

	it should "be 1 for 1 and 0" in {
		val tape = "0100".finiteTape
		tape(Sum)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("01")
	}

	it should "be 2 for 1 and 1" in {
		val tape = "01010".finiteTape
		tape(Sum)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("011")
	}
}