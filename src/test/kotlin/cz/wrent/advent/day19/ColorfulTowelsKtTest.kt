package cz.wrent.advent.day19

import cz.wrent.advent.day12.clear
import cz.wrent.advent.day12.findAllCombinations
import cz.wrent.advent.day12.findPossibleTowels
import cz.wrent.advent.day12.getCombinations
import cz.wrent.advent.day12.getCombinations2
import cz.wrent.advent.day12.getCombinations3
import org.junit.jupiter.api.Assertions.*

class ColorfulTowelsKtTest {

    @org.junit.jupiter.api.Test
    fun testPartOne() {
        val input = """r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb""".trimIndent()

        assertEquals(6, findPossibleTowels(input))
    }

    @org.junit.jupiter.api.Test
    fun testX() {
        val input =
            """rwwrww, uuwbwr, wgw, wuurr, urru, grrbr, gbg, bwgwguw, rwgruug, wbrb, ubb, brgub, bwg, rwg, uruubg, wwwbw, grr, brggug, wgbr, rurr, guwuuwur, rbrubbw, gbru, ggwu, urwbwg, bgg, buu, gggbgw, buwr, bgrb, ugr, rbwubb, gurrb, bbw, brgwgb, wrwu, gbggwru, ggbuu, wrg, rrw, gbb, gwr, gg, rgw, rwwrb, rbg, wurgww, rgr, bwub, bruw, ruwbr, rgbwww, uwbrw, buuu, ubbgg, buug, rug, bgrw, gbbr, burrrbwr, rrubug, bwguu, uuwrbu, uuur, gubr, www, rub, gbr, uggguw, bbrwu, gwgu, gug, rruuru, wwggg, gwg, gubgbrww, uwr, gb, rubb, uggbr, brrb, gu, uugr, ggg, rbu, r, rbubuwrw, bbruu, wgrb, ggrwuu, wgbub, wb, urwurb, gwwggr, wwbbr, uuu, gggrrb, bgr, bb, rwwgwg, buuwg, guurgg, uwwb, ugb, ugrr, gbw, wr, gw, rbbuwu, br, buurw, ugu, rrb, rbrw, wwubu, wrbuu, uubr, bbbruuw, rubbuwu, rubwb, ub, wbr, ru, brrw, wgwu, wubgbr, burbu, ubu, gub, wrgwrwrw, urg, gr, rrr, uuww, rwwrwuuw, ubgwwg, ggwwg, buuwr, rugbww, rrru, wbuuw, uu, gubgr, gru, buub, uruw, rugbbbrb, uugbww, bbrrru, uwgbgur, wrrr, grb, wrbbw, wgbubb, ur, bgw, bwwg, bubrb, guwgg, ggbw, ggwugrbg, gwrgur, ggwww, brr, rgbugr, uuuu, ggu, guwbw, wwwwrb, urwwu, gbrurgg, buwggwg, gurb, rbgrg, urbrgr, rwr, ugwbr, wrwg, brw, uugb, bwwu, ugub, ububbg, gbuu, rbrww, ubguwr, uuurrg, uwuugw, wrb, guu, wbu, rrgbrur, uurr, ggr, wgu, ruurug, uwrwwg, rugwg, bbbw, rbwwgrg, bub, uubb, rru, bburg, uwrwu, guwu, wwugwrr, wggbbbg, b, uubbg, rgbrg, urrb, gbgb, gwbwg, grww, uru, bgb, gwu, bbwg, burw, wggbrbru, uuwg, wurwu, wgr, brwb, bwug, ruruw, uguwu, bbb, gbgwuub, brb, ugwb, grrgb, wgbwb, wwr, wbg, wgb, rwb, bwubg, gruwb, wgg, grg, wuwb, rbrbuwb, wwgwb, bwgurww, bwwr, bgrwg, rrrgbg, wgwwguwb, rwbuw, guwgbwr, wu, uwbb, bwr, ggur, rrg, gbwuwgg, gbwwr, ubwgwwr, wwbb, ugg, gurw, uwwu, bwu, bug, rrurgg, wbrg, wgwruu, uwwugw, guw, ubrb, gbwwu, wguwbw, ggguwwb, rgu, wrw, ruu, uggbug, ubg, w, wbgw, bwrbbur, rrugbr, uug, rurbb, wur, wub, bwb, rww, bgrggrg, ggwb, brg, gwb, wwbu, rbggg, gwugr, ugwurbrb, uur, ugrwbr, bugwu, ggbgru, rgg, ugw, uwwuwwbr, ug, grgbbgg, rwbgrggb, uwur, wrgb, gur, gww, bgu, ubuub, gwwburr, ubbrwugu, rr, rbb, bwguubb, gbuuwwub, wug, wuug, wurb, rrgb, wrww, gugwgbr, ggwr, urgubwr, urr, wubwbgr, wgww, urgu, wuw, grbuw, ubwb, ubug, wru, uuurw, rgwbu, wbbuwr, wurr, ubr, bww, grwwg, u, bgrbg, uwrr, uwuwu, ggurwg, bbu, bbr, rrbgurr, bgbrggwu, rwru, rububwug, bguwwbw, gubw, rg, wwugu, buru, uwru, rrrb, rw, wbb, grgr, bggbwwwg, uwu, rbrgw, ggw, bu, bwrr, gbwuwbr, grw, bgwrw, wuur, uub, uuw, rwugg, ugrgwb, bwrrrgr, rrgrbrgw, grwruug, urw, rb, bg, bwrwwgg, bw, gwwguub, wugb, wwb, wuu, urwg, brwbb, bgbg, rwrrub, wbrwu, guur, gbu, rbbgb, buuubug, gbwruuw, wbwwrub, bru, bbg, brrbbr, rgggb, rbug, wrrrg, ubgu, grrru, buw, uurrg, bur, gbbwwg, ubw, grubrr, wrbgbgwg, ruwwgww, guwrrbu, wbrw, uwb, wrrwwg, wbw, rbbg, gwrr, rwrrwu, wrbrrw, uww, uwg, rbuur, gugwrb, bwgu, rbw, ggrwr, ggb, gbbb, uwwgru, wrgrww, rubwub, urb, rbr, ruw, wgwgw, gbub, rwu, bguub, uw

ggbuuuwbbuubggwbwgwurburwggrwgbbwgbuwrbgbwggbgwur""".trimIndent()

        assertEquals(1, findPossibleTowels(input))
    }

