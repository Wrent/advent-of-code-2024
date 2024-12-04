package cz.wrent.advent.day4

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class FindXmasKtTest {

    @Test
    fun testPartOne() {
        val input = """MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX""".trimIndent()
        assertThat(findXmas(input)).isEqualTo(18)
    }

    @Test
    fun testPartTwo() {
        val input = """.M.S......
..A..MSMS.
.M.S.MAA..
..A.ASMSM.
.M.S.M....
..........
S.S.S.S.S.
.A.A.A.A..
M.M.M.M.M.
..........""".trimIndent()
        assertThat(findHyphenXmas(input)).isEqualTo(9)
    }
}


