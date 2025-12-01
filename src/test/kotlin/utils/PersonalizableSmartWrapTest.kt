package utils

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldBeEmpty
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
    fun `when string with no spaces is longer than twice maxCharPerLine, then return string split in 3 lines with hyphens`() {
        val myString = "1234567890123"
        myString.smartWrap(6).shouldBeEqual("12345-${lineSeparator()}67890-${lineSeparator()}123")
    }

    @Test
    fun `when string with spaces is longer than maxCharPerLine, then return string split in 2 lines on spaces`() {
        val myString = "12345 789012"
        myString.smartWrap(6).shouldBeEqual("12345${lineSeparator()}789012")
    }

    @Test
    fun `given last line in multiline paragraph is completely full, when appending string, then append string to new line without preceding space`() {
        val myString = "123456 789012"
        myString.smartWrap(6).shouldBeEqual("123456${lineSeparator()}789012")
    }

    @Test
    fun `when string with spaces is longer than maxCharPerLine, and second word is longer than maxCharPerLine, then return string split in 3 lines`() {
        val myString = "12345 8901234567890"
        myString.smartWrap(
            6,
        ).shouldBeEqual("12345${lineSeparator()}89012-${lineSeparator()}34567-${lineSeparator()}890")
    }

    @Test
    fun `given sting contains multiple contiguous spaces, when total paragraph length is less than maxCharPerLine, then return paragraph`() {
        val myString = "12345    89012"
        myString.smartWrap(20).shouldBeEqual("12345    89012")
    }

    @Test
    fun `given sting contains multiple contiguous spaces, when total paragraph length is more than maxCharPerLine, then return paragraph wrapped`() {
        val myString = "12345    89012"
        myString.smartWrap(10).shouldBeEqual("12345   ${lineSeparator()}89012")
    }

    @Test
    fun `given sting contains multiple contiguous spaces, when total paragraph length is more than maxCharPerLine, and number of spaces overflows line length, then return wrapped paragraph with new line consuming one space`() {
        val myString = "12345       89012"
        myString.smartWrap(10).shouldBeEqual("12345     ${lineSeparator()} 89012")
    }

//    @Test
//    fun `ToDo - minFill acts as it should`() {
//        true.shouldBeFalse()
//    }


    companion object {
        private fun String.smartWrap(maxCharsPerLine: Int): String = PersonalizableSmartWrap(maxCharsPerLine).invoke(this)
    }
}
