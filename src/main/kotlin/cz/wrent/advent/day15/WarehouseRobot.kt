package cz.wrent.advent.day15

fun main() {
    println(getGps(input))
    println(getGpsExpanded(input))
}

fun getGps(input: String): Long {
    val (mapRaw, movesRaw) = input.split("\n\n")

    val map: MutableMap<Pair<Int, Int>, Char> = mapRaw.lines().map { it.toList() }
        .mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                Pair(Pair(x, y), cell)
            }
        }.flatten().toMap().toMutableMap()

    val moves = movesRaw.replace("\n", "")
        .split("").filterNot { it.isEmpty() }

    var robotPosition = map.filter { it.value == '@' }.keys.first()

    moves.forEachIndexed { index, it ->
        robotPosition = applyMove(map, robotPosition, it, index, '@').second
    }

    return map.filter { it.value == 'O' }.map { it.key.first + it.key.second * 100 }.sum().toLong()
}

fun getGpsExpanded(input: String): Long {
    val (mapRaw, movesRaw) = input.split("\n\n")

    val map: MutableMap<Pair<Int, Int>, Char> = mapRaw.lines().map { it.toList() }
        .mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                when (cell) {
                    'O' -> {
                        listOf(Pair(Pair(2 * x, y), '['), Pair(Pair(2 * x + 1, y), ']'))
                    }

                    '@' -> {
                        listOf(Pair(Pair(2 * x, y), '@'), Pair(Pair(2 * x + 1, y), '.'))
                    }

                    else -> {
                        listOf(Pair(Pair(2 * x, y), cell), Pair(Pair(2 * x + 1, y), cell))
                    }
                }
            }.flatten()
        }.flatten().toMap().toMutableMap()

    val moves = movesRaw.replace("\n", "")
        .split("").filterNot { it.isEmpty() }

    var robotPosition = map.filter { it.value == '@' }.keys.first()

    moves.forEachIndexed { index, it ->
        robotPosition = applyMoveExtended(map, robotPosition, it, index, '@').second
    }

    return map.filter { it.value == '[' }.map { it.key.first + it.key.second * 100 }.sum().toLong()
}

fun applyMoveExtended(
    map: MutableMap<Pair<Int, Int>, Char>,
    robotPosition: Pair<Int, Int>,
    move: String,
    index: Int,
    char: Char,
): Pair<Boolean, Pair<Int, Int>> {
    when (move) {
        "^" -> {
            val newPosition = Pair(robotPosition.first, robotPosition.second - 1)
            return moveExtended(map, newPosition, robotPosition, move, char, index)
        }

        "v" -> {
            val newPosition = Pair(robotPosition.first, robotPosition.second + 1)
            return moveExtended(map, newPosition, robotPosition, move, char, index)
        }

        "<" -> {
            val newPosition = Pair(robotPosition.first - 1, robotPosition.second)
            return moveExtended(map, newPosition, robotPosition, move, char, index)
        }

        ">" -> {
            val newPosition = Pair(robotPosition.first + 1, robotPosition.second)
            return moveExtended(map, newPosition, robotPosition, move, char, index)
        }

        else -> error("Unknown move: $move")
    }
}

private fun moveExtended(
    map: MutableMap<Pair<Int, Int>, Char>,
    newPosition: Pair<Int, Int>,
    position: Pair<Int, Int>,
    move: String,
    char: Char,
    index: Int
): Pair<Boolean, Pair<Int, Int>> {
    val newChar = map[newPosition]
    if (newChar == '#') {
        println("$move ($index): $char staying at $position")
        return false to position
    }
    if ((newChar == '[' || newChar == ']') && (move == "<" || move == ">")) {
        val res = applyMoveExtended(map, newPosition, move, index, map[newPosition]!!)
        if (!res.first) {
            println("$move ($index): $char staying at $position")
            return false to position
        }
    }
    if ((newChar == '[' || newChar == ']') && (move == "^" || move == "v")) {
        val otherPartPosition = if (newChar == '[') {
            Pair(newPosition.first + 1, newPosition.second)
        } else {
            Pair(newPosition.first - 1, newPosition.second)
        }
        if (canMove(map, otherPartPosition, move) && canMove(map, newPosition, move)) {
            applyMoveExtended(map, newPosition, move, index, map[newPosition]!!)
            applyMoveExtended(map, otherPartPosition, move, index, map[otherPartPosition]!!)
        } else {
            println("$move ($index): $char staying at $position")
            return false to position
        }
    }
    println("$move ($index): moving $char from $position to $newPosition")
    map[newPosition] = char
    map[position] = '.'
    return true to newPosition
}

private fun canMove(
    map: MutableMap<Pair<Int, Int>, Char>,
    newPosition: Pair<Int, Int>,
    move: String,
): Boolean {
    val newChar = map[newPosition]
    if (newChar == '#') {
        return false
    }
    if ((newChar == '[' || newChar == ']') && (move == "<" || move == ">")) {
        return if (move == "<") {
            canMove(map, Pair(newPosition.first - 1, newPosition.second), move)
        } else {
            canMove(map, Pair(newPosition.first + 1, newPosition.second), move)
        }
    }
    if (newChar == '[' && (move == "^" || move == "v")) {
        return if (move == "^") {
            canMove(map, Pair(newPosition.first, newPosition.second - 1), move) && canMove(
                map,
                Pair(newPosition.first + 1, newPosition.second - 1),
                move
            )
        } else {
            canMove(map, Pair(newPosition.first, newPosition.second + 1), move) && canMove(
                map,
                Pair(newPosition.first + 1, newPosition.second + 1),
                move
            )
        }
    }
    if (newChar == ']' && (move == "^" || move == "v")) {
        return if (move == "^") {
            canMove(map, Pair(newPosition.first, newPosition.second - 1), move) && canMove(
                map,
                Pair(newPosition.first - 1, newPosition.second - 1),
                move
            )
        } else {
            canMove(map, Pair(newPosition.first, newPosition.second + 1), move) && canMove(
                map,
                Pair(newPosition.first - 1, newPosition.second + 1),
                move
            )
        }
    }
    return true
}

fun applyMove(
    map: MutableMap<Pair<Int, Int>, Char>,
    robotPosition: Pair<Int, Int>,
    move: String,
    index: Int,
    char: Char,
): Pair<Boolean, Pair<Int, Int>> {
    when (move) {
        "^" -> {
            val newPosition = Pair(robotPosition.first, robotPosition.second - 1)
            return move(map, newPosition, robotPosition, move, char, index)
        }

        "v" -> {
            val newPosition = Pair(robotPosition.first, robotPosition.second + 1)
            return move(map, newPosition, robotPosition, move, char, index)
        }

        "<" -> {
            val newPosition = Pair(robotPosition.first - 1, robotPosition.second)
            return move(map, newPosition, robotPosition, move, char, index)
        }

        ">" -> {
            val newPosition = Pair(robotPosition.first + 1, robotPosition.second)
            return move(map, newPosition, robotPosition, move, char, index)
        }

        else -> error("Unknown move: $move")
    }
}

