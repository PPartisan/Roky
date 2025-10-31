package utils

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldBeEmpty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.smartWrapMartine
import java.lang.System.lineSeparator

class SmartWrapKtTest {
    @Test
    fun `given empty String, when cutoff size is 0, then return empty string`() {
        "".smartWrapMartine(0).shouldBeEmpty()
    }

    @Test
    fun `when a String shorter than the cutoff size, then return original string`(){
        "ciao".smartWrapMartine(10).shouldBeEqual("ciao")
    }

    @Test
    fun `when string is longer than cutoff size, and the string contains no white space, then return split string with hyphenation`() {
        "loooongStriiing".smartWrapMartine(10).shouldBeEqual("loooongSt-${lineSeparator()}riiing")
    }

    @Test
    fun `when string with no spaces is longer than twice maxCharPerLine, then return string split in 3 lines with hyphens`() {
        val myString = "1234567890123"
        myString.smartWrapMartine(6).shouldBeEqual("12345-${lineSeparator()}67890-${lineSeparator()}123")
    }

    @Test
    fun `when string with spaces is longer than maxCharPerLine, then return string split in 2 lines on spaces`() {
        val myString = "12345 789012"
        myString.smartWrapMartine(6).shouldBeEqual("12345${lineSeparator()}789012")
    }

    @Test
    fun `when string with spaces is longer than maxCharPerLine, and second word is longer than maxCharPerLine, then return string split in 3 lines`() {
        val myString = "12345 8901234567890"
        myString.smartWrapMartine(6).shouldBeEqual("12345${lineSeparator()}89012-${lineSeparator()}34567-${lineSeparator()}890")
    }

    //Added
    @Test
    fun `when first word is 2 characters shorter than maxCharPerLine, second word is sent to next line without hyphenating`(){
        val myString = "12345 890"
        myString.smartWrapMartine(7).shouldBeEqual("12345${lineSeparator()}890")
    }

    @Test  //doesn't
    fun `when first character is a space, then return string should start with space`(){
        " ciaoo ciaoo".smartWrapMartine(6).shouldBeEqual(" ciaoo${lineSeparator()}ciaoo")
    }

    @Test
    fun `when first character is a end of Line, then return string should start with end of Line`(){
        "\nciaoo ciaoo".smartWrapMartine(6).shouldBeEqual("${lineSeparator()}ciaoo${lineSeparator()}ciaoo")
    }

    @Test
    fun `when string starts with two spaces in a row, then starting string should start with 2 spaces`(){
        "  ciao".smartWrapMartine(6).shouldBeEqual("  ciao")
    }

    @Test
    fun `when string starts with two spaces in a row, and maxCharsPerLine is less than spaces+firt word, then starting string should start with 2 spaces and it should wrap at end of first word`(){
        "  ciao ciao".smartWrapMartine(6).shouldBeEqual("  ciao${lineSeparator()}ciao")
    }

    @Test
    fun `when character at index = maxCharsPerLine is endOfLine, is retains the endOfLine`() {
        "ciaoo\nmaka".smartWrapMartine(6).shouldBeEqual("ciaoo${lineSeparator()}maka")
    }

    @Test
    fun `when string has two end of line characters in a row, return string should retain them`() {
        "ciaoo\n\nmaka".smartWrapMartine(6).shouldBeEqual("ciaoo${lineSeparator()}${lineSeparator()}maka")
    }


}
