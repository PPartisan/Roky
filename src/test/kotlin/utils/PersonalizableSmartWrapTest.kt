package utils

import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test
import utils.personalizableSmartWrap.PersonalizableSmartWrap
import java.lang.System.lineSeparator

class PersonalizableSmartWrapTest {
//    @Test
//    fun `given empty String, when cutoff size is 0, then return empty string`() {
//        "".smartWrap(0).shouldBeEmpty()
//    }

    @Test
    fun `when a String shorter than the cutoff size, then return original string`() {
        "ciao".smartWrap(10).shouldBeEqual("ciao")
    }

    @Test
    fun `when string is longer than cutoff size, and the string contains no white space, then return split string with hyphenation`() {
        "loooongStriiing".smartWrap(10).shouldBeEqual("loooongSt-${lineSeparator()}riiing")
    }

    @Test
    fun `when string with no spaces is longer than double and shorter than triple maxCharPerLine, then return string split in 3 lines with hyphens`() {
        val myString = "1234567890123"
        myString.smartWrap(6).shouldBeEqual("12345-${lineSeparator()}67890-${lineSeparator()}123")
    }

    @Test
    fun `when string with spaces is longer than maxCharPerLine and line length is greater than minFill, then return string split in 2 lines on spaces`() {
        val myString = "12345 789012"
        myString.smartWrap(6).shouldBeEqual("12345${lineSeparator()}789012")
    }

    @Test
    fun `when string with spaces is longer than maxCharPerLine and line length is less than minFill, then hyphenate`() {
        val myString = "1 23456"
        myString.smartWrap(6).shouldBeEqual("1 234-${lineSeparator()}56")
    }

    @Test
    fun `given last line in multiline paragraph is completely full, when appending string, then append string to new line without preceding space`() {
        val myString = "123456 789012"
        myString.smartWrap(6).shouldBeEqual("123456${lineSeparator()}789012")
    }

    @Test
    fun `when string with spaces is longer than maxCharPerLine, line length is greater than minFill, and second word is longer than maxCharPerLine but less than double maxCharPerLine, then return string split in 3 lines`() {
        val myString = "12345 8901234567890"
        myString.smartWrap(
            6,
        ).shouldBeEqual("12345${lineSeparator()}89012-${lineSeparator()}34567-${lineSeparator()}890")
    }

    @Test
    fun `given string contains multiple contiguous spaces, when total paragraph length is less than maxCharPerLine, then return paragraph`() {
        val myString = "12345    89012"
        myString.smartWrap(20).shouldBeEqual("12345    89012")
    }

    @Test
    fun `given string contains multiple contiguous spaces, when total paragraph length is more than maxCharPerLine, then return paragraph wrapped consuming one space`() {
        val myString = "12345    89012"
        myString.smartWrap(10).shouldBeEqual("12345   ${lineSeparator()}89012")
    }

    @Test
    fun `given sting contains multiple contiguous spaces, when total paragraph length is more than maxCharPerLine, and number of spaces overflows line length, then return wrapped paragraph consuming one space`() {
        val myString = "12345       34567"
        myString.smartWrap(10).shouldBeEqual("12345     ${lineSeparator()} 34567")
    }

    @Test
    fun `given string contains line separator character already, when lines do not exceed maxCharsPerLine, then split on lineSeparator`() {
        val myString = "1234${lineSeparator()}5678"
        myString.smartWrap(6).shouldBeEqual("1234${lineSeparator()}5678")
    }

    @Test
    fun `given string contains line separator character already, when line exceed maxCharsPerLine, then keep lineSeparator and split next line appropriately`() {
        val myString = "1234${lineSeparator()}56789012"
        myString.smartWrap(6).shouldBeEqual("1234${lineSeparator()}56789-${lineSeparator()}012")
    }


    @Test
    fun `given minFill different from 0,5, when last whitespace index in line greater than ((maxCharsPerLine multiplied by minFill)-1), then hyphenate`() {
        "ciao ciao ciao".smartWrap(8, 0.6).shouldBeEqual("ciao ci-${lineSeparator()}ao ciao")
    }


    @Test
    fun `given minFill equal to maxCharsPerLine, then always hyphenate`() {
        "cia cia ooooooo".smartWrap(10, 1.0).shouldBeEqual("cia cia o-${lineSeparator()}oooooo")
    }


    @Test
    fun `given minFill set to zero, then insert lineSeparator rather than hyphenating`() {
        "c iaooooooo".smartWrap(10, 0.0).shouldBeEqual("c${lineSeparator()}iaooooooo")
    }

//    @Test
//    fun `real text example`() {
////        val loremIpsum = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
////        loremIpsum.smartWrap(25).shouldBeEqual("a")
//        val loremIpsumWithLongWord = "Lorem Ipsum is simply dummyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
//        loremIpsumWithLongWord.smartWrap(25).shouldBeEqual("a")
////        val loremIpsumWithLineSeparator = "Lorem Ipsum is simply dummy${lineSeparator()}text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
////        loremIpsumWithLineSeparator.smartWrap(25).shouldBeEqual("a")
//    }

//    @Test
//    fun `TODO - tabs and other white spaces question mark`() {
//        TODO()
//    }

    companion object {
        private fun String.smartWrap(maxCharsPerLine: Int, minFill: Double = 0.5): String = PersonalizableSmartWrap(maxCharsPerLine, lineSeparator(), minFill).invoke(this)
    }
}
