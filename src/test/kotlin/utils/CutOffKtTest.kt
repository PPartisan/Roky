package utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test

class CutOffKtTest {
    @Test
    fun `when string is less than max character limit, then return same string`() {
        val input = "testyy"
        input.cutOff(100).shouldBeEqual(input)
    }

    @Test
    fun `when string is longer than max character limit, then return truncated string with ellipsis`() {
        val input = "Testyyyyyyyyyyyyyyyyyyyy"
        input.cutOff(5).shouldBeEqual("Test…")
    }

    @Test
    fun `when max character limit is negative, then throw exception`() {
        val exception =
            shouldThrow<IllegalArgumentException> {
                "willCrash".cutOff(-10)
            }
        exception.message?.shouldBeEqual("Character limit should be at least 1")
    }

    @Test
    fun `when max character limit is 0, then throw exception`() {
        val exception =
            shouldThrow<IllegalArgumentException> {
                "willCrash".cutOff(0)
            }
        exception.message?.shouldBeEqual("Character limit should be at least 1")
    }

    @Test
    fun `when max character limit is 1, then return ellipsis character`() {
        "testy".cutOff(1).shouldBeEqual("…")
    }
}
