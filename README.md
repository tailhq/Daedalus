# Daedalus

>The master craftsman.

## Installation Instructions.

1. Clone this repository
2. In the top most directory run ```sbt console```

## Module: Turing

[Turing Machine](https://en.wikipedia.org/wiki/Turing_machine) 
is an abstract computer introduced by [Alan Turing](https://en.wikipedia.org/wiki/Alan_Turing).
It is used in Computer Science as a simple formalization of computability.

Turing Machine is an (infinite) sequence, or <i>tape</i>, of binary cells.
At any moment, each cell can contain either `0` or `1`.

The tape has also a <i>caret</i> pointing at a cell at any moment.
The cell the caret points at is called "the current cell" in this document.

A <i>program</i> which can be executed on the Turing Machine is a set of <i>states</i>.

A <i>non-terminal state</i> has a unique identifier, or name, and 2 commands,
one of which is applied if the <i>current cell</i> contains `0`, another - for `1`.

Components of a command:

1. A value to set the current cell to (`0` or `1`). If omitted, the cell will not be changed.
1. A direction to move the caret (left or right). If omitted, the caret will not be moved.
1. An ID of the next state. If omitted, the machine will be left in the current state.

These instructions are applied in the given order: first, the current cell is changed (if needed);
next, the caret is moved (if needed); last, the machine is transited to the new state (if needed).

An execution of the program should end in a special <i>terminal state</i>, which has no instructions.

### Arithmetical computations

In the typical case, the arithmetical program starts on a tape like `>0110111`,
where the sequences of `1` are natural numbers and arguments of the function represented by the program.

This library supports 2 <i>encodings of natural numbers</i>:

1. <i>"n to n+1" encoding</i>: a number `n` is represented by the 1-sequence of size `n+1`.
For example, when calculating the sum of `0` and `1`, the tape is transited from `>01011` to `>011`.
2. <i>"n to n" encoding</i>: a number `n` is represented by the 1-sequence of size `n`.
For instance, when calculating the sum of `1` and `1`,
the tape is transited from `>01010` to `>011`



## Creating your own programs

```scala
import io.github.mandar2812.turing.Direction._
import io.github.mandar2812.turing.CellState._
import io.github.mandar2812.turing.build.Dsl._
import io.github.mandar2812.turing.build.ProgramBuilder
val program = ProgramBuilder(
              		"q1" -> R~"q2",
              		"q2" -> (`1` -> R.c, `0` -> `1`~L~"q3"),
              		"q3" -> (`1` -> L.c, `0` -> "q0".c)
              	).toProgram
val tape = "010".finiteTape
tape(program)
println(tape) // will be ">011"
```

## Architecture and usage explanations

### Creating programs

#### Cell states

A state of the cell is represented by a value of enumeration [CellState](http://skozlov.github.io/turing/scaladoc/base/0.1/#io.github.mandar2812.turing.CellState$): `` `0` `` or `` `1` ``.
Also, these values has aliases - `Zero` and `One`.

#### Direction

Direction to move the caret is represented by a value of enumeration [Direction](http://skozlov.github.io/turing/scaladoc/base/0.1/#io.github.mandar2812.turing.Direction$): `L` or `R`.
The value `L` has aliases `Left` and `<=`; the aliases of `R` are `Right` and `->`.

#### Building commands

```scala
import io.github.mandar2812.turing.Direction._
import io.github.mandar2812.turing.CellState._
import io.github.mandar2812.turing.build.Dsl._
val command1 = "q2".c // just to transit to state `q2`
val command2 = L~"q2" // to move the caret left and transit to state `q2`
val command3 = `0`~"q2" // to assign the current cell with `0` and transit to state `q2`
val command4 = `0`~L~"q2" // to assign the current cell with `0`, move the caret left and transit to state `q2`
val command5 = `0` L "q2" // the same as `command4`
```

#### Building states

```scala
import io.github.mandar2812.turing.Direction._
import io.github.mandar2812.turing.CellState._
import io.github.mandar2812.turing.build.Dsl._
val state1 = "q1" -> R~"q2" // to move the caret right and transit to state `q2`
val state2 = "q2" -> (`0` -> L~"q3", `1` -> R~"q4") // if the current cell contains `0`,
                                                    // move the caret left and transit to state `q3`,
                                                    // otherwise move the caret right and transit to state `q4`
val state3 = "q3" -> (`0` -> "q4".c, `1` -> `0`~L) // if the current cell contains `0`, transit to state "q4",
                                                   // otherwise assign the current cell with `0`,
                                                   // move the caret left and stay in state `q3`
```

### Tapes

The tapes provided by this library are left-bounded,
i.e. the cell being first when creating the tape remains the most left cell during the execution of the program.
If the caret points at the first cell and the program instructs it to move left,
[Tape.OutOfBoundsException.Left](http://skozlov.github.io/turing/scaladoc/base/0.1/#io.github.mandar2812.turing.Tape$$OutOfBoundsException$$Left) will be thrown.

The base tape class - [Tape](http://skozlov.github.io/turing/scaladoc/base/0.1/#io.github.mandar2812.turing.Tape) - can be extended right infinitely:
if the caret points at the most right stored cell and the program instructs it to move right,
a new cell initialized with `0` will be stored.

Another implementation - [Tape.Finite](http://skozlov.github.io/turing/scaladoc/base/0.1/#io.github.mandar2812.turing.Tape$$Finite) - is also right-bounded.
The size of the tape is determined when creating it and cannot be increased.
If the caret points at the cell with index `(size - 1)` and the program instructs the caret to move right,
[Tape.OutOfBoundsException.Right](http://skozlov.github.io/turing/scaladoc/base/0.1/#io.github.mandar2812.turing.Tape$$OutOfBoundsException$$Right) will be thrown.

To prevent the errors leading to the infinite moving the caret right,
it is recommended to use [Tape.Finite](http://skozlov.github.io/turing/scaladoc/base/0.1/#io.github.mandar2812.turing.Tape$$Finite), if you can predict the right boundary.

### Using tapes

#### Base tape

```scala
import io.github.mandar2812.turing.Direction._
import io.github.mandar2812.turing.CellState._
import io.github.mandar2812.turing.build.Dsl._
import io.github.mandar2812.turing.build.ProgramBuilder
val tape = "010".tape
println(tape) // `>01` (the right cell is ignored, since it contains `0` by contract; the first character is `>`,
              // which means that the caret index is `0`)
val program1 = ProgramBuilder(
	"q1" -> (`0` -> R.c, `1` -> R~"q2"),
	"q2" -> R~"q0"
).toProgram
tape(program1)
println(tape) // `010>0` // (the right 2 cell are not ignored, since the caret points at the 2nd one)
val program2 = ProgramBuilder(
	"q1" -> `1`~L~"q2",
	"q2" -> (`0` -> L.c, `1` -> L~"q0")
).toProgram
tape(program2)
println(tape) // `>0101`
val program3 = ProgramBuilder(
	"q1" -> L~"q0"
).toProgram
tape(program3) // Tape.OutOfBoundsException.Left is thrown
```

#### Finite tape

```scala
import io.github.mandar2812.turing.Direction._
import io.github.mandar2812.turing.CellState._
import io.github.mandar2812.turing.build.Dsl._
import io.github.mandar2812.turing.build.ProgramBuilder
val tape = "010".finiteTape
println(tape) // `>01`
val program1 = ProgramBuilder(
	"q1" -> (`0` -> R.c, `1` -> R~"q0")
).toProgram
tape(program1)
println(tape) // `01>0`
val program2 = ProgramBuilder(
	"q1" -> R~"q0"
).toProgram
tape(program2) // Tape.OutOfBoundsException.Right is thrown
```

## Modules and artifacts

Module name | Artifact ID | Description | Dependencies
------------|-------------|-------------|----------------
`base` | `turing-base` | Turing machine emulator | `org.scala-lang:scala-library:2.11.7`
`build` | `turing-build` | DSL for creating programs | `turing-base`
`math` | `turing-math` | Arithmetical programs working right for [both supported encodings of numbers](https://github.com/skozlov/turing/blob/master/README.md#arithmetical-computations) | `turing-build`
`math00` | `turing-math00` | Arithmetical programs which are specific for the ["n to n" encoding](https://github.com/skozlov/turing/blob/master/README.md#arithmetical-computations) | `turing-math`
`math01` | `turing-math01` | Arithmetical programs which are specific for the ["n to n+1" encoding](https://github.com/skozlov/turing/blob/master/README.md#arithmetical-computations) | `turing-math`
`turing` (root) | `turing` | Combines all other modules of the library
