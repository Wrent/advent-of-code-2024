package cz.wrent.advent.day1

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class LocationSearchKtTest {

    @Test
    fun testPartOne() {
        val input = """3   4
4   3
2   5
1   3
3   9
3   3"""
        assertThat(getLocations(input)).isEqualTo(11)
    }

    @Test
    fun testPartTwo() {
        val input = """3   4
4   3
2   5
1   3
3   9
3   3"""
        assertThat(getSimilarityScore(input)).isEqualTo(31)
    }
}

