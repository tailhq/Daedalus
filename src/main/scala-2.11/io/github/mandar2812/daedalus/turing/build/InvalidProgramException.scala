package io.github.mandar2812.daedalus.turing.build

/**
 * An exception being thrown when trying to create an incorrect program, e.g. a program with no terminal state.
 */
class InvalidProgramException(message: String = null, cause: Option[Throwable] = None) extends RuntimeException(message){
	cause foreach initCause
}