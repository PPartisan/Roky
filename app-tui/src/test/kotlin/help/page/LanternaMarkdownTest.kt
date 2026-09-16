package help.page

import com.vladsch.flexmark.parser.Parser
import help.FormattedTextRow
import help.FormattedTextRow.*
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test

class LanternaMarkdownTest {
    @Test
    fun `when empty string, then parse nothing`() {
        "".toFormattedRows() shouldBeEqual listOf()
    }

    @Test
    fun `when bold formatted string, then parse bold text`() {
        "**boldtext**".toFormattedRows() shouldContainInOrder listOf(Bold("boldtext"))
    }

    @Test
    fun `when italic formatted string, then parse italic text`() {
        "*italictext*".toFormattedRows() shouldContainInOrder listOf(Italic("italictext"))
    }

    @Test
    fun `when header formatted string, then parse header text`() {
        "# headertext".toFormattedRows() shouldContainInOrder listOf(Header("headertext"))
    }

    @Test
    fun `when incorrectly formatted header string, then parse as plain text`() {
        "#headertext".toFormattedRows() shouldContainInOrder listOf(PlainText("#headertext"))
    }

    @Test
    fun `when string with no special formatting, then parse as plain text`() {
        "Jirachi".toFormattedRows() shouldContainInOrder listOf(PlainText("Jirachi"))
    }

    @Test
    fun `when string is line break, add exists between two plain texts literals, then render plain text and hard line break`() {
        """
        line1
        \
        line2
        """.trimIndent().toFormattedRows() shouldContainInOrder
            listOf(PlainText("line1"), LineBreak(), PlainText("line2"))
    }

    @Test
    fun `when string is only a hard line break, then render as plain text`() {
        "\\".toFormattedRows() shouldContainInOrder listOf(PlainText("\\"))
    }

    @Test
    fun `when string is unsupported markdown, then render as plain text without formatting`() {
        """
        ```code```
        """.trimIndent().toFormattedRows() shouldContainInOrder listOf(PlainText("code"))
    }

    @Test
    fun `when string is hyperlink formatted, then render as hyperlink`() {
        "[click me](example.com)".toFormattedRows() shouldContainInOrder
            listOf(
                Hyperlink("click me", "example.com"),
            )
    }

    @Test
    fun `given multiline string with different format types, when parsing, then render each line correctly`() {
        """
        # header
        \
        **bold text**
        *italic*
        regular text
        \
        [click me again](example.co.uk)
        """.trimIndent().toFormattedRows() shouldContainInOrder
            listOf(
                Header(text = "header"),
                LineBreak(),
                Bold(text = "bold text"),
                Italic(text = "italic"),
                PlainText(text = "regular text"),
                LineBreak(),
                Hyperlink(text = "click me again", url = "example.co.uk"),
            )
    }

    companion object {
        private fun String.toFormattedRows(): List<FormattedTextRow> =
            LanternaMarkdown.render(Parser.builder().build().parse(this))
    }
}
