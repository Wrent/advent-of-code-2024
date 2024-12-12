package cz.wrent.advent.day12

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GardenPlotsKtTest {

    @org.junit.jupiter.api.Test
    fun testPartOne() {
        val input = """RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE""".trimIndent()

        assertEquals(1930, countFences(input))
    }

    @org.junit.jupiter.api.Test
    fun testPartTwo() {
        val input = """RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE""".trimIndent()

        assertEquals(1206, countFencesWithSides(input))
    }

    @Test
    fun tesss() {
        val input = """EEEEE
EXXXX
EEEEE
EXXXX
EEEEE""".trimIndent()

        assertEquals(236, countFencesWithSides(input))
    }
}