    @org.junit.jupiter.api.Test
    fun testPartTwo() {
        val input = """r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb""".trimIndent()

        assertEquals(16, findAllCombinations(input))
    }

    @org.junit.jupiter.api.Test
    fun testPattern() {
        val towelList = listOf("r", "wr", "b", "g", "bwu", "rb", "gb", "br")
        // assertEquals(setOf(mapOf("b" to 1)), getCombinations("b", towelList))
        // clear()
        // assertEquals(setOf(mapOf("r" to 1)), getCombinations("r", towelList))
        // clear()
        // assertEquals(setOf(mapOf("r" to 3)), getCombinations("rrr", towelList))
        // clear()
        // assertEquals(setOf(mapOf("br" to 1), mapOf("b" to 1, "r" to 1)), getCombinations("br", towelList))
        // clear()
        assertEquals(setOf(mapOf("br" to 1, "r" to 1), mapOf("b" to 1, "r" to 2)), getCombinations("br|r", towelList))
    }

    @org.junit.jupiter.api.Test
    fun testPattern2() {
        val towelList = listOf("r", "wr", "b", "g", "bwu", "rb", "gb", "br")
        // assertEquals(1, getCombinations2("b", towelList))
        // clear()
        // assertEquals(1, getCombinations2("r", towelList))
        // clear()
        // assertEquals(1, getCombinations2("rrr", towelList))
        // clear()
        assertEquals(2, getCombinations2("br", towelList))
        clear()
        assertEquals(2, getCombinations2("br|r", towelList))
    }

    @org.junit.jupiter.api.Test
    fun testPattern3() {
        val towelList = listOf("r", "wr", "b", "g", "bwu", "rb", "gb", "br")
        // assertEquals(1, getCombinations3("b", towelList))
        // clear()
        // assertEquals(1, getCombinations3("r", towelList))
        // clear()
        // assertEquals(1, getCombinations3("rrr", towelList))
        // clear()
        // assertEquals(2, getCombinations3("br", towelList))
        // clear()
        // assertEquals(2, getCombinations3("brr", towelList))
        // clear()
        assertEquals(2, getCombinations3("brwrr", towelList))
    }
}
