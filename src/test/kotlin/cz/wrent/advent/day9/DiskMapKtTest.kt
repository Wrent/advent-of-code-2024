package cz.wrent.advent.day9

import org.junit.jupiter.api.Assertions.*

class DiskMapKtTest {

        @org.junit.jupiter.api.Test
        fun testPartOne() {
            // val input = """12345"""
            val input = """2333133121414131402"""

            assertEquals(1928, getChecksum(input))
        }

        @org.junit.jupiter.api.Test
        fun testPartTwo() {
            // val input = """12345"""
            val input = """2333133121414131402"""

            assertEquals(2858, getChecksumSmart(input))
        }
}
