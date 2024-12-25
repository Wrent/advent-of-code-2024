package cz.wrent.advent.day17

import org.junit.jupiter.api.Assertions.*

class FallingComputerKtTest {

    @org.junit.jupiter.api.Test
    fun testPartOne() {
        val input = """Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0""".trimIndent()

        assertEquals("4,6,3,5,6,3,5,2,1,0", getOutput(input).out.joinToString(","))
    }

    @org.junit.jupiter.api.Test
    fun testA() {
        val input = """Register A: 0
Register B: 0
Register C: 9

Program: 2,6""".trimIndent()

        assertEquals(1, getOutput(input).b)
    }

    @org.junit.jupiter.api.Test
    fun testB() {
        val input = """Register A: 10
Register B: 0
Register C: 0

Program: 5,0,5,1,5,4""".trimIndent()

        assertEquals("0,1,2", getOutput(input).out.joinToString(","))
    }

    @org.junit.jupiter.api.Test
    fun testC() {
        val input = """Register A: 2024
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0""".trimIndent()

        val res = getOutput(input)
        assertEquals("4,2,5,6,7,7,7,7,3,1,0", res.out.joinToString(","))
        assertEquals(0, res.a)
    }

    @org.junit.jupiter.api.Test
    fun testD() {
        val input = """Register A: 0
Register B: 29
Register C: 0

Program: 1,7""".trimIndent()

        val res = getOutput(input)
        assertEquals(26, res.b)
    }

    @org.junit.jupiter.api.Test
    fun testE() {
        val input = """Register A: 0
Register B: 2024
Register C: 43690

Program: 4,0""".trimIndent()

        val res = getOutput(input)
        assertEquals(44354, res.b)
    }

    @org.junit.jupiter.api.Test
    fun testPartTwo() {
        val input = """Register A: 2024
Register B: 0
Register C: 0

Program: 0,3,5,4,3,0""".trimIndent()

        assertEquals(117440, findReplica(input))
    }
}

