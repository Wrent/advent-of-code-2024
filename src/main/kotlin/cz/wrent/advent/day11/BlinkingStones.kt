package cz.wrent.advent.day11

fun main() {
    println(countStones(input, 75))
}

fun countStones(input: String, blinks: Int): Long {
    return input.split(" ").sumOf { willProduceStones(it, blinks) }
}

private val cache: MutableMap<Pair<String, Int>, Long> = mutableMapOf()

private fun willProduceStones(input: String, remaining: Int): Long {
    if (remaining == 0) return 1

    val key = Pair(input, remaining)
    if (cache.containsKey(key)) return cache[key]!!

    val result = if (input == "0") {
        willProduceStones("1", remaining - 1)
    } else if (input.length % 2 == 0) {
        val firstHalf = input.substring(0, input.length / 2).toLong()
        val secondHalf = input.substring(input.length / 2).toLong()
        willProduceStones(firstHalf.toString(10), remaining - 1) + willProduceStones(secondHalf.toString(), remaining - 1)
    } else {
        val newValue = input.toLong() * 2024
        willProduceStones(newValue.toString(10), remaining - 1)
    }

    cache[key] = result
    return result
}

// fun countStones(input: String, blinks: Int): Long {
//     val stones = input.split(" ").toMutableList()
//
//     for (i in 1..blinks) {
//         val newStones = mutableListOf<String>()
//         for (stone in stones) {
//             if (stone == "0") {
//                 newStones.add("1")
//             } else if (stone.length % 2 == 0) {
//                 val firstHalf = stone.substring(0, stone.length / 2).toLong()
//                 newStones.add(firstHalf.toString(10))
//                 val secondHalf = stone.substring(stone.length / 2).toLong()
//                 newStones.add(secondHalf.toString(10))
//             } else {
//                 val newValue = stone.toLong() * 2024
//                 newStones.add(newValue.toString(10))
//             }
//         }
//         stones.clear()
//         stones.addAll(newStones)
//     }
//     return stones.size.toLong()
// }

// Initial arrangement:
// 125 17
//
// After 1 blink:
// 253000 1 7
//
// After 2 blinks:
// 253 0 2024 14168

private val input =
    """5 62914 65 972 0 805922 6521 1639064""".trimIndent()
