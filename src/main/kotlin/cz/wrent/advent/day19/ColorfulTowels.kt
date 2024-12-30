package cz.wrent.advent.day12

fun main() {
    // println(findPossibleTowels(input))
    println(findAllCombinations(input))
}

fun findPossibleTowels(input: String): Int {
    val (towels, patterns) = input.split("\n\n")

    val towelList = towels.split(", ").sorted()

    // val towelSubCount = towelList.groupBy { twl -> towelList.count { it != twl && it.contains(twl) } }
    // val towelsSorted = towelSubCount.entries.sortedBy { it.key }
    val patternList = patterns.split("\n")

    return patternList.count {
        // println(cache)
        val res = isPossibleRec(it, towelList)
        println("$it $res")
        res
    }
}

fun findAllCombinations(input: String): Long {
    val (towels, patterns) = input.split("\n\n")

    val towelList = towels.split(", ").sorted()

    // val towelSubCount = towelList.groupBy { twl -> towelList.count { it != twl && it.contains(twl) } }
    // val towelsSorted = towelSubCount.entries.sortedBy { it.key }
    val patternList = patterns.split("\n")

    return patternList.filter { isPossibleRec(it, towelList) }.sumOf {
        val res = getCombinations3(it, towelList)
        // println(it)
        println("$it $res")
        // println(cache2)
        res
    }
}

private val cache = mutableMapOf<String, Boolean>()
private val cache2 = mutableMapOf<String, Set<Map<String, Int>>>()
private val cache3 = mutableMapOf<String, Long>()

fun clear() {
    cache.clear()
    cache2.clear()
    cache3.clear()
}

fun getCombinations3(patternIn: String, towels: List<String>): Long {
    for (i in 0 until patternIn.length) {
        val prevPattern = patternIn.take(i)
        val nextPattern = patternIn.take(i + 1)

        val prevPatternValue = cache3[prevPattern] ?: 1
        val nextPatternValue = towels.count { nextPattern.endsWith(it) }

        val res = prevPatternValue * nextPatternValue
        cache3[nextPattern] = res
    }
    return cache3[patternIn]!!
}

fun getCombinations2(patternIn: String, towelList: List<String>): Long {
    val pattern = patternIn.mergePipes()
    if (cache3.containsKey(pattern)) {
        val res = cache3[pattern]!!
        return res
    }
    val parts = pattern.split("|").filter { it.isNotEmpty() }
    val res = parts.map { part ->
        if (cache3.containsKey(part)) {
            return@map part to cache3[part]!!
        }
        val res = towelList.filter { it.length <= part.length }.sumOf { towel ->
            val newPattern = part.replaceFirst(towel, "|").mergePipes()
            val res = if (newPattern.replace("|", "").isEmpty()) {
                1
            } else if (newPattern != part) {
                val res = getCombinations2(newPattern, towelList)
                res
            } else {
                0
            }
            // println("pattern $pattern part $part towel $towel newPattern $newPattern res $res")
            // cache2[newPattern] = res
            res
        }
        val y = res
        cache3[part] = y
        // println("$part=$y")
        part to y
    }
    // println("$pattern=$res")
    val x = res.map { it.second }.reduceRight(Long::times)
    cache3[pattern] = x
    return x
}

fun getCombinations(patternIn: String, towelList: List<String>): Set<Map<String, Int>> {
    val pattern = patternIn.mergePipes()
    if (cache2.containsKey(pattern)) {
        val res = cache2[pattern]!!
        return res
    }
    val parts = pattern.split("|").filter { it.isNotEmpty() }
    val res = parts.map { part ->
        if (cache2.containsKey(part)) {
            return@map part to cache2[part]!!
        }
        val res = towelList.filter { it.length <= part.length }.map { towel ->
            val newPattern = part.replaceFirst(towel, "|").mergePipes()
            val res = if (newPattern.replace("|", "").isEmpty()) {
                setOf(mutableMapOf(towel to 1))
            } else if (newPattern != part) {
                val res = getCombinations(newPattern, towelList)
                    .map {
                        val m = it.toMutableMap()
                        m.putIfAbsent(towel, 0)
                        m.computeIfPresent(towel) { _, v -> v + 1 }
                        m
                    }.toSet()
                res
            } else {
                emptySet()
            }
            // println("pattern $pattern part $part towel $towel newPattern $newPattern res $res")
            // cache2[newPattern] = res
            res
        }
        val y = res.flatten().toSet()
        cache2[part] = y
        // println("$part=$y")
        part to y
    }
    // println("$pattern=$res")
    val x = res.map { it.second }.red()
    cache2[pattern] = x
    return x
}

private fun List<Set<Map<String, Int>>>.red(): Set<Map<String, Int>> {
    if (this.size < 2) {
        return this.first()
    }
    val new = mutableSetOf<Map<String, Int>>()
    for (firstSet in this.dropLast(1)) {
        for (otherSet in this.drop(1)) {
            firstSet.forEach { mapA ->
                otherSet.forEach { mapB ->
                    val newMap = mutableMapOf<String, Int>()
                    mapA.forEach { (k, v) -> newMap[k] = v }
                    mapB.forEach { (k, v1) ->
                        newMap.putIfAbsent(k, 0)
                        newMap.computeIfPresent(k) { _, v2 -> v1 + v2 }
                    }
                    new.add(newMap)
                }
            }
            //
            // for (entry in otherSet) {
            //     firstMap.putIfAbsent(entry.key, 0)
            //     firstMap.computeIfPresent(entry.key) { _, v -> v + entry.value }
            // }
        }
    }
    return new

    // return this.foldRight(mutableSetOf<MutableMap<String, Int>>(mutableMapOf())) { map, acc ->
    //     // for (other in this) {
    //     //     for (entry in other) {
    //     //         it.putIfAbsent(p.key, 0)
    //     //         it.computeIfPresent(p.key) { _, v -> v + p.value }
    //     //     }
    //     // }
    //
    //     map.flatMap { part ->
    //         part.forEach { p ->
    //             acc.forEach {
    //                 it.putIfAbsent(p.key, 0)
    //                 it.computeIfPresent(p.key) { _, v -> v + p.value }
    //             }
    //         }
    //         acc
    //     }.toMutableSet()
    // }.toSet()
}

private fun isPossibleRec(patternIn: String, towelList: List<String>): Boolean {
    val pattern = patternIn.mergePipes()
    if (cache.containsKey(pattern)) {
        val res = cache[pattern]!!
        return res
    }
    val parts = pattern.split("|").filter { it.isNotEmpty() }
    val res = parts.all { part ->
        val res = towelList.filter { it.length <= part.length }.any { towel ->
            val newPattern = part.replaceFirst(towel, "|").mergePipes()
            val res = if (newPattern.replace("|", "").isEmpty()) {
                true
            } else if (newPattern != part) {
                val res = isPossibleRec(newPattern, towelList)
                res
            } else {
                false
            }
            // println("pattern $pattern part $part towel $towel newPattern $newPattern res $res")
            cache[newPattern] = res
            res
        }
        cache[part] = res
        res
    }
    cache[pattern] = res
    return res
}

