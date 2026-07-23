package view

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType.Backspace
import com.googlecode.lanterna.input.KeyType.Character
import com.googlecode.lanterna.input.KeyType.Enter

/**
 * A Lanterna [TextBox] appropriate for showing a constant stream or feed of text. This text box is non-interactable/
 * read-only. Use [addLineAndScrollDown] instead of [addLine] to automatically scroll to the last line of text.
 *
 * @constructor Creates a new [NonFocusableTextBox].
 *
 * @param size The size of the text box. It is recommended to hardcocde this as dynamic layouts are unpredictable.
 * @param initialContent The initial content; defaults to empty.
 */
class NonFocusableTextBox(size: TerminalSize, initialContent: String = "") : TextBox(size, initialContent, TextBox.Style.MULTI_LINE) {
    init {
        // Filter input to allow navigation keys but block user editing
        setInputFilter { _, keyStroke: KeyStroke -> keyStroke.keyType !in arrayOf(Enter, Backspace, Character) }
    }

    /**
     * Appends a new line to the end of this text box and scrolls until the new line is visible.
     *
     * @param line the line to append to this text box.
     */
    fun addLineAndScrollDown(line: String?) {
        addLine(line)
        setCaretPosition(lineCount, 0)
    }

    /**
     * Appends a new line to the end of this text box and scrolls unless the user is initiating a manual scroll up.
     *
     * @param line the line to append to this text box.
     */
    fun addLineAndMaybeScrollDown(line: String?) {
        if (caretPosition.row == lineCount - 1) {
            addLineAndScrollDown(line)
        } else {
            addLine(line)
        }
    }
}
