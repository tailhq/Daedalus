package io.github.mandar2812.daedalus.turing.math01

import org.scalatest.{Matchers, FlatSpec}
import io.github.mandar2812.daedalus.turing.build.Dsl._

class SumTest extends FlatSpec with Matchers {
	"Sum" should "be 0 for 0 and 0" in {
		val tape = "01010".finiteTape
		tape(Sum)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("01")
	}

	it should "be 1 for 0 and 1" in {
		val tape = "010110".finiteTape
		tape(Sum)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("011")
	}

	it should "be 1 for 1 and 0" in {
		val tape = "011010".finiteTape
		tape(Sum)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("011")
	}

	it should "be 2 for 1 and 1" in {
		val tape = "0110110".finiteTape
		tape(Sum)
		tape.caretIndex shouldBe 0
		tape.cells shouldBe cellStatesFromString("0111")
	}
}