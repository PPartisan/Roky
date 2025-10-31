package utils
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test
import java.lang.System.lineSeparator

class StringUtilsFunctionsTest {
    @Test
    fun `given empty paragraph, when isSingleLineParagraph, then return true`() {
        "".isSingleLineParagraph().shouldBeTrue()
    }

    @Test
    fun `given non empty paragraph when is single line paragraph, then return true`() {
        "ciao".isSingleLineParagraph().shouldBeTrue()
    }

    @Test
    fun `given non empty paragraph and text contains new line break, then return false`() {
        "ciao${lineSeparator()}ciao".isSingleLineParagraph().shouldBeFalse()
    }

    @Test
    fun `given non empty paragraph that starts with end of line break, then return false`() {
        "${lineSeparator()}ciao".isSingleLineParagraph().shouldBeFalse()
    }

    @Test
    fun `given empty paragraph, when charactersUsedInLastLne then return 0`() {
        "".charactersUsedInLastLine() shouldBeEqual 0
    }

    @Test
    fun `given single line paragraph, when charactersUsedInLastLne then return length of input`() {
        "ciao".charactersUsedInLastLine() shouldBeEqual 4
    }

    @Test
    fun `given 2 line paragraph, when charactersUsedInLastLne then return length of last line`() {
        val myString = "ciao${lineSeparator()}ciaoo"
        println(myString.length)
        println(myString.lastIndexOf(lineSeparator()))
        myString.charactersUsedInLastLine() shouldBeEqual 5
    }

    @Test
    fun `given multiple line paragraph, when charactersUsedInLastLne then return length of last line`() {
        val myString = "ciaooo${lineSeparator()}ciao${lineSeparator()}ciaoo"
        myString.charactersUsedInLastLine() shouldBeEqual 5
    }

    @Test
    fun `given single line paragraph and non empty word, and total length is equal to max character count limit, when wouldOverflow, then return False`() {
        val paragraph = "1234"
        val myString = "567891"
        paragraph.wouldOverFlow(10, myString).shouldBeFalse()
    }

    @Test
    fun `given single line paragraph and non empty word, and total length is more than max character count limit, when wouldOverflow, then return True`() {
        val paragraph = "paragraph"
        val myString = "word"
        paragraph.wouldOverFlow(10, myString).shouldBeTrue()
    }

    @Test
    fun `given single line paragraph and non empty word, and total length is less than max character count limit, then return False`() {
        val paragraph = "paragraph"
        val myString = "word"
        paragraph.wouldOverFlow(200, myString).shouldBeFalse()
    }

    @Test
    fun `given multiple line paragraph and non empty word, and last line length plus word length is less than max character count limit, then return False`() {
        val paragraph = "ciaociao${lineSeparator()}ciao"
        val myString = "word"
        paragraph.wouldOverFlow(10, myString).shouldBeFalse()
    }

    @Test
    fun `given multiple line paragraph and non empty word, and last line length plus word length is more than max character count limit, then return True`() {
        val paragraph = "ciao${lineSeparator()}ciaociao"
        val myString = "word"
        paragraph.wouldOverFlow(10, myString).shouldBeTrue()
    }

    @Test
    fun `insertSubString with more than one char`() {
        "ciaoo12maka".insertSubStringAt(5, "34").shouldBeEqual("ciaoo3412maka")
    }


    @Test
    fun `substituteCharAt 1 char with more than one char`() {
        "ciaoo1maka".substituteCharAt(5, "23").shouldBeEqual("ciaoo23maka")
    }

}
