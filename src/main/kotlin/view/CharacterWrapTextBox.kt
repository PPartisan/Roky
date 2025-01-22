package view

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
    private val wrap: CharacterWrap =
        object : CharacterWrap() {
            override fun setText(text: String) {
                this@CharacterWrapTextBox.text = text
            }

            override fun getCaretPosition(): Position =
                this@CharacterWrapTextBox.caretPosition.run { Position(row = row, column = column) }

            override fun setCaretPosition(position: Position) {
                this@CharacterWrapTextBox.setCaretPosition(position.row, position.column)
            }

            override fun getText(): String = this@CharacterWrapTextBox.text

            override fun getLine(index: Int): String = this@CharacterWrapTextBox.getLine(index)

            override fun width(): Int = size.columns
        }

    init {
        setCaretWarp(true)
    }

    override fun handleKeyStroke(keyStroke: KeyStroke?): Result =
        if (isReadOnly) {
            super.handleKeyStroke(keyStroke)
        } else if (keyStroke?.keyType == Enter) {
            onEnterPressed(keyStroke)
        } else {
            keyStroke.toInterceptedKeyStroke()
                ?.let(wrap::onKeyStroke)
                ?.let { HANDLED }
                ?: super.handleKeyStroke(keyStroke)
        }

    private fun KeyStroke?.toInterceptedKeyStroke(): InterceptedKeyStroke? =
        when (this?.keyType) {
            Character -> InterceptedKeyStroke.Character(character.toChar())
            Backspace -> InterceptedKeyStroke.Backspace
            Delete -> InterceptedKeyStroke.Delete
            else -> null
        }

    private fun onEnterPressed(keyStroke: KeyStroke): Result =
        onEnterPressed?.run {
            invoke()
            HANDLED
        } ?: super.handleKeyStroke(keyStroke)

    companion object {
        private val DEFAULT_TEXT_BOX_SIZE = TerminalSize(10, 2)
    }
}
