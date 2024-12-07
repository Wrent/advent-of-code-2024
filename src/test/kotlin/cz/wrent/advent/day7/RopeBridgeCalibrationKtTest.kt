package cz.wrent.advent.day7

import org.junit.jupiter.api.Assertions.*
import java.math.BigInteger

class RopeBridgeCalibrationKtTest {

    @org.junit.jupiter.api.Test
    fun testPartOne() {
        val input = """190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20""".trimIndent()

        assertEquals(BigInteger.valueOf(3749), findOperators(input))
    }

    @org.junit.jupiter.api.Test
    fun testPartTwo() {
        val input = """190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20""".trimIndent()

        assertEquals(BigInteger.valueOf(11387), findThreeOperators(input))
    }
}
