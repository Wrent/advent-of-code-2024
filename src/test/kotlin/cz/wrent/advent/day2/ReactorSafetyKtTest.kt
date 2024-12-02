package cz.wrent.advent.day2

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class ReactorSafetyKtTest {
    @Test
    fun testPartOne() {
        val input = """7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
""".trimIndent()
        assertThat(countSafeReports(input)).isEqualTo(2)
    }

    @Test
    fun testPartTwo() {
        val input = """7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
""".trimIndent()
        assertThat(countSafeReportsWithProblemDampener(input)).isEqualTo(4)
    }
}
