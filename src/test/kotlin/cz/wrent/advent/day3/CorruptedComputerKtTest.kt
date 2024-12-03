package cz.wrent.advent.day3

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class CorruptedComputerKtTest {

    @Test
    fun testPartOne() {
        val input = """xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
""".trimIndent()
        assertThat(countMultiplications(input)).isEqualTo(161)
    }

    @Test
    fun testPartTwo() {
        val input = """xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
""".trimIndent()
        assertThat(countEnabledMultiplications(input)).isEqualTo(48)
    }
}
