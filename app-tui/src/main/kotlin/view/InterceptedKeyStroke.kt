package view

sealed interface InterceptedKeyStroke {
    data class Character(val char: Char) : InterceptedKeyStroke

    data object Backspace : InterceptedKeyStroke

    data object Delete : InterceptedKeyStroke
}
