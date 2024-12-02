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
//PASS
    @Test
    fun `when incorrectly formatted header string, then parse as plain text`() {
        "#headertext".toFormattedRows() shouldContainInOrder listOf(PlainText("#headertext"))
    }
//peepee
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
                listOf(PlainText("line1"),LineBreak(),PlainText("line2"))
    }

    @Test
    fun `when string is only a hard line break, then render as plain text`() {
        "\\".toFormattedRows() shouldContainInOrder listOf(PlainText("\\"))
    }

    companion object{
        private fun String.toFormattedRows() : List<FormattedTextRow> =
            LanternaMarkdown().render(Parser.builder().build().parse(this))
    }

}
