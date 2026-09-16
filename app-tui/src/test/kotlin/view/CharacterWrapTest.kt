package view

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import view.InterceptedKeyStroke.*
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CharacterWrapTest {
    private lateinit var wrap: StubCharacterWrap

    @BeforeEach
    fun setUp() {
        wrap = StubCharacterWrap()
    }

    @Test
    fun `given empty text box, when inserting char, then insert char, and move caret to end`() {
        wrap.onKeyStroke('c'.asKeyStroke())
        assertEquals(expected = "c", actual = wrap.text.toString())
        assertContentEquals(expected = intArrayOf(0, 1), actual = wrap.position)
    }

    @Test
    fun `given text box with single line, when inserting char at end, then insert char at end, and move caret to end`() {
        "text".sendToTextBox()
        wrap.onKeyStroke(Character('.'))
        assertEquals(expected = '.', actual = wrap.text.toString().last())
        assertContentEquals(expected = intArrayOf(0, "text.".length), actual = wrap.position)
    }

    @Test
    fun `given text box with line one before max width, when insert char, then insert char on new line`() {
        "Some texts".sendToTextBox()
        wrap.onKeyStroke('.'.asKeyStroke())
        assertEquals(expected = '.', actual = wrap.text.toString().last())
        assertContentEquals(expected = intArrayOf(1, 1), actual = wrap.position)
    }

    @Test
    fun `when insert char in middle of text, then track caret position in middle of text after adding new char`() {
        "Some text".sendToTextBox()
        wrap.jumpCaretTo(column = 5)
        wrap.onKeyStroke('-'.asKeyStroke())
        assertEquals("Some -text", wrap.text.toString())
        assertContentEquals(intArrayOf(0, 6), wrap.position)
    }

    @Test
    fun `given empty text box, when backspace, then do not move caret`() {
        wrap.onKeyStroke(Backspace)
        assertContentEquals(intArrayOf(0, 0), wrap.position)
    }

    @Test
    fun `give text box with single char, and caret at end, when backspace, then delete single char, and move caret to start`() {
        ".".sendToTextBox()
        assertContentEquals(intArrayOf(0, 1), wrap.position)
        wrap.onKeyStroke(Backspace)
        assertTrue(wrap.text.isEmpty())
        assertContentEquals(intArrayOf(0, 0), wrap.position)
    }

    @Test
    fun `give text box with single line, and caret in middle, when backspace, then delete char in middle, and move caret`() {
        "just a line".sendToTextBox()
        wrap.jumpCaretTo(column = 6)
        wrap.onKeyStroke(Backspace)
        assertEquals("just  line", wrap.text.toString())
        assertContentEquals(intArrayOf(0, 5), wrap.position)
    }

    @Test
    fun `given text box with multiple lines, and caret at start of next line, when backspace, then delete char at end of previous line, and move caret`() {
        "this is some multiline text".sendToTextBox()
        wrap.jumpCaretTo(row = 1, column = 1)
        wrap.onKeyStroke(Backspace)
        assertEquals("this is so\ne multilin\ne text", wrap.text.toString())
        assertContentEquals(intArrayOf(0, 10), wrap.position)
    }

    @Test
    fun `given textbox with multiple lines, and caret at end of line, when backspace, then move all text following caret back one`() {
        "this is multiline".sendToTextBox()
        assertEquals("this is mu\nltiline", wrap.text.toString())
        wrap.jumpCaretTo(column = 10)
        wrap.onKeyStroke(Backspace)
        assertEquals("this is ml\ntiline", wrap.text.toString())
        assertContentEquals(intArrayOf(0, 9), wrap.position)
    }

    @Test
    fun `given empty text box, when delete, then do nothing`() {
        wrap.onKeyStroke(Delete)
        assertTrue(wrap.text.toString().isEmpty())
        assertContentEquals(intArrayOf(0, 0), wrap.position)
    }

    @Test
    fun `given single line text box, when cursor is at start, and delete, then move text back one`() {
        "a line".sendToTextBox()
        wrap.jumpCaretTo()
        wrap.onKeyStroke(Delete)
        assertEquals(wrap.text.toString(), " line")
        assertContentEquals(intArrayOf(0, 0), wrap.position)
    }

    @Test
    fun `given single line text box, when cursor is at end, and delete, then do nothing`() {
        "a line".sendToTextBox()
        assertContentEquals(intArrayOf(0, 6), wrap.position)
        wrap.onKeyStroke(Delete)
        assertContentEquals(intArrayOf(0, 6), wrap.position)
        assertEquals(wrap.text.toString(), "a line")
    }

    @Test
    fun `given multiline text box, when cursor at end of line, and delete, then shift text back one`() {
        "this is multiline".sendToTextBox()
        assertEquals("this is mu\nltiline", wrap.text.toString())
        wrap.jumpCaretTo(column = 9)
        wrap.onKeyStroke(Delete)
        assertEquals("this is ml\ntiline", wrap.text.toString())
        assertContentEquals(intArrayOf(0, 9), wrap.position)
    }

    private fun String.sendToTextBox() = map(::Character).forEach(wrap::onKeyStroke)

    companion object {
        private fun StubCharacterWrap.jumpCaretTo(
            row: Int = 0,
            column: Int = 0,
        ) {
            position[0] = row
            position[1] = column
        }

        private fun Char.asKeyStroke() = Character(this)
    }

    class StubCharacterWrap(
        private val width: Int = 10,
    ) : CharacterWrap() {
        val text = StringBuilder()
        val position = intArrayOf(0, 0)

        override fun setText(text: String) {
            this.text.clear().append(text)
        }

        override fun getText(): String = text.toString()

        override fun getLine(index: Int): String = text.split("\n")[index]

        override fun getCaretPosition(): Position =
            with(position) {
                Position(get(0), get(1))
            }

        override fun setCaretPosition(position: Position) =
            with(this.position) {
                set(0, position.row)
                set(1, position.column)
            }

        override fun width(): Int = width
    }
}
