package utils

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.insertSubStringAt
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.leavesLineTooEmpty
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.lineLastIndex
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.lineStartIndex
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.overflowed
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.substituteSubStringAt
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.theresWhiteSpaceInCurrentLine
import java.lang.System.lineSeparator

class PersonalizableSmartWrapUtilsFunctionsTest {
    @ParameterizedTest
    @MethodSource("provideInsertString")
    fun `when insertSubStringAt called then add at the given index and shift remainder of word right`(
        input: String,
        index: Int,
        insert: String,
        expected: String
    ) {
        input.insertSubStringAt(index, insert).shouldBeEqual(expected)

    }


    @ParameterizedTest
    @MethodSource("provideSubstring")
    fun `when substituteSubStringAt called then replace char at stated index`(
        input: String,
        index: Int,
        insert: String,
        expected: String
    ) {
        input.substituteSubStringAt(index, insert).shouldBeEqual(expected)
    }

    @ParameterizedTest
    @MethodSource("provideSeparator")
    fun `when lineStartIndex called then find the index after the lineSeparator`(
        input: String,
        param: String,
        expected: Int
    ) {
        input.lineStartIndex(param).shouldBeEqual(expected)
    }

    @Test
    fun `when lineLastIndex called then find the index of the final character`() {
        val myString = "ciao${lineSeparator()}spr"
        val currentLineLen = myString.length - myString.lineStartIndex(lineSeparator())
        currentLineLen.shouldBeEqual(3)
        lineLastIndex(
            myString.lineStartIndex(lineSeparator()),
            currentLineLen
        ).shouldBeEqual(7 - 1 + lineSeparator().length)
    }

    @ParameterizedTest
    @MethodSource("provideOverflow")
    fun `given overflowed called, when line length greater than maxCharPerLine then return True else return False`(
        input: String,
        maxChars: Int,
        expected: Boolean
    ) {
        overflowed(input.length, maxChars).shouldBeEqual(expected)
    }

    @ParameterizedTest
    @MethodSource("provideMinFill")
    fun `given leavesLineTooEmpty called, when minFill less than or equal to lastWhiteSpaceIndex then return false else return true`(
        lastWhite: Int,
        start: Int,
        mf: Int,
        expected: Boolean
    ) {
        leavesLineTooEmpty(lastWhite, start, mf).shouldBeEqual(expected)
    }

    companion object {
        @JvmStatic
        fun provideMinFill() = listOf(
            // You can use actual code/logic here
            Arguments.of(5, 0, 4, false),
            Arguments.of(5, 0, 7, true)
        )

        @JvmStatic
        fun provideInsertString() = listOf(
            // You can use actual code/logic here
            Arguments.of("ciao", 0, "X", "Xciao"),
            Arguments.of("ciao", "ciao".length, "XX", "ciaoXX")
        )

        @JvmStatic
        fun provideSubstring() = listOf(
            // You can use actual code/logic here
            Arguments.of("ciao", 2, "X", "ciXo"),
            Arguments.of("ciao", 2, "XX", "ciXXo")
        )

        @JvmStatic
        fun provideSeparator() = listOf(
            // You can use actual code/logic here
            Arguments.of("ciao${lineSeparator()}ciao", lineSeparator(), 4 + lineSeparator().length),
            Arguments.of("ciao${lineSeparator()}", lineSeparator(), 4 + lineSeparator().length)
        )

        @JvmStatic
        fun provideOverflow() = listOf(
            // You can use actual code/logic here
            Arguments.of("ciao ", 5, false),
            Arguments.of("ciaoo ", 5, true)
        )

        @JvmStatic
        fun provideIndices() = listOf(
            // You can use actual code/logic here
            Arguments.of(6, 6, true),
            Arguments.of(6, 7, false),
            Arguments.of(0, 6, false),
            Arguments.of(-1, 0, false),
        )
    }

    @ParameterizedTest
    @MethodSource("provideIndices")
    fun `Given theresWhiteSpaceInCurrentLine called, when lastWhiteSpaceIndex less than currentLineStartIndex return false else return true`(
        lastWhite: Int,
        start: Int,
        expected: Boolean
    ) {
        theresWhiteSpaceInCurrentLine(6, 6).shouldBeTrue()
        theresWhiteSpaceInCurrentLine(6, 7).shouldBeFalse()
        theresWhiteSpaceInCurrentLine(0, 6).shouldBeFalse()
        theresWhiteSpaceInCurrentLine(-1, 0).shouldBeFalse()
    }

}
