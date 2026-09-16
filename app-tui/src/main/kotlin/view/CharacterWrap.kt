package view

import com.googlecode.lanterna.gui2.TextBox
import view.InterceptedKeyStroke.*

/**
 * Decorator for a Lanterna [TextBox] which allows character-level wrapping. See [CharacterWrapTextBox] for an
 * implementation example.
 */
abstract class CharacterWrap {
    protected abstract fun setText(text: String)

    protected abstract fun getText(): String

    protected abstract fun getLine(index: Int): String

    protected abstract fun getCaretPosition(): Position

    protected abstract fun setCaretPosition(position: Position)

    private fun setCaretPosition(
        row: Int,
        column: Int,
    ) = setCaretPosition(Position(row, column))

    protected abstract fun width(): Int

    /**
     * Processes keystrokes for Character, Backspace and Delete key presses from [TextBox.handleKeyStroke]. Forward
     * key presses for these characters to enable character wrap behaviour.
     *
     * @param InterceptedKeyStroke The key press, as one of [Delete], [Character] or [Backspace].
     */
    fun onKeyStroke(key: InterceptedKeyStroke) {
        when (key) {
            is Character -> onCharacterPressed(key.char)
            is Backspace -> onBackspacePressed()
            is Delete -> onDeletePressed()
        }
    }

    private fun onCharacterPressed(char: Char) {
        val position = getCaretPosition()
        char.insertAt(position)
        with(position) {
            if (isAtEndOfLine()) {
                setCaretToStartOfNextLine()
            } else {
                setCaretOneCharForward()
            }
        }
    }

    private fun onBackspacePressed() {
        val currentPosition = getCaretPosition()
        deleteCharBefore(currentPosition)
        with(currentPosition) {
            if (isAtStartOfLine()) {
                if (isFirstLine()) {
                    setCaretTopLeft()
                } else {
                    setCaretToEndOfPreviousLine()
                }
            } else {
                setCaretOneCharBack()
            }
        }
    }

    private fun onDeletePressed() {
        val currentPosition = getCaretPosition()
        deleteCharAfter(currentPosition)
    }

    private fun setCaretTopLeft() = setCaretPosition(0, 0)

    private fun Char.insertAt(pos: Position) {
        val idx = pos.asIndex()
        val char = this
        with(getText()) {
            "${substring(0, idx)}${char}${substring(idx)}"
        }.setLines()
    }

    private fun deleteCharAfter(pos: Position) {
        if (pos.isAtEndOfText()) {
            return
        }
        val idx = pos.asIndex()
        getText().run {
            substring(0, idx) + substring(idx + 1)
        }.setLines()
    }

    private fun String.toLined() = if (length < width()) this else lined()

    private fun deleteCharBefore(pos: Position) {
        if (pos.isTopLeft()) {
            return
        }
        val idx = pos.asIndex()
        with(getText()) {
            substring(0, idx - 1) + substring(idx)
        }.setLines()
    }

    private fun Position.setCaretToStartOfNextLine() = setCaretPosition(row + 1, 1)

    private fun Position.setCaretOneCharForward() = setCaretPosition(row, column + 1)

    private fun Position.setCaretOneCharBack() = setCaretPosition(row, column - 1)

    private fun Position.setCaretToEndOfPreviousLine() = setCaretPosition(row - 1, getLine(row - 1).length)

    private fun Position.isTopLeft() = column == 0 && row == 0

    private fun Position.isAtEndOfText() = asIndex() == getText().length

    private fun Position.asIndex(): Int = (0..<row).sumOf { getLine(it).length + 1 }.plus(column)

    private fun String.setLines() = toLined().also(::setText)

    private fun String.lined() = replace("\n", "").chunked(width()).joinToString("\n")

    private fun Position.isAtEndOfLine() = column == width()

    private fun Position.isAtStartOfLine() = column == 1 || getCaretPosition().column == 0

    private fun Position.isFirstLine() = row == 0

    data class Position(val row: Int, val column: Int)
}
