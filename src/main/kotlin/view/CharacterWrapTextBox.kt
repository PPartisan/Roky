package view

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalPosition.TOP_LEFT_CORNER
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Interactable.Result
import com.googlecode.lanterna.gui2.Interactable.Result.HANDLED
import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType.*

class CharacterWrapTextBox(
    size: TerminalSize = DEFAULT_TEXT_BOX_SIZE,
    private val onEnterPressed: (() -> Unit)? = null,
) : TextBox(size) {
    private val cols = size.columns

    init {
        setCaretWarp(true)
    }

    override fun handleKeyStroke(keyStroke: KeyStroke?): Result {
        if (isReadOnly) {
            return super.handleKeyStroke(keyStroke)
        }
        return when (keyStroke?.keyType) {
            Character -> onCharacterPressed(keyStroke.character.toChar())
            Backspace -> onBackspacePressed()
            Delete -> onDeletePressed()
            Enter -> onEnterPressed(keyStroke)
            else -> super.handleKeyStroke(keyStroke)
        }
    }

    private fun onCharacterPressed(char: Char): Result {
        val currentPosition = caretPosition
        char.insertAt(currentPosition)
        with(currentPosition) {
            if (isAtEndOfLine()) {
                setCaretToStartOfNextLine()
            } else {
                setCaretOneCharForward()
            }
        }
        return HANDLED
    }

    private fun onBackspacePressed(): Result {
        val currentPosition = caretPosition
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
        return HANDLED
    }

    private fun onDeletePressed(): Result {
        val currentPosition = caretPosition
        deleteCharAfter(currentPosition)
        return HANDLED
    }

    private fun onEnterPressed(keyStroke: KeyStroke): Result =
        onEnterPressed?.run {
            invoke()
            HANDLED
        } ?: super.handleKeyStroke(keyStroke)

    private fun setCaretTopLeft() =
        with(TOP_LEFT_CORNER) {
            setCaretPosition(row, column)
        }

    private fun TerminalPosition.setCaretToStartOfNextLine() = setCaretPosition(row + 1, 1)

    private fun TerminalPosition.setCaretOneCharForward() = setCaretPosition(row, column + 1)

    private fun TerminalPosition.setCaretOneCharBack() = setCaretPosition(row, column - 1)

    private fun TerminalPosition.setCaretToEndOfPreviousLine() = setCaretPosition(row - 1, getLine(row - 1).length)

    private fun Char.insertAt(pos: TerminalPosition) {
        val idx = pos.asIndex()
        val next = text.substring(0, idx) + this + text.substring(idx)
        text =
            with(next) {
                if (length < cols) this else lined()
            }
    }

    private fun deleteCharAfter(pos: TerminalPosition) {
        if (pos.isAtEndOfText()) {
            return
        }
        val idx = pos.asIndex()
        val next = text.substring(0, idx) + text.substring(idx + 1)
        text =
            with(next) {
                if (length < cols) this else lined()
            }
    }

    private fun deleteCharBefore(pos: TerminalPosition) {
        if (pos.isTopLeft()) {
            return
        }
        val idx = pos.asIndex()
        val next = text.substring(0, idx - 1) + text.substring(idx)
        text =
            with(next) {
                if (length < cols) this else lined()
            }
    }

    private fun TerminalPosition.isTopLeft() =
        TOP_LEFT_CORNER.let {
            it.column == column && it.row == row
        }

    private fun TerminalPosition.isAtEndOfText() = asIndex() == text.length

    private fun TerminalPosition.asIndex() = (row * (cols + 1)) + column

    private fun String.lined() = replace("\n", "").chunked(cols).joinToString("\n")

    private fun TerminalPosition.isAtEndOfLine() = column == cols

    private fun TerminalPosition.isAtStartOfLine() = column == 0

    private fun TerminalPosition.isFirstLine() = row == 0

    companion object {
        private val DEFAULT_TEXT_BOX_SIZE = TerminalSize(10, 2)
    }
}