private fun String.mergePipes(): String {
    var prev = this
    while (prev.contains("||")) {
        prev = prev.replace("||", "|")
    }
    prev = prev.dropWhile { it == '|' }
    prev = prev.dropLastWhile { it == '|' }
    return prev
}

// private fun isPossibleRec(pattern: String, towelList: List<String>): Boolean {
//     if (cache.containsKey(pattern)) {
//         val res = cache[pattern]!!
//         if (res) println(pattern)
//         return res
//     }
//     val res = towelList.any { towel ->
//         val newPattern = pattern.replaceFirst(towel, "")
//         val res = if (newPattern.isEmpty()) {
//             true
//         } else if (newPattern != pattern) {
//             val res = isPossibleRec(newPattern, towelList)
//             res
//         } else {
//             false
//         }
//         // println("pattern $pattern towel $towel newPattern $newPattern res $res")
//         res
//     }
//     cache[pattern] = res
//     if (res) println(pattern)
//     return res
// }

private val input =
    """rwwrww, uuwbwr, wgw, wuurr, urru, grrbr, gbg, bwgwguw, rwgruug, wbrb, ubb, brgub, bwg, rwg, uruubg, wwwbw, grr, brggug, wgbr, rurr, guwuuwur, rbrubbw, gbru, ggwu, urwbwg, bgg, buu, gggbgw, buwr, bgrb, ugr, rbwubb, gurrb, bbw, brgwgb, wrwu, gbggwru, ggbuu, wrg, rrw, gbb, gwr, gg, rgw, rwwrb, rbg, wurgww, rgr, bwub, bruw, ruwbr, rgbwww, uwbrw, buuu, ubbgg, buug, rug, bgrw, gbbr, burrrbwr, rrubug, bwguu, uuwrbu, uuur, gubr, www, rub, gbr, uggguw, bbrwu, gwgu, gug, rruuru, wwggg, gwg, gubgbrww, uwr, gb, rubb, uggbr, brrb, gu, uugr, ggg, rbu, r, rbubuwrw, bbruu, wgrb, ggrwuu, wgbub, wb, urwurb, gwwggr, wwbbr, uuu, gggrrb, bgr, bb, rwwgwg, buuwg, guurgg, uwwb, ugb, ugrr, gbw, wr, gw, rbbuwu, br, buurw, ugu, rrb, rbrw, wwubu, wrbuu, uubr, bbbruuw, rubbuwu, rubwb, ub, wbr, ru, brrw, wgwu, wubgbr, burbu, ubu, gub, wrgwrwrw, urg, gr, rrr, uuww, rwwrwuuw, ubgwwg, ggwwg, buuwr, rugbww, rrru, wbuuw, uu, gubgr, gru, buub, uruw, rugbbbrb, uugbww, bbrrru, uwgbgur, wrrr, grb, wrbbw, wgbubb, ur, bgw, bwwg, bubrb, guwgg, ggbw, ggwugrbg, gwrgur, ggwww, brr, rgbugr, uuuu, ggu, guwbw, wwwwrb, urwwu, gbrurgg, buwggwg, gurb, rbgrg, urbrgr, rwr, ugwbr, wrwg, brw, uugb, bwwu, ugub, ububbg, gbuu, rbrww, ubguwr, uuurrg, uwuugw, wrb, guu, wbu, rrgbrur, uurr, ggr, wgu, ruurug, uwrwwg, rugwg, bbbw, rbwwgrg, bub, uubb, rru, bburg, uwrwu, guwu, wwugwrr, wggbbbg, b, uubbg, rgbrg, urrb, gbgb, gwbwg, grww, uru, bgb, gwu, bbwg, burw, wggbrbru, uuwg, wurwu, wgr, brwb, bwug, ruruw, uguwu, bbb, gbgwuub, brb, ugwb, grrgb, wgbwb, wwr, wbg, wgb, rwb, bwubg, gruwb, wgg, grg, wuwb, rbrbuwb, wwgwb, bwgurww, bwwr, bgrwg, rrrgbg, wgwwguwb, rwbuw, guwgbwr, wu, uwbb, bwr, ggur, rrg, gbwuwgg, gbwwr, ubwgwwr, wwbb, ugg, gurw, uwwu, bwu, bug, rrurgg, wbrg, wgwruu, uwwugw, guw, ubrb, gbwwu, wguwbw, ggguwwb, rgu, wrw, ruu, uggbug, ubg, w, wbgw, bwrbbur, rrugbr, uug, rurbb, wur, wub, bwb, rww, bgrggrg, ggwb, brg, gwb, wwbu, rbggg, gwugr, ugwurbrb, uur, ugrwbr, bugwu, ggbgru, rgg, ugw, uwwuwwbr, ug, grgbbgg, rwbgrggb, uwur, wrgb, gur, gww, bgu, ubuub, gwwburr, ubbrwugu, rr, rbb, bwguubb, gbuuwwub, wug, wuug, wurb, rrgb, wrww, gugwgbr, ggwr, urgubwr, urr, wubwbgr, wgww, urgu, wuw, grbuw, ubwb, ubug, wru, uuurw, rgwbu, wbbuwr, wurr, ubr, bww, grwwg, u, bgrbg, uwrr, uwuwu, ggurwg, bbu, bbr, rrbgurr, bgbrggwu, rwru, rububwug, bguwwbw, gubw, rg, wwugu, buru, uwru, rrrb, rw, wbb, grgr, bggbwwwg, uwu, rbrgw, ggw, bu, bwrr, gbwuwbr, grw, bgwrw, wuur, uub, uuw, rwugg, ugrgwb, bwrrrgr, rrgrbrgw, grwruug, urw, rb, bg, bwrwwgg, bw, gwwguub, wugb, wwb, wuu, urwg, brwbb, bgbg, rwrrub, wbrwu, guur, gbu, rbbgb, buuubug, gbwruuw, wbwwrub, bru, bbg, brrbbr, rgggb, rbug, wrrrg, ubgu, grrru, buw, uurrg, bur, gbbwwg, ubw, grubrr, wrbgbgwg, ruwwgww, guwrrbu, wbrw, uwb, wrrwwg, wbw, rbbg, gwrr, rwrrwu, wrbrrw, uww, uwg, rbuur, gugwrb, bwgu, rbw, ggrwr, ggb, gbbb, uwwgru, wrgrww, rubwub, urb, rbr, ruw, wgwgw, gbub, rwu, bguub, uw

rrggbrggwgwwrrwwuwbruurubbbgbbrwuwrrggubbuggugrubr
bgwgwwguuwgrwrwrwgwwwrgbububuwrubguwuwubbbbu
rguwwbgbgwggbwubbbbruwgubggrubgguggwgwwgugbbgwuu
guugwuwbwgugwbggbubrwbugrgrruwuggwbbrwguggbguwwwgwwbguw
buuuwuurugubwbwurgrrbbrbgrubbuguuwguggbrwwugbrgguwbggwb
ggbuuuwbbuubggwbwgwurburwggrwgbbwgbuwrbgbwggbgwur
grwwuubgrruugugurggwgugrwgrugbuwurugugbrbgrubuubr
uwrbubuwrwgbubguguurwwurbrurbwbwwwgbrwwwg
bgrrwrrbbgwgbbrbgbgwgwwuguubbwgbwrguubuuubuggb
rrwbwgubgbgrggwruwrrbbrbwwrwrrwbwurrwwuwbbbwwuwu
rrbuuwuuwrrggwbrrgrwgbbbbgbuwwguuruugrbwrbw
rrwuuurruugwrbburbbrwwbbrrbwwubwugbrgwgwubrbggrwbw
grwwbbbguuuburrrrruwbgbbbbrrrgguwgwubbwgbbwrgwwrwbrruuwbbg
bgwuburbbwwrgwwgbbggurwrbgbwrgbwgubbugubruwbrggrbbrubug
uwrbbrbrbbwwuwwgrggrgguwwwrgurwbwrbwuwguuuwwu
wbwuwggubgbwrbbrwrurrbwrguwgruururgruuurbrr
ugwugwburrruurrrwuubrgrbrrwrwwrgbrguwbguwwbrbwubwwrbug
rbbbguurrgurggwrgugrguwbwwgwwwbrggrbubwwrwburbrugur
bwrrrbuurrgburbuubwgrrruwwbuuuuuwbubwwububwuu
buubugrubugwgbrrbwrugrwwbburwbwwwrrbbbwbuwbwu
wrrbbwrrrgwwurgrwrbugbwuggrbrugbwrrubgwwwgguwruurwbwgb
wubwubrgwrrwbwwwuwrubwgugbbbwwbgurgrrgrgwbwwbrgbwr
rwrugwwurbgrbwuwggbbwggrwbbgbwrrugbbuguwrugwwbgruu
wugwubgbwbwgrgrbubgbggwgwrwwgwuugruggwbgbrggwuggub
rbuubwwggururwgwrbuwbbruwbrrbgubuwgrrbuugwrrgrbrgwgwwgbr
urwrbugbwwgbrggrggrgugwwbbugwwrubrubrrrrgwwugu
uburguwrrrbbbguuuuwwuwbruuwuugrrurbwbgwwbwurbgu
grururubgggbubwwruubrgggbwbrgubwuggwurwugruuugugrgrbggrr
gburubbgruwrbbrruuubwbrrrwbrgguwuuwurwbwurbbgwrrgwrbrrbbu
urbgbrrrgbrrugrrurrbbguwgburwgbbuuggrwrgbgwrbrww
rugrgwgggrgwbgggrwrbrurburgrbrgugbwuwgwgrbrbuggugb
rrgwgwbuggrbbuwwggguwrurwgbgurruwbwrrrrbbgrbwburgu
bgbrgrurrurgrgrbwuwbwgbgwggwguwbwwwurrbgbwru
uurwurwgwrgbwbrguuwgruuruwrrrugbwwubuugrrwwubbwggguuwwbuwg
rggwgbwwbrbbrrggurwwgurgwrubgrgubbwbbwwwbbgrgruwwgwggbrw
rruugbubgrrubbburgbgwbggbruggwuurwgwbwburbrgbgrwurgwbbrrw
wwrgwrwrwggbwbgrrrwuurgrwrubrbgbrrwuuwbwbrrbbggr
uwbrrbwuwwrwbwuwuggbbggbubrgrruguburgbgwrubguwgrwrrubgwrr
rwbuwurwurgrrurbbgwbrgwguwwbrbbwgbbwwugbuuwwubuuwwg
wbbwbuurrrugwgwgwwruguwbrugwubuwuguggburrggurbwwgw
wgubgubwgbgurwrrwrgggwwuwuruubruugbuubbwubrwbgrggbbgw
rrrbgrbgwugbbrurwruuuugrrwguwgwggrgguuwgruubbubgwbwrrw
bbwggbbgbrgrbrbbrurubrwrwuuwrwwgwwbbbgugbgbrurwbrgbb
gurgbbggggwwurwrwrubrwbwgbgwgrwbugbruuwwwuubguubr
brgrgbubwbrrgwwubrrrgurwbruwrruwububwuggbwuugwubbgr
bruwgwugwbwbugbuburrrugubggbwbuwbwruwuwrwrwgguwrr
guwwrwwbwbugbbgggggwuwurwruurrwugwbgbrgrwgbgbwur
gugwbwuubwgbwbwgrwrwrguwwbggurwwuurbggwugubrggguru
uurwrbwuwbbwbgbbbubgwuurbwruuurbubwbwbrgwgwgbbruwr
bwrrbubgrwwrrrurugururugrgwguguwurwruugugbb
rgwggwuguuwbuwuwwbbggbrbbwbbbwuwrwbwgggruw
wbgbwbbgwubwwbrguubbbbrugbugubrugrrubuwbbwwrwugbgb
grgbrgwgugbwuuwbwrguwrwuuurruwrugbuurrrgurrwbubwbrwuwur
grbuuuguggugbgbgrwgrrrrubugwubrwbwuuugguubuwbrbgww
wwrbuwuwruubwgburbbwwggrgubbgwwuwwrgurrbbubbbuurgwuwb
rwwrwwuwurrbgrbrurruguwbubuububurwbwugurbbuwwrrgwrbgrwrg
gbwbugbbuuugugwbbrrrurrwrrgwwgrgbubbgrgbwuggb
uwwwwugubggrugwbgggggbbrruwbggwburuuuuwrrwrbgwgrwurbguggg
ggrrurwbwbbbgrwburrgwrgruubbuwggwwgurburbr
gururggwggwrwrrbwbwubggugrwgrburruuwbwuwbbbuugb
rwwbgbwurgubwuururubugrbrbburbwgubguugrbuugwbuwgrrwrbrugb
gbgrrwrbrggwbwbbbwbbgugrgwgugbrgbubrgwrbbru
bwwbrbugrbgwgrwuugwwbguuuubwwubbgwrbuurrubbbwwwuubrwrgb
gwwurgurwurugwbwrbuuuwgrbrugbbbrbrbwbgrwbgrrbuu
wubgbwuuwwrwrbubrrrwgubgbuuuurbwuuugrugbww
rgwrrgbrgubrgbuggggurgwruwbrgurgbwgggbburbwrbwbuwbrw
ubbgwuwwubggrguuwbrwbrwrrrurugrrbubrgubgbrwwg
buwrrggbwbwrrrubwwbrrubrububrwgurrrgbgwrruugbugwrurrbrgb
wuuggubguwrwrwuuubggugwwbwbrugrwbubrrrruwbwbgwgguwwrwbwwbb
uguugwgwrugrgwurrrrbubrrbuwwrwgwgrwbrubuuubwb
ubwwrgburgrgruugggrwgwgrbwguwuwuuuurrubbuww
bgbgggruwggrruubuwwggubrbwbrbwbbubbuwgwrrwwbwruugr
guwururrwwruwurwggwwbbwgwbubwgburbwuwuurgrwrgrbr
brwurgwrwgggugbgrrbwwbgwburbuwgrbbgwwbubuubrwwuugwub
wwwwuggrwuwbbruwgbgbbbuwgubwrwwwwgbwbgbwugwrwbwurwbgg
rwrgwwrbuwwuugwuggruwgububbbgrwrrgbwwurggwg
gbwwbubgwbwrgwbgurbbbrrubuwubgwbuwggguuwruruwurbr
rbgurrrwrgbrrrbwgbguwbrbrggruwbgbrwwwubguuwgburwurrrw
wggbuwbruruuugrgwruwwuruuuurwggwbwwwwbbwuwwrugrwwbgb
wwrrrubuggubrggggwgrgurggwwbbrruggubruugbwrguu
bgbuubuggburgwrbrbwwrwrguubbrwugubbuwbrgrwwugbgwggbrbrbuwwg
uwwubbwbgggguwbuuubruwwuwwbrrugrwbubggubwugu
wrgbwrwwwbgbwgbgbrrgwuugbgrggbgrwwuuwbuwwbwg
uwurbuburwuubrwbgrgwbgrwrrbuurwrgwbrurwgwrbubgru
wbbwrggwwrububwugrrrbgrwbwwbgwrubrwbbwbwurbw
bwbwubbrugwbbggwugbrrwrrwwwwwgbbgrrwurrgrbrgwbwrbbwuurgwwwg
rbbbrwwbrgwbbbwbwbbwruugbrwgrubugbwgrwgwwrruuwru
grgruburwrruwrwwgrrgrrrrbbuwwguwwbrurubbuurrgbubu
wbwrurwbuwrwuugubwbwggguuururwgggbugggggwuwgwwruububwbwbb
uwwuguwwrgbgubgbrgrrbbwbwwgguwbwgugbgubuwggwbruurg
ubgbubgbuurbrubururwubggwgrggwruubwrubwbgwwrwrwbrb
wuwgwwguwbbrbbrwuwwburruguwuubbruuwrurrwwuurrrwwgbwg
wbrwwwwgwubugwwgurbrrwgggugrwgbbrbuuwugrbgrwbrwruwwrwgu
wwbbbrbwwrwwwbrgrrbrrwbwbwrruugbggrgggrwwggwwuwr
rrgurruwbbubgwbgruwwrrgwuruggbbgwwrubuwwggwg
buwwbwrbruurbrgbguwgwurwwburruwbwgbrurgwwgugbuwwbgg
urwwwubuuwrugwrggruuwrubguwwbgwubwrwbgwbwubwwggbu
wwubbwuwwbwbrrbwwrwbuwuggwgwgbgugbruwbwurwbbgrbu
bgwgwurbrrwuururwbwbuwgrwgrrbrwbubwrbgugbubwbguuguu
wwrwubugrurbwwwwgbggwbbwwurrwrrbwwrbruwgbubwrguugbrwbbrrbr
bgurwwwrgwgwrgurrrrrwububruuwguggrgrrggwbrgrbrgbgrgrrwuguu
ugwggrggwwgrgbuwuwbbgbwuwbgwguwurwrrubbruwbugwgwgugurubb
buguwuwwbbwrubgugwbwbrbgwbgwwbuuuwwwuwwgwuuurgbgrrrwrurgb
uwbgrwwgbugbuuwwubgrwbbrrbgrugbgwwgbggbruurw
wwuwwwbrbwbugbbrurgugbbrgbwuurgwggbwubugwuwuugggggwr
gbrwwggggrbbrwbwbuwgwwguwwurwugwbbbrrrwrbgbbgbwuuuw
gwurbgbubwgbrggbwrurgbwuuugurgguwrrgwbugrgbwgwruwwbgurgg
brbbwbgrwwwwguwuggrrugggwwbwrrubbwgggguggbrrgbrrububuwgbgw
ugggwuuubuuwuuurbggggrgwuwwguubuggwwwwrbrggwrwugggubuw
bwguwbuwwgwwrgggwwbrgrbbbgwgggbwugrrbuuurrgbbwrrbgguw
uwgurbugrbbbbbrurgrguwrwbwbwuwubwbwrububuubwur
buwugrgwruwbgubbggrwrgwwuruurwbuugguwgrrwwgwg
uurbwbuuguuwbuwbwgrugrgrgwrgrbrwgrugbbgbbuggbrbwg
brrrrwbrbgbuwrburrwgrguuguwbrugguwbrrugbugbgwggwurbrbu
wruugrbwuwrrggugugugugrwuububrwrrbrbuugbbgrggwrrrgw
bwwugggguruwuwrbgbuwbgrguurwbbwgrbwgrgubruguwubbwug
gwbbrwgrrgbgbuburggbwugrgwgubbguwgrurwuuubrrbwu
wgubwwuwwwrbgbgwgrwugubgrwbrbruwwgbubbuuwwg
urgrgbuwwburubggubruwwbrrwrbbbgrwrwbuugrrwggubbwu
rurwggwrwrrubggbuuurbubwbwbruwgwrrbgguruwuwwrwwrubgb
rrrugbbbrburubugggrbuwwuubuwwbugrwuwubugrwbbwrguugwg
bggwbrugbbbugwwgbwubrwggrbgbuwrgbrwbrwugrwbrrbrwwb
wbubuuuwggwgbggbwrugbgwbrgbubugbgwuwgbuwuubbwrbwwwrguubu
wbururwgwuwgurruwwubwgrbrgugrrrrgwwbrrwuwbwwrrrurwurg
ubrruwruwguubgruwgwgwrwggwgbguwbuuurbrrguuu
wwggurrwbrrruugurbugrgwgbrrbrbubbrgwwuruwguuwwgww
wbbgrrwwbgrubggggwuuurburuwbguuggwugbwgrrgwwg
gwrguwwrwubrrbuuwwuwwbrrwurrgbrwggruurwwg
urrrwgbbbwrwbuuburgggwrwugrurguurbgurbbubbwgubbwwubug
ubwbrubuubbrrrubbugbubwgbbggurrwbrbgwbrurbbrrwuwu
wwrgrbugbgbburubuwggggrgguuwgrrugubguwggguurrgwururwbwgrbw
gwbgbbwbwgbuuuwwgwubwbwggbbrwbwuwbgbuguugrrwwbu
rguubwbuwwbbbrgbbwwbwgrbrbugrbwrruuguuurrruwg
gwuuwwrwwugbgwwrruugugwrbrbgurruugwwbbuguwrgw
ruwwwbwuburuguugrgbbwbwrruubbrurggwubgbruuwgwgwrw
gbbwbrwugbwrugburwggwguwubwbwubwgbugrbbrruwuububrururbwg
rrwbwuurwbggrguwwbrwwbrwguugrrguwwbgburrug
wggwuuurrbwuwgbbuwbburrwwwbwrguuurburgbbbwbr
bwubgwrrbbbwrggugbuwbgwrguurbbbrgrggwrbbrbbbrbu
bgbwurgurggurgwuwbbbubwugwrwgrwwrgbuwwgrwbgburggrgwrwg
gubrwubwwrugrwgrbbrrrwwruwwuurbrwwgruuuruwb
uwwbrwggwrgrbuurugwwugugbbbggbwgugbruwgrwbbgu
bubrgrwgrwgbwbbbwrwwubrgwbgrwrggwrbbwrwurruwrwuguurbuwbwg
wrrrwbwgwgbrugrbbwwgbwuruwgbuguuwbwbgburrwgurgwwgb
bggrwbbugrwgbrgburbbrwugrgbbbwgguuwbgrwrbwurgwrwugrwwug
wbbbrrbgruwrwrgrrwurbbwgrrbuubguwrbwrrrgubrwwbrubggugrrwwwg
bbwgwbbwuggbgurggubbrgubwgwwuuwurgrbgrgbwggbwwubbwrwrbbgu
ubwwgubgwggrbgbrrurrrrbugwbwgrurugrbwgbbrwbgbbrbwu
uwwwwrwbubgrgruwbwgrwgrgurgggwrbrrwwbgubwubrwwruwwuggrugu
rrrwwgggwwuwugrrrgrbgrbwuburgbrwgrrrbwbbgbwgruwr
rruwgwbrrgbwbbbwbwgwwbuwbbggggrrguwbbburubwrwrbuww
rgbuuugrgurwburbwruugugrwwbrburgrwwguggrbgrwuuwuwguugw
rrrgbgbbbrgubrbbbbgubwrbwbuwugbrrgrrbgrugbrwbggrwg
grrubwrururbguurbrrrrwuuggrwbwwbgrwurbgwbguruggwwwbbruguug
wuwbwrbgruwgubbgbggbbgrgrwbugwwwrwgbrwgrwuuuwb
uruggwwuwrrbbrgggrgbbbuwrwrbbbuurbrrgbgbggggbwbrwrgrrgwbrw
ugruguugwwubwbbuubrurgwwubgbrwuwurururbbrubrr
wbwrbgbgubggubwbbbrwgrruwgbggbrbgwwwrwwbwugbrbwbrurwgrbbwr
buuwuuwgrbbgbuugbrgbbwbgwuwbrrurbubwuggwrwugbburgwbug
urbrwwrrwrwbrgugrburrggwbrgbwruwuuurguwwwgbwgggrwrrbuw
bbuuubwwbgrbbrrgrbbwwgwubuurwwruwuububbguubgw
uwwrrbrbwggrbrggwuuburrbwbbwgwgrurbwwgubgrbwwb
gwwbrrbwwwbwwwuwbbwwuruurububuggbubbggbbuu
rrggwgbwgugggwrwrgbuuuggwuugwwbbruubwbgguuubbubrggwwbrbrwb
rwrrgguwuwrgbuubbgguuuwwuwwrgwurrugrbburgbwggbbwgrurbuwwbw
brgbwugrurrrgrrrwwbbwbwgrrrrwggwrbrbwwgurgu
wwwbwrruuguwrgwguwurubuwrrbbbrwbrrrugbggubbwbrwugggwgwurr
ggrwrbwwgubrgwgrbbwwuuburggguwgwurwgbbubwgwrgrgubuwrw
grbbbuurwbguggbwbbgggurbbbggugwrwrrurggwbrrrgbgwugguw
brrgrbwurgbbgrbrwrbgwwguwgwbbrugbrgwubrugwugu
bgwggggbbwrwggggbgrbwgwgbrubrrugwbwbbuwbwgubgb
gbwrwwgwuuwuwbrbggguuwurgrgugbbwurrrbrbugwgggrrbrggugrwwru
grgrwrugrwrrwuuubrggrgbugwrrbrgruwurbbwgrbrgr
ubgbbbrrgrbgrugwbgwbwugububgwrbgbrurubrgwwrgwrgbgguuwggwr
rrwwruwbgwrruwwgbuuburrgrwrbbruuwggbgbbgubguuuwrrrwwbrbg
uurrgrwrwrgugwbbubugwrrrgguwbwwugwgruuguwggurrb
wwrbwurrbrubuguwgwrggburbrbguburbwrrgbwrwbrbbruwbrugu
bggrrwwggurgggrubbrwububbwgrbuugggrruurwurwwwbgbbbwubrwwg
rbrbgwrwwgrwrbruwggwrrgbugburrrwugwbrgbwgrubbuurwbwbw
rrwrubgwgwwrwwbbwgbbrrbrggugbwwbwbbrwururb
gurrgubgbwggbrbuguwuwurrbwbwwggurrrrgurgurg
uwgbgruubbgrwubguwguuwuruguuwgbrburwuuuurgggrw
gbwbggwrwbbuuwrggbrbgwrggguggrguubrubugwww
bgguuurwuggbbrbrwrguuwgbgrbbbrgrbrgrgubrwuwwg
wgrgbbuwwuugwgbruwguwuubuuguugrggbbrgbburubgbrrugwwbggbu
gwwwrwguwbgugbbwbrwwwwggruururuwwwuuugrrwgbrwurbuwrb
ruguwurgburwgugurbwruurbrgwwuburbbgbgwgbbw
bbwubuwrwwbubgbubururbwuurbbuubwwwbbwgbrgugrwburgurwuugw
grbrrwrbrgrurwgburgwwrgrrwgbbwgrbubwrgwrwrwwg
bbrrubuwrrubrubuwruurrbrrrrgwugrugruggwguubuwr
brbbubwwgrwrbrbbgbrurbrwrwuubrugbggbuwbruwggbgubwubbbwwu
wrbgwbrbggggbuwuwwwrubbuuwwrbururuwgurgbubuubrbrruugw
bgwuwurrgrguuuuwrwrwwbguwwwrwbgwgrbguruuwrrw
bwgwgrbbgrubuuwwrrrurwrbguwbuwbwwrgugbgbgwbwbr
bbubuwrwrugbwwwgwuwrgrugrbbbbubwbrrguwburubgurbgwu
wrwbbrbrurwrruwuwurugwrgurrgburrubgruubbrrrgrurrbg
wgrugrbgwbubrgwgrggggugrbburguruwbruguwrbrbggrrrbrwrwwug
ggggbbwwggrwwugggbbubwwgwbubrrurrbbwrbuuubbu
wwguwwubwbuuuggbrwguwgrrrwgrrrwburrbrbbgubr
wuguugbuwwurbbbwburuguggguwbrwbrgurubwbwbbwwruwwg
urbugrrugrgrgurbwgrbbwgwrwwgwggbubgwwgbrrugrwuuwwrwbgubwg
rbbwubgruruurbgbbuggrbrrwguugwgbbrwurburwbbwr
wbubuwuguubguguwbgrwuubrbrwwrgrbuuubururbgrw
buuwuuuuwbgbgguggwguwgggbwrwwbugwbbrubbrurgguubgwwguwbwur
ggbrguuuggguwwrgubrwrgubrubgbgggurrbrbgbwrwgwgrggw
rrbbuwbbrwuuwwbwuwurguwrwgggguuugwwbguwgruwuwrbuuwwgbwwwur
rgwguguuuwbuubbwruugwuuwwbrwrwwbugbwwburrgw
urubwbwuuggururwwurbwbguruburuwbuwggrwugrruugbubgubrb
grgwrurbwbrbbuggwubbwwurguuuuubggbgwwrwrwubrrbbgbgbr
wuwruwuwwbburwbruguugwwwbrwwrruwrbruwgwgwwbwwburguwggbuw
wbgbrwrrwrbwgbrbbubguwgwgburggwbbrggbgwwuwugwrgwrgrwruwgbr
bgwwgbrguggggwgwgrurwgugburguuwubuwrbwbwrb
brgbrurgubbrubgwburrbrwrbbububgbrggwuubuubwguwrbwgrbwru
wuugurrrrurbgrrurbbgrubrgugggbwbbrguggwgrurbbbbbwbuw
ggggwwubbbubuubbwrwwrgwwuguurbuburruruurrwbrgbbwggb
uburggguuurbrgrrgubbgbugrbrbguubgbwgrgbwrbrwg
rrrbrurbggwwrrwgbrbrbwwuuubwrugwgbgbggwuwuuwuggugb
gbgrurgwrwbwbrwugrwrurwugrrwuuwrrrwrrwwuwgrbwrggbwr
grguwbururwuruwuuggwwggwbwrwwrguubbbwugguwuruwuwgu
bruguwrrrbgrgguugrbwuwrwbrugbgbgrbgwrwwruggb
gwuuurbwgwbrrurbbrwbbwbbgwubrbrwggguuggwguwubruug
ugugrwwbwwubuubgrbrbwuuwrbwgwbugggwguruwbuw
wububbbgruwuwurgwbgruugrbrwrrgwruggurbbwwbbrgbbwbbruu
grbruwuwrrgbrbbubgwgwwguwbwwwbrgwrurruggrbbgurbgbgruu
brrwwbubgrgbwguwruwrrrrruuwuwwgbgbuugggbbbgwbggguwuubg
uggwrbbgbubwburwwgrbuuwgwrrgwgrrgurgwwuubrrwburwggbrrwg
bgwgrbubgwbububbrwuguwurguwbrggugurrgurbguwbu
rrwbgrggbbbwgbuwrrguwrbrruguwgwurbrubgrurbwrwggbwrrrrgwwg
gwwbububrrwbrbrgbuubbrggbggugwwruugwgwuubrgrgrbrbgww
wbwgbbgwubwbrrbgwrwgwugbugurwggbrbrugbrurgruugbwwbgu
grgubwurgbgbbbwrwrgrgrurwwgwrrwwbrugbbrugburgrbu
rubrbwubbgubrrrbrubrugwrbbwruwwrrbwwwrwbburrwrwuuurbrwrggg
brgbugwugbrururwbruurgbuwuwuwbrrgwbuuubbwrbgugbugg
rgwggrwwggwbrwwbwrgbuuwgrrurbwrwggbuwwwgugwwbwwbwggbub
rbbuggbguruwuuwwuuubgurugruubrrguwuuuwwg
brbgugwgbwrwwbbuwrgwbbgruguwgurbgwrrgubbbwgggurgrwbuw
bbubwwrwuwgwggwbgwuugurrgrbrugwrurguggrwburruwrwwg
ugwruugrwrwrugrguburgwwgubgrrruwbrbrugruururrgugug
buururggguwrgubgwuggwuwgurrurwwubgrbbuwruwwbbgbrwgrggggwr
rrrbggwgrggurrgbrrgwrubburuggbggwwwrurwgugrrguurwrbruwwg
wbbrrrrbubwwwuuwrbrwbwubwrwrrrugwrrurwuwbrwrrubw
buwrbgwuruwbrbrbwuwrwbbrggwugrbgbubugwurrwrruwwg
uwwbgugububurrrruuugrrubwugbrrbgwuugugrbugrwggrwgrwub
bwrbbgbbwggbugrwgrgggwrubbwgwrgububwgwwrgw
wrrwgbrbuuuugguuwgrrwruurrrwwwubggwrbgrwgrgg
urrbgrbbbbwggguguwwrugwubbgwgburbgubgrrrbbwgwwrgwg
bbbrbgwbwbbwrbbwgbbwgwrwwrugugwugburgugwwurbbuwbgug
ugwrburwuguruubwrrurwbgwrubgrubrgbguwrrgrguurrwwurbru
wwwgrbgbgbgburububurbgrbuwuwwuwgrbrwgwrbgwuggurggruwugbgwwg
ugbbwurrbubruugrbgwwurrwruugwguugwrburwbbrugwrbrbguwu
bbrwbbwwgwbuugruubwbuggwggrbrggrbgrgwurbugggrurgbwrubr
bbwburrwrwwbwgwggbrbrugrbwuwbubrbugwggbwwrwgurrwwwwurwwwg
wgrwrbgrrwwgwrwbbubrgrrwbuggbwbwbgrrugguuwwrbgrruuuw
grwwuwgguubgwbubrwwrrgbgugwrwurrbubuwuburbrrbggw
bubwrrwbuubbrrgrrruggwbugbuwwwwrbrgwubbubwugwrrwb
bgubgwuwuwbbggggrurugbbgbuurgwrwubrbgbrgrgbbb
guruuuuwwubugggwbrguwrurgbggggrurgbgwrbwggub
buwbuwgbugggurrrggbggwbuwgbwbwrggrgbrggwuugbgwb
rggwbbbwbgrwrburrrbwrwwwubgbrurwbwrggbbrggwugb
bugrwguwgbwbugbwwuwrgwwwugwwgwrubwbuwrrwbrbubuwrwgub
gggbwwgrubuubwubrwwrwuubwrrbbrrbruwuggrrbgguwr
bgwggwrbrguwuwuwbuwurgubwrbgbgwggbwubuguru
rgwwurgwuuubuuwwrrwugbubbrbuguwuwwrggbrgrgbwrrrw
ubgubuwwwuwwrwwbbrbgbbrbrrgggwubwwgbwwgurguwguugbrwgr
gbwwgguurwuuugwurubbbrrgrbgubgbuwuwurwbuggbbuwwuwbgwgbg
rrgrbrrbwuuurbruwrbuwwrwwubbrrggrgwurwubbguugwwrg
uwrbgbbrrgruuubgbuwwburgwwbrwwgbgugwrbguububurrwgrrwg
grwurwbbbwuruurgbburgbwbbrrgbwburubwruuurww
rubuuurruubuuugruuwbrgurbwwwrugwbuguwbubgbbbwbwwrwggurg
rbggwrggrguurbrwrwgbgbbwrgguggwrrggbbubgrwgrurguggrwwubu
uwgbrbwrwbrgbbugggurgruwgwbwwbguwuwrrwburwwubbggrbgbb
wggwwuugbwbwrburugbbwbubrwubgrgubbbwgbbggubrubggu
rgugwrgurbwbguwwrbuwrbbuwbuwwurwwubwburuurwwruu
wuggugugrggrgbwbbbwbggbgrbwubrbbbrbrbrwwrurbbgugrbuwrw
guuwrurwbuwwgrgwgwrwgwbuuugubwguruuggbrgrgrwbrrbbubgruu
gwurgrurwubgbugbuwrgurugbrwbbwwbrgburbrrgugwbuuwrwrubwruwb
bgrruuwwubuugwguurgrgwuuugwrwgugubrbubrgubwugwubru
bgbbrbrwguwbrgrruwuwuwuubuggwgrwbwbggrgbgrgwrw
buuuwbubbwrururrggbrbuuwrrwwwgbrbwrgurbggguuuw
ggbugbwururwgrwbgbwrrrwrwrbugwgrgggrwrgrbrrbgbggrwbggbwgg
gwwugrwrwgwwwwbwbgwbbrbgwwbbrgbbbbuwrwwgbbrwrr
wwuwruwbwuwubrbubugwbuubwwbwwbwwguuuwwrbwwgw
bguguwrugrburwrwrrwrrbggugrbgwwwwbbbbbbgwwwu
gbgggrwugugbbrwuuwuuwrbrgggwgugwgrurguwgrrru
rurwurguubwbrbwgurbbgwbrrwugurbwwuggrgugggbbggbugrrgrggruwwg
guubgrbwggwwuwrurbgbbruruurgbwbruubrgwbwugrwgrwbbbu
wubbgrrgurggwgwbbbgwbbugbbgwggrubbbuggwwbrrwbrwgrbugugbg
bgguwwuugbggrrugrubuugbuuugguurbbrrwwrwwwrrrbwbbgbrug
gwbruguruuwwrguurbgguwwuwuuwwrgguugubwwuggwg
bwrwbrrbuuwrwbrrrruwggrurwrurgururuwgrrwwwrwggw
wrgbwbbuwwggwrbbgwgwgwuurrbrbbrbwruwuuugwbwrbbburwwg
grbwwbbubrgurrgbbbbwrwgugrrubwbwuwwgggubbgub
wurbburggbwuguwugugwubruggwbburgwbrgubbwgbgugwbbrbrbwrggg
grwgbwwrugguwggwrrbruurwbbrggrbuuwuguwuurwbrg
rrgguwwwurgrwbbugbruuubrrrguguwrbwugbgrgwggugrrurrru
wwuguuwubugwrgburrubgwbugubwwurruubbwrgwgrgbggbrwgrgru
bwuruwbrgwwwuggwugrbgbrurguwbuwrgrrgubwbwrwwburggubrbg
bwrrggbbrugwrbbgwbbggubgrrbwburrrbwrburruwwwg
rwrgrwruurubbrbrgrwbbbwurrubguwggggwugwurbrbwgrrugggggbuwwg
uuubuubbbubbuwubbwwuubgbbguugrgurbwggrwgrw
urrrgwrggbbwbruwbgggwuwgrubbgwrbgbgrwbwbrgrug
uruwrgruguuwgrurbbrurbrbbggbbbgubbbgurbggbgwguuwb
uuuwrrbwrbbgurubwrrgrgubbuggurbrbggwugrbbw
ubruuwbrwuurububwuguurwbwuwwrbrwrwwbbwbbruwgbbgwuruwwwg
ubrgbwbrbrgburuwgbgrrwubgbwwwurbrwwurwbgwgrurrbgbrggwuwwwg
rbwwgrrgburrgrguguwgrgugubwuruubwrrwurgwbbbgwguwrbuugrbub
ruwuwuwugwbbwwbwbrwuwuguwbwrgbgwgwwrgurugrrbrrrwwurrwbrbu
rwuugwguurwguggwurgwbubuwggubrrwwrwguwgrwwgrrbuwbr
gbwwuburbuwbwgbruuuguuwwgwrbbbrbubwgrwgwgrbbgggrbbgrbbuu
rbbwuwbbbuuwbbrurgbwrwbgbbwbwrugbwbbgbrubgurggwbrrwgwbu
wbgwbugbgwrurwrgurwwbbgwurbgubwrbuuuggwwwuuurburuubgb
ugguwuwrugrugwbwbrwruuwwbwbgrrwgwrrguggrggr
uwrwgurgwruwwgurubruruwuwbubwrruuwbbrwuuuwr
uuwgwbbuubgbubugwwuwgwbbubruuugugrrbrwgrrwbggrbu
urgugbuurbrwbgbgggubgbrwwguuubuwuuuggwrrbrrwgu
grguwguugwrbgrrburuwugubrgbrwbwgguwruwbgbbgwwggrggburg
rwwbuuubbrbrrrurbrubbgwwgbwwwgwbwubggrgugbrrwu
wgugbbbbwwugwwuwrbugwubrurrwgbggrwgugurbgrrbwbuubwwbbgrrw
gwwuuwubgbwuurwbuuwuwwgwwbrwruurwuuurrwrbguurbuuuggu
bbbwrbgwgbburgbuwguwbwwgggwrwrgwwgbgwgwggubgwwbbguwbuw
bgrgrrgbwwwwrubwbrwbwuwwrbuwurbbbggbggruuubwgubb
wurrruuwbbugbbuuwggrurgrggbgwgbggwburwgwbw
grrbwrgbuwgbrbggbwrrwgbwwrwrgbugrrbwgguwuu
gbbwbwruubuwruburgbrrgwrwbguurbgbgwwrbbgrwbgbgbb
wbbggbwwwgrggwruwbrbwgrrwgruubuubrgrrrgrrb
wrrwgwbwubwbbururrwguwrruggbbuguuwwrgrrgbgrgwbwrggbbuuuuw
bbwgbugbubrggwrguuwwrrwgrguwbrbwggwuwbgrgbrugubwgubu
rrgugwwubbuwbwbrubbwbwbrbbbggubbwbwrwgggwbbbuugugbgg
uubgbuuwrbubbbguburgrwbrrgrwrbugbbwgwgbgrburb
wrrwbbrurrwgwwrwwbwgwbuugguururbbbwuubgugugrwbuubwwwrg
ugwrgwwugrbwggbruuurggguggguwwggwwbwwwrwbuubrwgwgb
gbwbbbbrrbrrgbuwggbrwuwruururgwwrugburgbrbgwb
wrgugwuurrrgbuguuuuwbrbwuwurbuwbbguwwwgrrbruurguggwgwu
ugbbbggugrggugurwruggurgrubggwrguggrgbgrwbgb
rrrbruwrrrgwwgrubrbugwgwubgbwguwuwwgrwgbwbrugruwgbgbbr
rrbugwwubgbwggbgbwbgbuurwwubwbuuwububwbbguw
ruruguwgbburbuwbuwbguuwugrrrgrgwwuurrwruwrrbbgrwr
urgugubwwbubbrwwwrbbbuuburwugbbbbwruubbgwbgwrrrwubgwbgr
bubbbgwrwwuwrruwgbrbrurbrrgugurrbwrguwwgwgbbg
rrggggrgrrgbrgrubrbwrbbrugrubwwuuwbrgubbbub
brgwgubrugwrguwuruubwgbggbwwwgguwwbgwguburgugruwgbruwbgwwg
grgubwurgruggrgrwbrubugbrbuguggbbgwgbgrwbggbg
guurwggurbguwgwwgubwbwubwbrbrwubbwgbuwbwgrgwbwugbubbbwrwrb
bgrubbuurgrrwgwgguurbrrgwrbwbruwububrrwbgggbwggwuugbrbwwgu
bbgggrbgrugrbgrrururgbrbwurwubuwgrrubgrwwuw
bruggwgrgbubrwugbbwbuwguuwrgugggguwbrrugubgggwwrguwwg
wrguggwuuwuubbrgwwwgubbggwggugurrguwbwgburwuwgguuu
wwwrurgrwrwbbbrbrrggubbwgbbubggrwbbbbwbwgruruwrbw
uguwuwwubgrubgbwrggrwbwwgruuugurgugruwrbuggrrwgwrbbrrg
urgurbgbggrbgrwwbbuwguwgurwrbgbubwwrwwrwuuwuwwur
wwggwgwrruwurgurruuwwwbrggwwuuwbggrggwrbbwu
wuuburbuggrururbubwubwgugwbugbrwwwwbwgrrgwb
wbruwwrrbuuurrwggrgrwrwbbbbbbguuwrrrrggbrurrbggb
wgbbuwgwwruwwgrurrugubbgugwgubwggbrbbbguuuwruwwuw
wuwbrbgbwrwbubruwbrbwubgwrrbgbuuruuwrwbrrgubgg
brbgwgrwbggguwubguuubbrbwgrwbbrwwwuwggrwrrgwww
guugrgbuwbrwubuubuwuwrrbrgrgrguwrwggwbggbrwrrwwub
rrbwwburbwrrugwugwgbuubbggbwwubwrrwggurubuurwgb
rwrugwgbrgrwurubbruuuwburbuuwurgbbgrbgrbrbwurbgu
wwrbrwwwbgbbuwrwbgubgggrrggwwwgugrgrwubwbg
bbbwugwwbubuubwgwbuurwbwbbbggbwggrruuwwgrwgb
gwgrrbwurwgbwurbubwrbwbwuwwrubuuwrbbgbrrrrwbb
bwuuuruwuwuurgrgbwrguwrgurbrrwuggbwwbwwgggubbrgbr
grbwrwrggbgbrwbgugrwrgbruruwwrugbuwwwrgbbrrwwgb
wwbguwrwwbwbrwuurbbwurubbgbubrrubbbgurubuurwgr
ubwwrbwgbruggbgugwwgrwurggruubggbrrugrwgbuubgbgbbubru
buurwuguwguwbuurwwbuwbggrrbbuwgrgrrugbrurrr
wububgwuguwrbbggbgwubwbbgrurgwwbrbbggggwuw
rbwrbburbburrrwurubwuwrrrwbguwwwrbrbbuubbuw
wrgubbrguwbgbgwuwugbrrbrrwruwwwbggbuuwbgug
wgrgwrgugrgurbrgwwgbuwbwrgrrwurubwbuuwwugbrwugubwbwwugwbr
bwrubgbbrwurrrrrgwgrgugbgguwrwrrgrwuguuwuruwbrgbwubug
uubrggwwwwrbbwwwruubgubugbgbrbugggwrgrwgrbwguwuugug
bwuuruuuuwwuwrbbwbgrbbuggrurrrrubbrbrburubgurgwrgbburuwr
urwwururgbwrrbuwbwggrrugbbbrbrwbgwuwwwwwg
gwgbggbbwwbrruuwurubwbuuwurubrgrrbbggburrubrrrrb
gbbgwgrgbugrrgbrrgbbgurbugbrurrgrwgwuggrrgwgwubuugruggrur
bgrubbugbbgbbgwrrrwwwurbbgbgwgwuruugrurubbwbwgwru
rrwwrubgwwrrwgrubuuwbbgrubrwbrrgbggbuguguwgu
ubwuburwrgubrgwrubgugbuwrugugruggbruwwbbugb
ruuurrgbwrruggurgugrrwugubbuggbguwwbrggrguuwurgruggr
bruwguwgbubbruwurugrwwbrurrwrwwrwguurrwwwwwrwuubu
wrbrwuuruwrrwwwrwbgrbwbruwgwgwwbwwubguburbuwugurr
wuwguwgrurrurburugrwwwuwwbwguubrbwuwbguwbbrugwb
wwrbrubgubrbuwuwuwburrrurbrrwwwrbgrgrrbrwgrugrgw
buwbbugrgrbrwuwbrbubuurrgwbbuwwggugrbgrrbwr
gbguwwuugruwgrbbrgrwgwrwwurgguwwruwwbrgrrwgrgbwbwwbwu
bbburbbuubrwgrbubbbgrurrrbugwggggrwrguwrggrbuggwwrru
gbgubrugrbbburguuwrurgwbrwwuubwuggwgbbbwbgbgrwub
bbrgrurrgrgrggrwgrgrgbwrrgbrrbwgrruuwbgwwg
ugwwgrbbubwwgwrugwuurugwbuwggwrubwbwbuubwgrbwrwrgrwwwwwwwg
wwbwwbrbgwuuurggububggrgwgbwbgrwrgrgrrbwwugbw
wwggwgruwurgrruurwrwwrwuuwwwugruwrubggwwruwwg
rrbguuurwuwggbrwrugrbgurgrrgrggrgbwubgruwguurrgr
wwwbwgrubrruuurgrwrwurwgwrwuurwrggbggrgwwg
bwruururuggwwubguwuuwurbrrwgubrwbwuwgwugwrrbbubwbuburwwg
rgbwuwurwrubbuugwurbrbuubrwwgrgbuurgwrrbugbbggwwubr
ubbguubwurbrbbwubugwgwuuggbuuugbrwuwwgbgbbrug
wbbubbrbwuwbwbggburbrwburbbrbbrbwugwbgrwbrbggbwbwuru
gbrbbwbwrwrurbwbbbuwwbgurwuurguuwgurwgrugwbw""".trimIndent()
