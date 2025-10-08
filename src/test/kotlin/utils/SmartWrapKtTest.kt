package utils

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldBeEmpty
import org.junit.jupiter.api.Test

class SmartWrapKtTest {
    @Test
    fun `given empty String, when cutoff size is 0, then return empty string`() {
        "".smartWrap(0).shouldBeEmpty()
    }

    @Test
    fun `when a String shorter than the cutoff size, then return original string`(){
        "ciao".smartWrap(10).shouldBeEqual("ciao")
    }

    @Test
    fun `when string is longer than cutoff size, and the string contains no white space, then return split string with hyphenation`() {
        "loooongStriiing".smartWrap(10).shouldBeEqual("loooongSt-\nriiing")
    }
}