private fun move(
    map: MutableMap<Pair<Int, Int>, Char>,
    newPosition: Pair<Int, Int>,
    position: Pair<Int, Int>,
    move: String,
    char: Char,
    index: Int
): Pair<Boolean, Pair<Int, Int>> {
    val newChar = map[newPosition]
    if (newChar == '#') {
        println("$move ($index): $char staying at $position")
        return false to position
    }
    if (newChar == 'O') {
        val res = applyMove(map, newPosition, move, index, map[newPosition]!!)
        if (!res.first) {
            println("$move ($index): $char staying at $position")
            return false to position
        }
    }
    println("$move ($index): moving $char from $position to $newPosition")
    map[newPosition] = char
    map[position] = '.'
    return true to newPosition
}

private val input =
    """##################################################
#.......#....O....O......O..O#.OOOO....#.OO......#
#..O.O..#O..#O..#O.OO......O....O..O....#O.......#
#...O......O...O...O.......#O#.O.#OO.O........O.O#
#..O..#O..#.O....OO..OO.....#.OO.....#.O#OOOOO.O.#
#........O#.....O..OO......#.O..O#...O.O..O......#
##O..O.O...#O.#...#O...#O..##......#............O#
#.OO#O.O..O...O#..OO...#..###O....OO.#.O.##....#.#
#.O...........O.....O..O.OO.O.O...O..OO....OO....#
#OO..........O...O........O..##O.OOO.O..O..O..#.O#
#.#O.....O.OO....#O..O...O.....O...........OO..#O#
##.....O.......O.O.O....O.O...#O.O..O##....OO#...#
#.OO......O.......O..#...O......#.OOOO.OOO...#O..#
#.........O.O....OO..##.....OO...#....O.OO..OO...#
#O....O..O..#...OOO...OOO#..OO......OO..O....OO.O#
#....O.........O....#.#.O..OO......O.O..#O.O#OO..#
#...O.OOO.O.O..O.........O.OO....OO.O.....OO..OOO#
#..O.........#...OO.O#.O....OOO......O..........O#
#........O.O.O.O..OOOOO.#.O.OO#OO.....#...O.O.#O.#
#OO...O.#O.OOO....O....O.#.O.OOO.O..O......OOOO.O#
#...#OO.O..O...O.OOO.....O.O.OO.#..O#...O..OO....#
#O.OOOO.O........#O.##......#....#...OO..O.O.OO..#
#.OOO.O.O#OO...O.O...O.........O........O..O#OOO.#
#.#..O........#......O......O.....O....O.#..O....#
#...#.....O.O.OOO.OO.O.O@O.O.O.O..#O....#.#.OO..O#
#O.......#O...#O....O.#.....OO.#O.O..O.O......O..#
#......O...OO.OO..O#....OOO.#.#....O.O....OO....O#
#...O.O.#..OO#.O.OO...O.#.#..#O..#...##..O.......#
##O...O..OO.O..O.OOO..O.........O#O...#..#.O..O#O#
#O.O....#OO..OOO.#O..#O..O.#...#.#..O....O.O....##
#.O.OOO.O....O...#O.O...OOO..O.....O.O..O.O..O..O#
#......O...O..OO.O........O..........O....#....OO#
#.O..OO.O.OOO..OO...........O........O.O....OO.O##
#.O.OO#....#.........#..O...OO...OO..OO..........#
#...OO...OO..OO.O...O.....#...O.#.....OO.O.O.O#O.#
#OO......#...OO......O..O.O...O...#.....O......OO#
#O...O.....O...O.O.O....O##OO.O..#..O....O#OO...O#
#.O.O#OO..##.#O...O#O..O.O#..O...OO...OOO.......##
#...O.#.O.#......OO.......O.OO..#.OOOOO...OO..O.##
#............O..O.....#O.##.O.O.#....O#.......O.O#
#.O..##..O....#O.O..#....O...OOO#.#...#..O...O...#
#.O.OOOO..#.........O.O#....O....OO.OO.....##OOO.#
#..O#...OO..O...O#...#..OO.OO...#....O........O.O#
#.O.O..O....O...O.O.OO#...OO..O.OO.O.O..O...O....#
#.O#..OO...#.OO...........OO.....OO#O.#..O.O...#.#
#..................O.......O...OO..O..O.O.O.O##..#
#.##.#..OO.#..........O#...O.O.O...##..O......O.O#
#OO.O........####.......O.......O......O...##....#
#.O........O..O..#...O.OO...#....#.#.....OO...O.##
##################################################

<><v>^vv<^<v<>^>^^<^^>>^>><vv^>^>^<v<>>^^>>v^>^<v><v><v>>^<^<<^^>^>^<<v^<<^^v><<><v>v^>^^<v<<<>v<>^>^^^><v<><v^<><vv^v>^>^>><^<vv<^>^<^<<<<<^<^vv><v>^v^^><<>>v<v>vvv^<>v<>v><<<v^<^<^vv<^v<>v<<<<<>v<^<<<^<^v^<^>^>>vv>>^^<>^^<^v<^v<<vv<vvv^<^^<>v^^><^^^v^>^>^^<<<^<v<^<><v^<^v^<>>v><^^v^vv><vv>^v>>^vv>v><v<>>>^>>^v>v^^<vv^<v>vv<^<>^>>^v^v><^v^>>^>^<<<<<><v<v<vvv><><^^v^^^<>v<>vv>v^>>>^<>>^^^><v<^<>v<^>^<vvv^>^>vvv>>v<>v<>v<><><<^vv>^<^<<>^^<v<^^>v^v<v<>>^^><>^>>>^v^<>^^^<>><v><>v><>v>^<>vvv>>><><v<><>v^vv^>v^^>>v<^<^<v^>>>^^>>v<<<v^vvvv>^^v>^v<^>^vv^v^v<^^>v>^vv<<v^^^v^<v<^v^<v><v>^^v<^^><^^<^vvv<v<<><>v^<>><<<^vv<^^<<>^^<^<^>^<><v><^>v>><><v^vv<<>>^^>vvv^><<^v<<^>^<^<<><^<>^<>vv>><v>^^<v<v>v^v^^<<>^>>^^v>>v>^^>^<><^>v<vvv>^>v<><vv<><^v>>^<^^<<<<<>^vv<<<v>^<><^<v>v^<v<v<v^<v^<>^v<vv^vv>v^^>v^>^^<v^<<v>^^v>v><<<<<^vv><v>>v><^^^v<^^vvv^^>^^^>vv<>v^<>><><<><^>^^^v^<v>>>v<>vv>vvvv^>>v<<<^vv>>>^v^v><<^vvv^<<>v<><^v><>^v>>v>^^<<>v^vvv><^^<>>^<<<<>>^>>v^^<>>>>vv>v<v^>^<^v>v>>^^>v^^v<^vv><^<^^<vv
<<^<><v<^vvv<<vv<vv<><v^v><>><<v^<>^>>^>>^^^<^^v<vvv^v>v<>^>><^v>^^vvv^<>>>v>>vvv><^^v>vvv<vv<><v>>vv^^<>^^<^>v^<v>vv^<><v<^<>^vv>^^><>^>^v^<vvv><>^>><^<^>v>v>><v<<vv^^^>^v^vvv>>vv^^<^^<>>v<<vvv^<^<^>>v>>v<<v^<^v><v^><^>v^<v<vvvvv>>>^^v><<^^>v><<<^^<<<^><^<<^v<>^<>v<<<v>v<v<<<^v^^^<v<^^^<^<vv<>>^<^<>v^>>v^v><^vvv>^>>v><<^<<v>v>v>^v<v^<v^^v>^>>^<v^v<>^v><<>>>>>^>^<v<v>>^^v<><><<v>vv><^<>><v>>>v^v<>^><v^v^><<v<^^<vvv<v>^>vvv<>^>>v>^v<^^^v>v>v<>>^<><^<^<<v^<^>vv^v^>v^^^^^v^v><>v<><<<v^vv^>v<v<><>>^^<^<v<>^><>v^^>^^><>v>v^^>v<>>^>^^<v<^^^<<^^>^^>vv<^<^><>^^^v^^^>>^>>v<<^v^^v^<^^^v<^^>>^>^<v<>>^^>><vvv>v<v<>^<vv<v^v>^<v>v>v<>v<v<<<><<<^>>>^>^v>vv>>v^vv^<vv>><<>>v^>^^>>^v>^^v^v^<^<v><><v>v><^^^>^><v>vv<><<^^^<v<^^vv<><v<v>vv^^><><^v^<^v>v<<v>>v<vvv<<^<<v<vvv>>vv<<^<vv><>>v><vv<<>v<vv^><><^v<^<><<>>v<>v^v^<v<><v>^>v>v^vv>v<<v^>>><<<v<^v<v^^v^^>v^v>vvv>v<^v<^<<^>v>><<<v<>>vv>v><vvvvv>vv>v^<^^^<v<v^v<<v^v<^^>>v<vvv<>v>>^<v>^<v^<>^^>>^>vv^<>v^v^<vv^<>>v^^^v^^^^>^v><v<>^>v<v><>>><><^v><^^v<>v^v^>
vv^^>v^^v<v<vvv<v<<v^>vv<>>>^v^>v>^><>v^><><v^^<^>v><vv>>><v<>v<<<^v<<>^^<vvvv^^>v>v<^>v^^<<vv^v>v^><><><<vvv>v>vv^<<<vv^<v<^>v^<^>vv<>>>vv>^^^^>vv>v^^v<>^^^>v><>>v<>v>>v<^<<>^>><^v<<^<^>><>vv>^><>^^^^^^>^<^<v<<^<<v<v^v>^vvv^<>^v^>><<<^^>^^<v>v^v<^>>><^^<v^<vv><<vvv<<^>v^^>v>v<v^v>v<^v>>v^>^<<v><v>^>^<v<><^>><>>^><^<<^v><^<<>v>><>v<<v<><^v>><^<vv>>v^<><>>^^<<v>>>v^>^v>v>^<v<>><v>^<v<v^^^>^>^vv^<vv<<<>><>><^<v<<<v>>v>^>^>v<><><^v<>><>vvv>^><<^^^<vv<vv><>vv>v^>vv>v><><<^vv>^vvvv<<<<v<v^<>>v>v><v^><v<<v>>^v^^v>^v<<v^^>v>><v<<v<^v<v^^<<<v^^<<v^>^^^^^^<><^>v^><><v^<^^<>>v^<v^v^^^<v^>^^v>v<>^><v>^^><^>^>v>>^<>vv<v<^^v<^^>v^^<>^<^>>>^<^v<vvvv>^<v^>v<<^^^<v<>^v<<<>>^<<^vv>>vvv>v<<>><>^^><vv><<<^v>>><<<vv<v><<v>^v^vv<v>^>>v<<v>v>>><v>><^^v^v^v<<>^>v^>><v^>>v><<vvv^<^vv^v>>^^>v^v<<<>^>v^^v<<vv>v^>v<<^^<<>>v<<v<v^>>^><>^vv^><^>v<<v><<<v<^vvv^v^^v^>><v>vv<>^>v^<<v><><v<^v>v>vv^^v^^<<>>^<v><>v^>>>v<^<vvv<<v<>v>v<v>vvvv>^v>v>^^>v>^><v<^>v><<>><v^vvv<^^v>v^^^^>^<v<>>^>v^<^<>^^>>><v^v>>^><^v^vv>^<v>v<
v<^^vv><>><<^v^<^v^>^^<^>vv<<^vvvvvv^<><<vv>^<^v>>>><^v<<^<>><>><>>>><<^>>v>>^>^^>^><^<<v^v>v^v>^<^v^<>vv^v>><>vv>><>^<<v><v^v>v>>^^^<<^v><v<v^^>>^vv^>^^>^^v^<<<^>^>^><>v<<<v^>^^^>v<>^<v^v<>>><<<v<>^>v<^^<>^^v<^v>>^>><v<v>>^><^v<^<><>v>v>>>^<<><^^^<<<vvv<^>>^><^><v^<v<v><^v^v<<<><v<vv><<^v<v<<v^^<^<^^^>v^^>v><>^v^><^>>>v>><^v^v>v^<<>v^v<<^<<vv^^<v^^v<>^>^>^<>v>^v<>v>>v>vv^vv>^><><v<^<><<<>>><<>^>^<v<>>>^^<>>^>v^><^vv><^<^>>v>vv<^<v<><^v<^>>><^v<^><v><v^v^<<^><^<><^^<^v^^v>vv^^<v<<>^>>^^v^^<vv<<^vv<vv^<>vv>^>^<v^><><<><<><^v>^^v^><<<v<^<vvv>^><<>>><<v><>vv^^>>^^><<v><^<v>vv^^<^<^^^>^<v<>^v^>v^^v^<>v^<v^v<v><^>v^^><<<>^^^<<v^v<<<<>>v<v^^v^vv^vv^v>v>>^>>^vv^v^<<^vv^<v^v^<>^<^<v>v^<<v<<>^^>><v^>v^^>v^>>>v><<>><v^^^<><><v<<v<<^v>v^>><v<>^vv>^><v<^v>^<>v<<><>>v^v^>^>v>><^<^^><v><^>vv>v<vv^><^^v>^^>v^v<>v<v^^^<<><v>^^vv>v<^>vv^^v^v>>^vv^<><^<^<<<v<>><>>^>^^^^><^v<<<>v^<^^vv><^v>v^vvv><>v>^<<^^^v^^<>>v<^^v<^^^>>^vv<v^^<vvv<^<<^>v^<>^^^<^v^^<v>^><^>^^^v^^>^<>>^v^>vv<v>^<vv^^><^v^^^>v<^v<<>v<^<v
<^><vv^v^>>^<>>>v><^vv<v^<^vv<<>v^>v<v^^^^>vv^><<v>^^^v<v^<<v<v><^^v>^<v<<^>^^^>v>v<^^>v^v><<vvv^^^>vv^v>^<vvv<vv><<>>>v>v<vv<><<><^v>vv><<^^^vv<><^<<^>^><<>^<v>^>^v<<>>v^<^vvv<v^>>v<<^>^>v<<<^v<><><^^<>^<^<v><<vv<>>^<v^^><^^v<<><vv><^>^^>v^vv^^<>^vv>v^>v>^v>v>^>^>^>><>v<^v>><^<>^v<>vv<^v<^v^<>^<>^<^^v><v^<vv>v<^>>v>>v^v^<^vvv>><v<vvv><<^^<^v<^<vvvvvv<v^^^>^^vv^^v>>>>>^^>v^>^><^><>>^<>v^>v<<<^v><v<v^^vv^<^^v>^v^<vvvv^^<^<^<<<v^v^^<v<<v><^<v><vv<^^>v<vv<^<^v<^>^^^vv^<>^<v>^v^<><>><v>v^<<><^^^>>vv<v^^<v>>^<<>v<vv>^<<v<^<<vv^^<v<vvv>^vv<v^><vv<><v>>^^>v>^<>>^^<v<v<v^<vv>><<^><v<v^<<vv^^^<v><<^<v<<<v>^<vv^><<<^^v<^>vv<>>^>^v^<v>^^v>>v^>>v>vv><<v<<v>v^vv^>vvv^^v<>^>^v><vv>^><v>v^>^v^^><>v<^<v>^<<^vv>^v<<><vv>v^>vv<>>^>>>><<^v<v>^<><v<<<<>>^v>v><^v^^>^<<<v<v^^v^<>v><^>^>^^^v><<<^>v><^><<^vvv<vv^v>v^><<><^>^<<vv^>v><>v>v<>v<vv<>v<<^v>^>^v>^^v<>v>v<<v^v>>>vv>>v>>><^^^v^>>vvv^^vv><^v^<vv^><<^v<vvv<^^v<<vv<<v<><vvv<><<>v<v>^><<>><v<v<>^<v<>vv<<><<v^>^^^^^><<><^v^v<^>v<^^^><<<^<^v><^v<>^>>v^v^<^^
>vvv^<<>vv>vv<^^vvv>vvvv^<^>v^<vv^^v>^v<<^<<v<<vv>^v^v<<><v<><<>>><><v><<>v<<>>v<<<^>><v<v<>>>>vv>>>>^v<>^><v>>^><v<>^v^v<>^v<^v>^v^^v^^v<><^v<<>^<>><v><^<^<<<><^v^vv<<<<>>>^vv<<><<v^^vv<>vv^>v<^v^>vv<>vv<<v^<>^<<<>^><v<vv><>^v>vv^><<^^<v<<v><>^>>v><>^>vv^^>>^^<^>^^<^vv^^<>><v<<^><<v^^^<v^v<<>>>>^v^<v^>>^><<v^<v^^>>v<<<v>>>^<v^^>v^v<>^>>>>>^^^>v>v>>vv<v^^>>^v>v^^vv<<vvv<<>^^v<<>>>^<><<<>v<>>^v^><<>^>v<>^^^>v>vv<vv>^<^^v<^^>v<^^^^<<<v>><<vv<v>v^>>><v^v>v^^^v<<<v^><>><^<^<^>><>^^v>v>v>>v<>>>v^^><^v<^>><v><v^<^<^>^^^>>>v^<>^>^v^v<vvv<^^^<>><<v<<>><^v>v^v<>>vvv<vv<<><v<>^<<^vv<^vv><>v<^v><^<>>>^<v<vvv<^vv<^vv><^v^><<<v^^<<^v^v>^>v^^^^v<>^^^<<><<vv<<^>vvvv^vv>^<vv<^^<^>^<^^^>v>v^<<>^>^<<v><>^v^^^><<><>v><<<><>v^>v>^<<<>v^>^<<^vv>>>^vvv<>>vv<vvv^<v<>>>v^vvv^v>^>>v<v^>><v^<^<^<^v<vv>v>^><><><^v>>v<^^^v>^vvv^^>>^<^<^^><<>^^<v<<^>^>^>v><>v<<v><<>><^v<>v>^<>^v^>v>^<>^>^>v^<v^><<>><<>v<^v>^<<<<<<v><<>><>><>><^v^^^vv<v^v<vv>v>vv<v^^^v^><v^^v<>v><<>v>>><<v><^<><>^v<v<><<<><>>^<v^<v<v<v^v<v<><v^<>vv
<<<v^>v>>^>v>v<v<>^^><<^^><^^vvv>>>^>^><<^^<v<><v<<>^^^^vv<^<v^><<v>>><<<<<<<vv<<><v<<<>v<v<<^vv^<v>^^^^^^><^vvv<>v^>^^vv<vv><^>><v<>><>^<<vv^^<>^>^><v><v>v<^v<v<^^^^><^<^^><^v^^<vv^<><<^>^v<v^^v^^<>><<v<^^^<>vv<^^vv^><v><<v^v>>vv^><v<^^^v<<v<<<>vv>>><v<vv>^<^<v<v<>^>v><^>v>v<^<>v^^>>^^<>^<^<v>><<^<<^^^v<v<^<^<<v<v>>^<>><^v^v^<>vv>><v^^><v>><^<^>>^^<<v^<<<>v<^<v^v<v><<^v<vv<v<<>^v^v<<><v^><>><^v^vv^>>v^^v^^^<v><<<v>>^v^><^<v<<>>>^v>^^v<><vv<^^<>^^<<v<<v<<v^^^<^v^<<>vv><<<vv^^<^^vv<^v>^<v>^^^vv^<>v<<>^vv<<vv^^<>v<><^^<vv<<v^<<^<^vv>><^>><>><v^<<v><>^<<^vvv^>v^^>v>v^^^^^^^^v^^^v^<^^^v^^vv^>>v>v^^<v><><<^>v>><^>^v^><^^v>v^vv<v^>^>^<<^^^vvvv<<>><>v<>vv<vvv<<v^^v^^vv><<>^<^>^^^^^vv>^><^^^<<><>^^v^>^^>>v^^^<^<><>^<<>>v^<v<^v<>>>^<vv>^v^>^<<>^v<<v><^v^>><>^<><<v<>><v<>^>^vvv^^>v^>>v>>^>vvvvvv>>v>^vvvv>>^^^<<v>^^^<vvv<<v<v^v^^v<><<<v^v<><<vv<<<<vvv^^^<^>v^v^v^^>^v>^<>^v>>><^vv>vv<^^^vv<>^<><><>vv^v>v>v>>>^v^<^<>^<v<v<v><^<<>^^<^<>^v>>^v^>v^>v^>v<v^>^<>v^<^<^<vvv<v<vv^v<<>>^<^v<^^^<<<^>^^>^<<v>
^v<v^<^^>>^v><v<><^<><><>v^<>^><>v<<vvv<><>v<><<^^vv>>^^^><^^<>^>><<^^>v><v><^>^>^>><>v^<><><^^^v<^<v^<>v>vv>v>^<><<><^^<^^v<><^<<v^<<^>^^>>>^>>^^^^<^v><><<<^^<^v>v^<^v^><v>^^^><^>^vvv>^v<>v<v><<<^v>^<v<<>^^^<^>^^^<^<v<<>>>>v^>^v^>v^v<v<<^<^^<v^^>v<><^<><^>v^v><<v<v>^vv<^vv><v<v<^><>^v^<<^>vv<^^v<^vv<>>v>>^<>^>>^>>>>^v^<>><v^>vv^^>^v^>^<<<v>^><<>>>><<>^v<<>>>>>^>^^^>>><^>>v^vv^^v><vv>^v^vv^vvv^>^>v>v^vv>^v^^<vvv>vv>><<<<^<<<><>v^v^^^<>^v^<>><<^^^<>vv<<<v>v<<<^>><>^<><v<v>v<v<vv^vv<v^vv^^<<<<vv<>vv^vv<v<<v<v><><>^^><^>^<><^v^>>^<^>>>vv^><<><<^>><v<<v^v<>^v^^v^<><v>^v^^vvv<<v^v<<^^^^^<vvv<><^<>^v<<>><v^v^>>>v^^<^>v<><vv><<<<^<v<v<<<^>v<<<v^v<<^v>^^><<<v>vv<<>v<>v^v<^<>^>>><^v><>^^v><<>vv^v<^v><vvv<<vv<><^>>>v<v><<^^<v>^vv^^>^v<<^<>v^v<v><<>^vv><<<vv^v<>^<v^v<^v>^<<<>^>^vv^><>v^<>v<>>>^vv<vv^<^v^<v><>>v><vv<v>><^v^^^^>>v^>^>v^^<<^^v>vvvvv<>^vv^<^<>^<>v>>vv>v>v<>^><>v^^^vv^>v^<>^vvv<>>>v<<vv<<v>>v>><<^vvv^>v^<<v<v<<>^<><^v<<^>>>v^<^>v<<<<v>^^^^^v><>v>vv<>>^<v^^^v<>v<<>v^<>>v><^^^v^^^>^^>v>
<>^^<<vvvvv^>vv>>>^<>^v^vv<v^<^v>^^><^>><<^^vv>v<v><v^<^<^>><><<<>v^v>vv<v^v^v>><><v>>^<v^<><v<^^^v<v<<v<<^>^v<^^>^^^vv^><>>^<<<<v<^^<v^<<^^<v>>v^^vvvv^vv>v>>vv<^^>v^vv<>v<v<v<^<v>v^>v^>>^<v><<<<<^^<>>^v^<<v^><v><>vv<v>^^<^<><v<^v<^^vv^<><v>^<<^<>^^^^^>^vv>v>^>>vv>v<>v^^<><^^>^>><>^^v>v><v<^<<<^v<>^>^v^^v>v>>^>^vv><^>^>^<><^^v><v<^>v>vv^v>^v^>^>^v<v>>v>>v^<<v^>v^>v^>>v<^<<<><>^vv<>>v<^^^>v><<<><<<vvv<v<>v<^vv^v<v><^^vvv>^^vv^<>>v>^vv^^<^<>><<>v^v^^^^^<v>v^^<<^^<^v>>>^>^^^^v>^^v><^v^>^v>^^^<>^<<^v^v>v><^^v>^vvv<<>>^v^^>^>^v<v^>>>^v><v<>vv^v>v>v^v<^v>vv>>^>v^<^^^^>^<^>v^^>>^<>^^><v>vv<^>^^<^><<>>^>^v>>v>^<^v<<vvvv^vv<>>v<><^v^v>^v^^v^v<v><<^^v><^>^>^v<vv^^^^<^^v^v<v>^><<<v><<<>^v^>v>vv<^<><^^<>^<>>vv<v<<>^><^^^^v>v<>>v<<>vv^>^vv><<vvv><>v><^v^><vvv>vv<v^<>>^v^^<<<<>>><v<>>^^><^><v<^^<<<<^^^>vv^>^v<<v<vvvv<>^^v>^>v^^><<^<^v><v>^^^>v^<>>^>vv>><^^><^^^>^>><^<>>>>vv><><^<>>v<>v>^v^>^>^><v^^>v>>>^v^vv>v>><>v^^^>><v><>v<>v^^^vv<<vv<vv><><<^<<<>vv<vv>v>^^<v>>v^>>v><<v>^^<><>^>v^<>^vvv>^<^v<<vv<
v><^^>v>v>^<^<^<<v^<<v><^^<<<^^>><<<^>^^vv<^>vvv<>^^^<<>^vv^v>^^v>^v^^v^<>^vvvv^>vvv>^^<<v>v>^>vvv>><<<v>>v^><>>^<<v^<^^v>><<^^^^><v^<<^^>^^v>^vv^>vvv>v^v^vv^>>^<^<>>v<v^v><<^^^><^^vv^v>v>^>v<v>^><>^<>^>^>vvv>>^<^>^vv>^^<>^^>^><^>^<v<>>^vvv<vv^v>>><<<v^>^v^><^v^<v>vv<<<><<<vv^^vv>>v<v<>v<><^^vv<>>>^v^^<^<<>^<<^<vv^<v>>>v^<>^>>vv>>>^^v^v<vv<^v>v<<^v<^<v^v>v<v>^^><>v^v><<<<v<>>>v^vv<^>>^><v>^v>^<vv<<^>^>v^^<^^^^vv>><v^^v^>^>><^v<^v<>v^>v<<^<v<vv<<^<>vv<>^<v<>^^vv<^^^^<^>^^v>v>v>v<vv>v>v>vv^^<^^<vvvv>v^<^>vv>>>^^^>>>><^^<^<v>v^^v>>>>>v>^>>vv<v^>vv<<<><>v>vv>^><<>>v^<vv<v^v>^v^>>^v^<>^v<^v^<v<^v><^^>v<v<>>><>><^>>^v<>>><><<v>^>^<v><^<^<<v^>v>vv>>vvvv<^>^><>v^<>^><<v^^^^^v>^>v<v^v<>>>^^vv>v>><^^<>><<v>vv<<<>><^v><v>^^<vvv^^^^^<^v>^<<v>>^^vv><<<v<>v^<<><v<>><^<>v<<^v><vv>>^^^v>v><^^<v^><^<v>vv<v^^^^^>><>^^<<>^>>v^^>v^<><>^v<^v>>v^vv<>v<<^<v^v>>^>><v<^^vv<^^<^<v<><^vvvv^>v^<<<><<v>^^^><^><v>^<<^^vv^vv>v>v<^>>>>^v<^v>^v<<^>>>^<<^<v^^v<^<v^<v>^^^vv>^<>^<^<^><vv>>>^<>^^<<<<<v^^>>>vv<^^>>>v^<vv<^
>>^>^^>vvv<<^^vv^<^>v>^<^<^<^>v^<v>>v>>v>^^>^<<>>vv>>^>>>>>v^<v>><v<>^<vv<>>><v<^^^v<v^<v^>^>>>v>^v^v<^v<>^^v^^^>>>^>v<^>^<<><v<<<<v>v>^<^^^<<>^<v^>>^<<<^>>^>^^>^v^^><vv^<v<v^v^<^>^<v^vv>^><^^^><^v<<>v<^<^<v>^^>v>^v><>^<>v^v^<^<v><>vv<>vvvv<<<^v>v>v^^<<vv<<<<v>>v^^^v>>v><^^^><v<<^v^v>^<<>>><^^v>>^v>><><><<vvv<^^>^vv<^^v<>^vvv><<^^<v>>>^>vvvv^^vv<^v<v<><<vv><><>>>><><v>v^><^vvv>v<<>^^^v>><vv<v<^<v^^<<>><<^<><vv<>^><>>><^v>^<>>vv>><>^>^<>v>v<v>v^^>^^^>^><>><>v<<v<<<<v<<<^v<<<<<^>^v>^v<<<>v>><<><v<<^<>v^v^^v^^<^>>^^^^v^<<<v<>^^<^>>>>^^vvv<><^v<<^^>v>v>^v><v^<^>^^^^><<>^>^v<^v^><<<>^<^v<<v>^<^^^>^^^^^<^^<v<>vv>^<^<<vv<^^>v^vvv<^<vv<^><^^>^^v<^v<><><>>v><v^<><^vvv^v^vv^^<^<^<<><^<^<>>^<<<^^>v><<v^>^v<>>>>^v<^>^^^^v>>^v<^>>v><^^^<<^<<v^>vv>v<^^vvv^v>><<>^><<^v<v><v<><vvvv^>v>>v<^>^>>^<^v^>^v^v<<^<^>^vvv^>>v>^>v^vv>>><<<vvv<v>^><^>vvv<^v<v<<v<v^v^>>^vv^><v>^v>v>v^v<^<v>^vv>v<v>v>>>^^<vvv>>><<>^<>>^<v>>^^^<^^v^v><>>>v^^^vv>>^<^v^>>^v>>^<>vv^vv>^^><v>v><>^><<>^<>^^^<><^><v<<<<>>>v<<<v^^^v^^v>^<
>^^<<>v^><v>>^v^^<v<^<>^>^v<><>^><<>^^>>^^>><>>vv<^v<^<>^^v^v^v^^<>v<<><^vv^>vvv<vvv<v>v^v^<<vv<v><^^>>v<v>v>^vv>^^^v<v<>><v^>>v<^<v>v^v>vv><>v<>>><v^^<v<<v>><^>>><v>>v^>^v>v^^v<>vv^><^v^>^^^vv<v^>v<v>^^v^>>^v<>>><v<^>>v^<<^>>>^<^>>vvv><>^<v<v<^>^^<^<>>^^v><>>><>vvv>^<<v>><>>^>><^<v><^><>^^^<^v><^^<<<<^<<^v<><vv^><^^^>^<^<<><vv^>v^<^>><>>^><^<>^>v><>>v>>>vv>^vv^>^^><v^^v^<<>v^^^vv>v>v^>>v^<<<^vvv><><><vv<><>^v>v^<>>>>^>><v<^^^vv^^<>v>v<^v<<v^<^vvv<<><<^v>vvv^^v^^><^<^^<<v^^<<^<^<<<>v<v<><>v^>vvv^<^^^v^v^v^<><v>vv>>v>vvvv^>>^v<^>>^v^^^^><^<v<^^<><^^<<<><>vv<<>^^vvv><vv^v<v<>vv<<>v<v<v^v>v>><^v<<^>^>>^v^<^vv><^>v<^v><v>^^v>><^>^>^^vv^>^v^^^v<v<<<<<v^>v><>><^v^vv>>^>><^><>v<<>v<<<v>v^^<<^>v<^><^^^><<^<vv<vv<<>^>vv>v><^><<^<>>v><<<^v^^<<<vv<>^><<>v^v>vv<<v^v>>v<<v^^vvv^<>><^v^<v<^^^^>>>>vvv>v^>v>^v>v><^^^<v^^>>>v<v>^>^^>v>vv<>>v^^v^>vv><<>v^>^^v<<>vv^^^><<^<>v<vvvvv^^^>^vv><<v<^>v><><><<v^>^>^v<vvv>v><v>><<>><<^<^<v>>v<v<^<><<v^>>>^>><^^^>^^<><v>^^<^<<<v<^<v>v>>v<v>>^^v^>v>>^v>^^>><<>v^><>
^<v^>^>v<<^^<<<vvvv<^v<v^^^><vvvv^^<>^>v>vv<v>^<<><><^^^v^<^>^>>^^<^>v>vv><^^>vv<>^v<v>^v<^v>^>><<>^<v><v^>v<>^^vv>>^vv>^v^^v^^^>>>^<<>v><><<^<><^^^vv<v>v^>>^><>^^^<<^^v<^vv<<>>>^v<>v>v^<v<>^v>vvvv><<<^v>^>v<vvv<>>>^<>^<v^>^<<^v>^><^<^<vvv>^^>v<v<v<>^vv>^>>>^v^<>^<vv<>v>v^><v<^^<><v<>><vvvv^<<^<^<^v<^<^>^v<><^<^<>^<v<v^<^><v^>^v^vv>>^<>v^<>^<v>v>>v>><>v<>>^<v>>v<vv<v<v><>^<>^^>^vv^v^<v>>^^^v^>vv^^>^^^<<^vv>^<v>vv<^v>^v<^><>>^v<v<>v<v>^v^<v>v>>v^v^>>^^^>^v<^v>^^v>><^v^<>^<v<<v^^v^^><^^v<><<>^v>^>vv>^<>><v>><<<>v>^v^>v>^><<>v<v^^v><^v>^^<^>^^v<vv><vv><><^<>^><^<>>^^<<>^v^^>^>^v<^^><v><^^^<<^><<v^<^v<<v>vvvv>^^v<v^v<<>v<<>^v>>vv>>v><>^^<v>v>v>>^^>^<vv<^v>^>v<<^>^vv<<<v<vv<^>v>v>^^<v<vv>^><^>^v><v<<^v^v^>^<<^^v<<v>^v^>>><^>>^<^v<^v^>^v<>v<^v^<^^><><<^^vvv<^><^^<v>v><v>>v<^>vv^v><^^^v^<>>vvv^v<^<<>v><><v^>^><<>^<v<<v^<v^^<^<><^vvv>^^>v^vv^>vv<v<<>^><<<<v>>v^<>>v^>^><vv^v^<^><^vv<^>>vvv><v^>v^<><^>^^v<^>^>^^<<^<<>><^v^>>><^v<v^^^^v<><>>^v^^v<<v<^<vv>>>v^>^v<<<>^v<<><v>>v<<<vvv>v>><^>><vvvv^^
>^^v<v^<v^<<v<v>^<<<^^><<>v^<^^<>v>^>vv>v>v^>^v^>v<>v<vvv^v>>v>vv<v>vv^>^><^^vvv<v<<><>^<<>>^^^v>>vv^vvv^<<v^v<v<^<<<<<<v>>>>^^^>>>>>>^v^><<v^<v<>^<v<v>>>^><<<v><^^<>>v^<^>^<>v^<^>^>^v^><^<^vvv>v<^v<>vv^^^>v<v>vvv<^<v<v^<<<>^<v<<^^<>^^>v^<vvv^<v^v>><<v>>^v>v>vv<><>>v<^v^><<v<v<^v<v<<><^<^>^<v<>>vv>v>^>v>^<v^v<^<^<<>vv<v^v^^v<>^^><>^<>vvv>>^^^>v>vvv<<><^v>>>^v^<v<><^^^vv>^^v>v>>v><v<^^^>v<<>^><^v<v<v^vv<><>v><<<<^v^>>^v>vv<>v^<vv^v^<^^>vv><^v^^<v<><v^<><^<<>v<<><<>>>^><<<v>^<^<v>v^^<<>>>><<<<<^>^v^v^>>><><>>>>>vv<<>^<v>v^vv><<^^<><^^<^^><<vv^<^v^^vv>>>>v>vv><>v>vv>v>vvv^<v<<^v<>v<<v><<v><><^^^^^>^^>><^v<^^<vv<<<<^v><<^v^<>>^^v<v^vv<><<vv<v<^<v<<><v^v>v><>><>v<^<v<>>>^><>>^^v>>>v>v<<><^<<vv^^><v<v>>vv><<>><<^>v^^v^>>>^v^<>vv<vv<vv<^^><<vvv^>^><^^v>^><>^<<<v^>^<v<<vv><v<^v<<v<>>><<><><<<vv>>v>>><>^v<<<^<^<^^^<<vvv^^>v<v^v<<^<><<v<^>><v<<v<^^v^v<>><<<>>^^^<v>>^<v<>^^>v^^^^<>vvv^<^<<<^^^>^<>vv^<>^^^^^<v^v>><<^<^^<^^vvv>v><<>>v^<<<>^^^><v>^>>v>^^><v<>^v>v<^^>v<^<>^^^<>^<>>v>^v><>v^<^>^>vv<v^
><<^v<>v^v>v>^^>v^v<<^<<v^^>v<v<^>>^<v<^<>><vv>><<<^^v><v<><^>^<<>^>v^v>>>v<v>>v>^v>>v>v^<^<>^vv^><v<^v<v^<^v^<>v^v^v^>>^><><<v>^^<><^<v^>>^<>^<v<<<>>>><<>>v^<^<^<v>^><^v^v^<<^>>^^<v>^^<>v^v><>><<v<v<>^>><>>vvv<v>^<<v>^^<>^v<<^<<^^vv<^v<^>^<>><^v>>vv>v<<>><<>>^><vv^><>v<v^v^^v^>>^v^v>^^<^^<^^^<<vvv><>^^^<^<<<<vv<><v><^vv<>vv>^^>>^<^^v^>^vvv^^<v>^><>>^<<>>>><><v<^<>^>v>^<>v^v<<v>>^^v<^><^<^v><<>>>><><v^<><>>^<vv>>><vv>v<vvv>v^<^vvvvv^v>><<<v^>^>v<<<>^v^^vv<>^vv>^<v><^><><^>vvvvvv>v<^<>>v<><v^><^<>vv>>^^v>>vv<v^>v^v>^^><^^>><<<>>><v>>^v>v><v<^<>>^<^vv><^v<<v<^<^<vv><vv<>^^^>^>>>v>^>v<<><^^<<>>>^>><<v<v<<<^^>v><^>v^<<v^<>><<^<^<>>v^vvv<^^v><^<^v<>^v>v^><>vv<v<v<^v>^^>vv^^>^<v><^<^<><v<v><>>>v^>><<<^<^>vv<^^^>><<<v>^>v<>v^^>vvvv^<<<<>v<><v^>>^>>v^^^<v><vv>v<>>vv<^<^><v<^^<v>v<<v><^>vv>^v^>v>v<^<<><>>^v<<>v>^v^>^<v><<>v>^^<><><v><>>v<<v^<v<^><>^vv<><>v^<vv^^^>v<<vv>>>>>v^<>>^><>>v<<>v<vv^v><>vv<<vv^v<>>^^<v<^v<v>vv>^>>v>vv^v>v<>><>>v^v>>^>v<v^>>>^<v<^^>>v<<<vv<<>v><<><^v<>>vvvvv^v><<>^v<v>^
v>^>v<^v>v><^>vvvv<<v<>^^<<>vvvvvv<v<>^>>><v><v^<<^>^v<>v^>^<<v<><vv>^v^>>^<<v<v^>^>v<><v>^>^^v<^>v<>^><v^>v<v>v^^>^^<>^<<^<<<v>>v^<^>^>><v>>^>>v^<^vv^^^vv>v>v<v>v^>v>^><<^<><<<<>v<vvvvvv<><v>^<>^>>vv<<^vv>^>^^>^v>vvv^vv>>v<^^>^^>v^<<vv^>>vvv^vv>vv><>v^v<^><<>^^vv<<^vv><>^<>>^^^vv^v^>^>^<<vv>><^>>v^vv>^><v<<vv^v>>v<^<>v<v><v<><^^vv^^<^<v^v>>^<vv^<<v^<v^^^><<^>^<<^^^>>vv>^>><><^v^>vv<>^<vv>>v>^<<>><<v>^>>vv<>^>>>v<vv<v^>>>>^v^v^>v^<^v<<>v<<<v<^<>v><<^v^<<v^<vv<^v<><^<^>v^<v>^vv>>v<v><<<^v<v<v^^>>^>v^v^>><<>>vv>>>>v^v<<v^v>^>v<vv<v<<>v><<^<<>vv>^<^>v<vv^^>^^>^<^v<>^<^<v<v^^v<^>>^<><^<<>^>^v^v><<>vv^v<v><>^^v>>^v><<v^^^>^v<><^<^^^^<>><<^^<<<^<^<^v<>^<^<v>vv>><<vv^>vv<>>><^^><^vv>>^v^<<>>>v^^^<>>^^>>vv^<vv^>v>>v<>v><><v>vv>v<<v^<><<<v^v><v^^v<<vvv>^^<^v<>^><v>>>^^v^<^<>^^v<^<>>>><v^>^^^>^v>><v>>v>^>^v>><>v<v<v<>v<^^><<><>^v<<<><>vv><>v><<<>>>>v><<^<^^>v<v^v<<v<^>><vv>^>v<v^v<<^v^^>v^<v^vv^vv><^<v<^v<^<>v<v^>v>^>^>v><^v^<><<v>>^v^><><<>^>^^>^<v^>v>^^vv>>v<v>^<v<v^v^>^>v^v<vv<v^^vv<<v<<^>v>^
^<<>>>v^^vv<>^<>v>^v>>^vvv<><v^^^<<^^v<^v<^><<<><v>^<v^<^^<^v>^>vv^v^vv^>>vvv<vvv<^><>><><^<v<^^><^^v^v^><vv><<^vv<^v<v<^v<^v<<v>^^<<><>v<>>>^v^v<>>v><<<<>><vvv<^<<>>>^v^<<<^v^>>vv^v<^<<>^^v>>^^^^<^>v<^v^<><v^<<<^v^v^v>^>^>vv><<^vv>><>vv><v<>^v>^>>vv<^^<>v^^<^v^<^v^^>v>>^<v<<>>>v>><v>^^v^>^<>><<>>v>v^>>><>vvv><>>>v>^<<^v<^v<^<>^v<^>>^<^^><^^v<^<^<>vv<<v<^^v^<<<>v<><>vv^v>^>v^<v>^^>v<<v^>>>>v<^<<v<>vv<v<^^>>v<^v^^^>^<><>^v<<^><<v>^<v^v^<^>^>vv<v^^^^v^^^v^<>><^>>v<^v>v<>>>^<^<^<v^>>^<<<v<<v^^<>^<v>>v>^<^^v<v>^>^vv>v^>v>>vv<<><v>^v>^v<<<<>^<<<<>v<vvv>>>>><<vv><>v<v^<><^v<>v^^<v^>^^<<><^><<^v<<^>^<v^<^v<^vvv<^^<v<>v^v<><<<v<>>><v<<v^><<^^>^<>>>v^<>>v>><>v><v^><^<>>v^<>v^<^>v^<^<<>v<<<vv>^>>v^<v^>^>v^^<<^>>^<^^>><v>><v<<v<<>^^^vv>vv<<>^<v^><v<>^v>^^^^>v>^^<<^<^<^<<v><vv^^^v^<>v>>vv^><<>^^^<^>>>vv<>>v>^<>^^^v<^vvvvvv^<^^>><^<^v^vv<<v<<<>^vv><>>^v<v^^><<v><v^>^v^v<^v>>v^v><^>vvv<<^^<v>^^<<v^v>><<<>v<^>>^v<v^<^vv^v>v>v<v<vv^^<^<>>vv<v<v^<>v>^>v><^^^^<>^>>v<>>^<v><^vvvv>vvv^>^v^<<v>^>>^<<^>vv<>
vvvv^v^^^<<^>^^vv<<^>v>^v>^>^>^^^^^^>v>>vv^>>^^>^vv<vv^<v<<<^<^<<<<^<<v^^^v^<<><<>^>^>v^<vv<vv>^>>^^v<v<v>^^v^^^^v^<<>^<>v<>>^><><v>^<vv><v^<vv<v^>>^v^^vv^>^v><<^><^^vvv>^^><>>^v>^^>v><>>^<>><^<^>vvv<>v^<><><^v>><^>^>^^^v>><>^>><>^<vv<v>^<v><>^>^vv<vv^^^<^<v^^v>>^>><v^^<>vv>v^vv<vv<v<<<^v>^<v>v^<<^<>vvv^v<<v<<vv>>vv<v<v<>v^>>><><v><^v<<^><vv<^^>^vv>>v<>vv>v^vv>^<^v<<><<^>^>vv<v^<v<<><>^>^><v^^<<>^^><>v<><^^^><v><vv<^^vv><v<^<v<^vv^<<^>vv>^>>>v^v>v^>^>v^vv>>><v<v<<v^>>v^^^<>^>^>v>>^^^>>>><><^<>>^vvvv<vvv^^vvv^<vv>>>>^v^>vvv<vv>^><^><v><<v<^v><v^<v<<>>><<v<v<<^>^v^<v^^<>^v^<^^>><v^>^vv>^v<<v>>v>v<v^vv><<><<^><^<<>^<>>>^vv^^^>v<^^^v>^v<>v^^><vv<<^^>^^^v>v>v><<^^<>><><<<^><>v<^<<>><v<<vvv<<vvv^>v^vv<vv<>^^^<^<^><<vv>v^>>v<<v<v>>v<<v^<><v^^>^^<>^v<><vv^<^v<<<>^<<^^v>v<^vvvvv>v^<^><v<><<<^<v<<<v<vv<^^>^<v<v^>^>v^^v>>^vv^>v<<<v><>^v><v>v<>v>^v^v^<^>^>^^v>><v^v<v>><<>^^^^^<vv^v^^<^>>>v<<vv^><v^v^^>><>^>v^>v>^>v^>vv><v<^<^>^>^v^>v<>><<<>^v>>vv^v<<^v>>><<><<<>^<vvvv<v<^<<>vv<vv<v<><vv><<^^vv>^><
<^^^<v><^<vv^^v^><><^v<^>v^<v<>^v^v>^^v>>v^^>^v<<<vv<^v^><vvv><^vvv>>><>><^>><>>v<>v<<<^^<>^^^^>>^<^v^^<v<><<<vv>v^^^>>^<><^<>>v>v^>^^>>^v^>^^^^>v<^><>^<>>v<^v^<<>v^v^<>^<<<>v>^>>v^^v>v^v>^>v^^>>>v<^^v^^>>v^>><^^<^<<vv>>v^<vv<<v<^^^v<<v<v<v<>^v><^>v^<^vv<>>^v<v>^>v>^>^>><vv<<><<vv^v>>^^v><^<^^<<^vv><vv><<^v<^^^^>>^><v<^v>>v>>^>^<><^vvv^^<v^^><^><^^v^<v<<^v<><^><><<^v<v<^<v>^>v<>vv>^^^vv^^^^^<>v<<>vv<>^vv<^<^>^^>^^<^v<<<>^<v^<v<>vv^<v>^v><^>^v^<<<^<>v<<^<vv^v<^v<v>>v^^v^^v^v><<v<vvv<^<<^vvv^^v>v<<v^>vv><<^<^<^>^>^<>><>v<><>><^^><<<v<>><v>^<<<<^<<^^^^v>><^^v<v><v^v<^v^v>^>>>>v<>v<v^v<vv>^^<v^^^<v^v^<<^>v^><>>>v<>^v>v^^>><>v>^v^<^^v^v^^^v>^><<^v^vv<>>>^<<<>^^<>^^^vv^<v^>v<<<v>v<>>><v^><<^>v^^^v>^<v<^<>^^>vv^>>><><<^<v^^vv^>v><vv^^v<^^><<v>v^>>v<v^^^<vvv^^<<<^><<^>><>^^v<^^v<^^>^<<>^vv^v^^<><>v>v^^^<^^<v>><>v^^vv>>^vv^^>><>v^vv>vvvv^^vv^><v^^^>vv>^vv<v^^^^^v>^^><v^<^vv<>>>v<>>>^^>><<>v<^vv<<><v><>v><v^<<<<><>^^vv^^<^v<^>>>>vv<<^<>v<^<><^v^<<v<<>^<<<>^>><^v<vv>v>><^^>>^>^<>^^>>v<>^<<^v<><v^
^<>vv<vv^^>^<^<vv><>>^<^^>^>^<^^v^>><>v^>v>v>vv><^>^>^>><<>^><vv>vvvvvv<<v>^^<<<><<^v<<^^^<>v<<<<vv^^v<><>^v^^v^>^^><vv><^vvv^^<^v^><>v>v<>v<<v^>>^v^>^^^^>>^v<vv>>><>^<^vv^^<v^vv^^^>^>^v^>>^^>>v><>><v^<>>vv^>^>v>v^<>v<<^<<^>v<<v<>v>vv^>^>v^<v<>^<>^v<>><vv<>><<>vv^^>^>^^v^^>v>^<<^<><v>>^<>v><>^>>v^><^^v<vv^v<v^>v^^<v^>v><>v^^><<><<><>^^<^<>vvv>v>v<>v>^<<>>^v<^><<<vv^>^<<<>v<^v>><^^>>>^<^<>v^^^>>>>v^>^<><^<<^><^^^<>^^>^^^v><^^<^v<>><<>^v<><^<<^^>><<><<^>^<^^>^>^<<>>vv<<<>v<vv>^<>v^>><>v>^^^>^><<^^^>>^^v^^<<><^^vv<>v^<<v>v>^vv>>vv<v<v<v<>v^^<>><^<<v<><>^v>^>v>^^vv<>^v^^>>^<>^<<^><^^^v>^v><v^^<>v^^<^v<<<><><<vv<>^>vv>>^>^<>^>v<<v<<><>><^vvv>>><v^^<^vvvv^v<^><><<>v><><<>v><^^^v><^<^vv>>><^^<^v><^<^^<v><^^<v^vvv<vv>v<<>^^^v><<><<vv<^<<^v<v<v><>v^>^<<<<v^v>>>vv>v^^^<>>>^<v>v<v>^^>><v<>^vv><><<<>v><^><v<v<<<vv><<vv^<<>^v^v<<^<<>>^>^^vv^>>>v>^><^vv><v<vv>^>><<^>>>^>>^>^>>v^^v>^<v><v^<<<^v<^<<^<>v^<<^<<v<><><v>^^v^^v<<^>v^v^>v>v>v>><>><<v<v<<><^>v^<<<^>^^<<<>^v>><v>v<vv<><^><<^<v>^v<<v<^>^v^^>vv""".trimIndent()
