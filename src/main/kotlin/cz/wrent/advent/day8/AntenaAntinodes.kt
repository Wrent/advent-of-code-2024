package cz.wrent.advent.day8

fun main() {
    println(findAntinodes(input))
    println(findAntinodesWithHarmonicResonant(input))
}

fun findAntinodes(input: String): Int {
    val map = input.split("\n").map { it.toCharArray().toList() }
        .mapIndexed { y, row -> row.mapIndexed { x, c -> Pair(x, y) to c } }.flatten().toMap()

    val groups =
        map.entries.filter { it.value != '.' }.groupBy { it.value }.map { it.key to it.value.map { it.key } }.toMap()

    return groups.map { (_, coords) ->
        val coordPairs = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        for (coord in coords) {
            for (other in coords) {
                if (coord == other) continue
                coordPairs.add(coord to other)
            }
        }

        val antinodes = mutableSetOf<Pair<Int, Int>>()

        coordPairs.map {
            val vector = Pair(it.second.first - it.first.first, it.second.second - it.first.second)
            val prolong = it.first.first - vector.first to it.first.second - vector.second

            if (map.contains(prolong)) antinodes.add(prolong)
        }
        antinodes
    }.flatten().toSet().size
}

fun findAntinodesWithHarmonicResonant(input: String): Int {
    val map = input.split("\n").map { it.toCharArray().toList() }
        .mapIndexed { y, row -> row.mapIndexed { x, c -> Pair(x, y) to c } }.flatten().toMap()

    val groups =
        map.entries.filter { it.value != '.' }.groupBy { it.value }.map { it.key to it.value.map { it.key } }.toMap()

    return groups.map { (_, coords) ->
        val coordPairs = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        for (coord in coords) {
            for (other in coords) {
                if (coord == other) continue
                coordPairs.add(coord to other)
            }
        }

        val antinodes = mutableSetOf<Pair<Int, Int>>()

        coordPairs.map {
            val vector = Pair(it.second.first - it.first.first, it.second.second - it.first.second)

            var i = 0
            var next = it.first.first + vector.first * i to it.first.second + vector.second * i
            while (map.contains(next)) {
                antinodes.add(next)
                next = it.first.first + vector.first * i to it.first.second + vector.second * i
                i++
            }
        }
        antinodes
    }.flatten().toSet().size
}
// ..........
// ...#......
// #.........
// ....a.....
// ........a.
// .....a....
// ..#.......
// ......A...
// ..........
// ..........

private val input =
    """.........................p........................
......................h....C............M.........
..............................p....U..............
..5..................p............................
..6z...........................................C..
...............c...........zV.....................
...5.....c........................................
.Z.............h........S...z....9................
.O............................9...z........M..C...
..O....5..............................F..M..C.....
..Z.........5.c...............M....V..............
........ZO................q.......................
s...O................h..Uq.....7V...........4.....
.q.g..............c.............p.......4.........
............hZ.............................4G.....
6s...........................U.Q.....3............
.......6.................9.......Q.............3..
....s..D.........................6................
.............................................FL...
..................................................
..g...D.........q.....f.......Q...F....L......7...
...............2.........f.............V.L...4....
...................2.s...................f3......G
....g...........................v......7P.........
..2..g.............d.....v...........P.......1....
..............u.........f.............L........G..
.........l.D....u...............d........o..P.....
..................8...............9..1......o...7.
............l.....................................
...................l...S...........F.......o..U...
.......................u...S......................
..........l....u...............m...........P....G.
......................................1.8.......o.
..................................................
..................v.......S................0......
.............v........d.....1.....................
..................................................
..........D....................................0..
...................m.............H..........0.....
...................................d......0.......
..................................................
....Q.............................................
................................H.................
........................H....................8....
..................................................
..................................................
.........................................8........
.......................H3.........................
............................m.....................
................................m.................""".trimIndent()
