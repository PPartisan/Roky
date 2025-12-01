package utils

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test
import utils.personalizableSmartWrap.PersonalizableSmartWrap
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.insertSubStringAt
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.lineStartIndex
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.substituteSubStringAt
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.lineLastIndex
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.overflowed
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.leavesLineTooEmpty
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.theresWhiteSpaceInCurrentLine
import java.lang.System.lineSeparator
import kotlin.math.roundToInt
import kotlin.math.roundToInt

class PersonalizableSmartWrapUtilsFunctionsTest {

    @Test
    fun `insertSubStringAt adds AT the given index and shift the rest right`() {
        "ciao".insertSubStringAt(2, "X").shouldBeEqual("ciXao")
        "ciao".insertSubStringAt(2, "XX").shouldBeEqual("ciXXao")
        "ciao".insertSubStringAt("ciao".length, "X").shouldBeEqual("ciaoX")
        "ciao".insertSubStringAt("ciao".length, "XX").shouldBeEqual("ciaoXX")
    }

     @Test
     fun `substituteSubString substitutes appropriately`() {
         "ciao".substituteSubStringAt(2, "X").shouldBeEqual("ciXo")
         "ciao".substituteSubStringAt(2, "XX").shouldBeEqual("ciXXo")
     }

    @Test
    fun `lineStartIndex finds the index right after the lineSeparator String`(){
        "ciao${lineSeparator()}ciao".lineStartIndex(lineSeparator()).shouldBeEqual(6)
        "ciao\nciao".lineStartIndex("\n").shouldBeEqual(5)
        "ciao\r\nciao".lineStartIndex("\r\n").shouldBeEqual(6)
        "ciao\rciao".lineStartIndex("\r").shouldBeEqual(5)
        "ciao\nciao".lineStartIndex(lineSeparator()).shouldBeEqual(0)
        "ciao${lineSeparator()}".lineStartIndex(lineSeparator()).shouldBeEqual(6)
    }

    @Test
    fun `lineLastIndex find the end of the line`(){
        val myString = "ciao${lineSeparator()}spr"
        val currentLineLen = myString.length - myString.lineStartIndex(lineSeparator())
        currentLineLen.shouldBeEqual(3)
        lineLastIndex(myString.lineStartIndex(lineSeparator()), currentLineLen).shouldBeEqual(8)
        val myString2 = "ciao\nspr"
        val currentLineLen2 = myString.length - myString.lineStartIndex("\n")
        lineLastIndex(myString2.lineStartIndex("\n"), currentLineLen2).shouldBeEqual(7)
    }

    @Test
    fun `overflowed returns true on line length bigger than maxCharPerLine`(){
        val nonOverflowingLine = "ciao "
        overflowed(nonOverflowingLine.length, 5).shouldBeFalse()
        val overflowingLine = "ciaoo "
        overflowed(overflowingLine.length, 5).shouldBeTrue()
    }

    @Test
    fun `leavesLineTooEmpty works`(){
        val overflowingLine = "ciaoo "
        leavesLineTooEmpty(5, 0, 4 ).shouldBeFalse()

    }

    @Test
    fun `minFill calculation gives the minimum number of characters that a line must contain before wrapping, between 0 and maxCharsPerLine-1`(){
        val minFillFraction = 0.7
        val maxCharsPerLine = 11
        (maxCharsPerLine * minFillFraction).roundToInt().coerceIn(0,maxCharsPerLine - 1).shouldBeEqual(8)
        val minFillFraction2 = 0
        val maxCharsPerLine2 = 113
        (maxCharsPerLine2.toDouble() * minFillFraction2).roundToInt().coerceIn(0,maxCharsPerLine2 - 1).shouldBeEqual(0)
        val minFillFraction3 = 1
        val maxCharsPerLine3 = 113
        (maxCharsPerLine3.toDouble() * minFillFraction3).roundToInt().coerceIn(0,maxCharsPerLine3 - 1).shouldBeEqual(112)
        val minFillFraction4 = 1.5
        val maxCharsPerLine4 = 113
        (maxCharsPerLine4.toDouble() * minFillFraction4).roundToInt().coerceIn(0,maxCharsPerLine4 - 1).shouldBeEqual(112)
        val minFillFraction5 = -1
        val maxCharsPerLine5 = 113
        (maxCharsPerLine5.toDouble() * minFillFraction5).roundToInt().coerceIn(0,maxCharsPerLine5 - 1).shouldBeEqual(0)
    }

    @Test
    fun `theresWhiteSpaceInCurrentLine counts also spaces at the end that made us overflow`(){
        theresWhiteSpaceInCurrentLine(6, 5).shouldBeTrue()
        theresWhiteSpaceInCurrentLine(6, 6).shouldBeTrue()
        theresWhiteSpaceInCurrentLine(6, 7).shouldBeFalse()
        theresWhiteSpaceInCurrentLine(0, 6).shouldBeFalse()
        theresWhiteSpaceInCurrentLine(0, 0).shouldBeTrue()
        theresWhiteSpaceInCurrentLine(-1, 6).shouldBeFalse()
        theresWhiteSpaceInCurrentLine(-1, 0).shouldBeFalse()
//        theresWhiteSpaceInCurrentLine(-1, -1).shouldBeFalse()

    }

    @Test
    fun `leavesLineTooEmpty works as expected`(){

    }


//    theresWhiteSpaceInCurrentLine
//
//    "".startNewLine(lineSeparator: String)
//
//    newLineLastIndex
//
//    "".lineStartIndex(lineSeparator: String)
//
//    leavesLineTooEmpty

    companion object {
        private fun String.smartWrap(maxCharsPerLine: Int): String = PersonalizableSmartWrap(maxCharsPerLine).invoke(this)
    }

}
