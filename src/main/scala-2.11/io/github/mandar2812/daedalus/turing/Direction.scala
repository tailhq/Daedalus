package io.github.mandar2812.daedalus.turing

object Direction extends Enumeration {
	type Direction = Value

	val L = Value("L")
	val R = Value("R")

	val Left, <= = L
	val Right, -> = R
}