package utils

import kotlin.test.Test
import kotlin.test.assertEquals

class TextUtilsTest {

    @Test
    fun `cutOff should return original string if short enough`() {
        val input = "Testyy"
        val result = input.cutOff(10)
        assertEquals("Testyy", result)
    }

    @Test
    fun `cutOff should truncate and append ellipsis if too long`() { //should add tests with special characters that could make it not work?
        val input = "AVeryLongUsername"
        val result = input.cutOff(10)
        assertEquals("AVeryLon…", result) // 8 characters + "…"
    }

    @Test
    fun `smartWrap should wrap text with spaces`() {
        val input = "This is a test sentence that should wrap"
        val result = input.smartWrap(10)
        val expected = "This is a\ntest\nsentence\nthat\nshould\nwrap\n"
        assertEquals(expected, result)
    }

    @Test
    fun `smartWrap should hyphenate long words`() {
        val input = "Supercalifragilisticexpialidocious"
        val result = input.smartWrap(10)
        val expected = "Supercali-\nfragilist-\nicexpiali-\ndocious\n"
        assertEquals(expected, result)
    }
}
