package cz.wrent.advent.day18

import cz.wrent.advent.day12.blockingCoords
import cz.wrent.advent.day12.shortestPath
import org.junit.jupiter.api.Assertions.*

class FallingMemoryKtTest {

    @org.junit.jupiter.api.Test
    fun testPartOne() {
        val input = """5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0""".trimIndent()
        assertEquals(22, shortestPath(input, 12, 6 to 6))
    }

    @org.junit.jupiter.api.Test
    fun testPartTwo() {
        val input = """5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0""".trimIndent()
        assertEquals(6 to 1, blockingCoords(input, 12, 6 to 6))
    }
}
