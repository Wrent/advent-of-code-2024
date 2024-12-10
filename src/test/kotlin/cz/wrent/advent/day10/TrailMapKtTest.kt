package cz.wrent.advent.day10

import org.junit.jupiter.api.Assertions.*

class TrailMapKtTest {

        @org.junit.jupiter.api.Test
        fun testPartOne() {
            val input = """89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732""".trimIndent()

            assertEquals(36, getTrailScore(input))
        }

    @org.junit.jupiter.api.Test
        fun testPartTwo() {
            val input = """89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732""".trimIndent()

            assertEquals(81, getTrailScoreDistinct(input))
        }

        @org.junit.jupiter.api.Test
        fun testPartOneA() {
            val input = """...0...
...1...
...2...
6543456
7.....7
8.....8
9.....9""".trimIndent()

            assertEquals(2, getTrailScore(input))
        }
}
