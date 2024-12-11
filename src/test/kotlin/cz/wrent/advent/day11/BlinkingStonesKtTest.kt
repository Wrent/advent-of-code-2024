package cz.wrent.advent.day11

import org.junit.jupiter.api.Assertions.*

class BlinkingStonesKtTest {

        @org.junit.jupiter.api.Test
        fun testPartOne() {
            val input = """125 17"""

            assertEquals(22, countStones(input, 6))
            assertEquals(55312, countStones(input, 25))
        }
}
