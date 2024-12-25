package cz.wrent.advent.day17

import kotlin.math.pow

fun main() {
    // println(51064159*(8.0.pow(7).toLong()))
    // println(26643244480*8*8*8*8*8)
    // println(getOutput(input).out.joinToString(separator = ",") { it.toString() })
    println(solvePart2())
// println(findReplica(input))
}

fun getOutput(input: String, overrideA: Long? = null, find: List<Long>? = null): Result {
    val registers = input.lines().take(3).map { it.split(" ")[2].toLong() }.toMutableList()
    var (a, b, c) = registers

    val program = input.lines().last().replace("Program: ", "").split(",").map { it.toLong() }

    if (overrideA != null) {
        a = overrideA
    }

    var pointer = 0
    val out = mutableListOf<Long>()

    while (pointer in 0 until program.size) {
        val operator = program[pointer]
        val literal = program[pointer + 1]

        val combo = when (literal) {
            0L, 1L, 2L, 3L -> literal
            4L -> a
            5L -> b
            6L -> c
            else -> {
                println("Invalid operand $literal")
                0
            }
        }
        // println("pointer $pointer operator $operator literal $literal combo $combo a $a b $b c $c")

        when (operator) {
            0L -> {
                var res = a
                repeat(combo.toInt()) {
                    res /= 2
                }
                // println("a = a / 2 ^ combo = $a / 2 ^ $combo = $res")
                a = res
            }

            1L -> {
                val res = b xor literal
                // println("b = b xor literal = $b xor $literal = $res")
                b = res
            }

            2L -> {
                b = combo.mod(8L)
                // println("b = combo mod 8 = $combo mod 8 = $b")
            }

            3L -> {
                if (a != 0L) {
                    pointer = literal.toInt()
                    // println("pointer = literal = $pointer")
                    continue
                }
                // println("a = 0, skipping")
            }

            4L -> {
                val res = b xor c
                // println("b = b xor c = $b xor $c = $res")
                b = res
            }

            5L -> {
                val res = combo.mod(8L)
                out += res
                // println("out += combo mod 8 = $combo mod 8 = $res")
                // val f = find ?: program
                // if (overrideA != null && f.take(out.size) != out) {
                    // println(out)
                    // return Result(a, b, c, out)
                // }
            }

            6L -> {
                var res = a
                repeat(combo.toInt()) {
                    res /= 2
                }
                // println("b = a / 2 ^ combo = $a / 2 ^ $combo = $res")
                b = res
            }

            7L -> {
                var res = a
                repeat(combo.toInt()) {
                    res /= 2
                }
                // println("c = a / 2 ^ combo = $a / 2 ^ $combo = $res")
                c = res
            }
        }
        pointer += 2
    }

    // println(registers.first().toString(8))
    // println(registers.first().toString(2))
    // println(out.joinToString(separator = "") { it.toString() })
    return Result(a, b, c, out)
}

fun findReplica(input: String): Long {
    val program = input.lines().last().replace("Program: ", "").split(",").map { it.toLong() }
    // var i = 107089303175168L
    var i = 950101607L
    val find = program.takeLast(10)

    while (getOutput(input, i, find).out != find) {
        i++
        if (i % 1000000 == 0L) {
            println(i)
        }
    }
    return i.toLong()
}

// borrowed from https://github.com/ClouddJR/advent-of-code-2024/blob/main/src/main/kotlin/com/clouddjr/advent2024/Day17.kt
fun solvePart2(): Long {
    val program = input.lines().last().replace("Program: ", "").split(",").map { it.toLong() }
    fun findA(currentA: Long = 0): Long? =
        (currentA..currentA + 8).firstNotNullOfOrNull { a ->
            val out = getOutput(input, overrideA = a).out
            println(out)
            if (program.takeLast(out.size) == out) {
                println("found intermittent a $a")
                if (program == out) a else findA(maxOf(a shl 3, 8))
            } else {
                null
            }
        }

    return findA() ?: error("No solution")
}

data class Result(
    val a: Long,
    val b: Long,
    val c: Long,
    val out: List<Long>
)
// 12691
// 101528
//  3330405560 - 3230405560 -> 3410000000 nic

// 416300695
// 3330405560
// 61603465530
// 11000110100000011111010010111000

//873045835120640
private val input =
    """Register A: 435175063
Register B: 0
Register C: 0

Program: 2,4,1,5,7,5,1,6,0,3,4,6,5,5,3,0""".trimIndent()

// 3064037227 (416300695) -> 1,6,0,3,4,6,5,5,3,0


// 3154037227 (430980759) -> 1,6,0,3,4,0,1,3,3,0
// 3164037227 (433077911) -> 1,6,0,3,4,6,5,4,3,0
// 3174037227 (435175063) -> 1,6,0,3,4,4,1,7,3,0

// 3264037227 (449855127) -> 1,6,0,3,4,6,5,7,1,0
// 3364037227 (466632343) -> 1,6,0,3,4,6,5,6,0,0
// 3464037227 (483409559) -> 1,6,0,3,4,6,5,1,1,0


// 3564037227 (1087385335) -> 1,6,2,1,3,6,5,5,1,3,2


// 4064037227 (550518423) -> 1,6,0,3,4,6,5,5,2,5
// 5064037227 (684736151) -> 1,6,0,3,4,6,5,5,2,3
// 6064037227 (818953879) -> 1,6,0,3,4,6,5,5,2,5
// 7064037227 (953171607) -> 1,6,0,3,4,6,5,5,2,5
// 7164037227 (969948823) -> 1,6,0,3,4,6,5,4,1,5

// 2064037227 (282082967) -> 1,6,0,3,4,6,5,5,3,1
