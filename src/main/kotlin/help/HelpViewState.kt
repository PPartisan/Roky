package help


sealed interface FormattedTextRow {
    val text: String

    data class Bold(
        override val text: String
    ) : FormattedTextRow
    data class Italic(
        override val text: String
    ) : FormattedTextRow
    data class Underline(
        override val text: String
    ) : FormattedTextRow
    data class Hyperlink(
        override val text: String
    ) : FormattedTextRow
    data class Header(
        override val text: String
    ) : FormattedTextRow
}

sealed interface HelpViewState {
    val rows: List<FormattedTextRow>
}

data class LoadedHelpViewState(
    override val rows: List<FormattedTextRow>
) : HelpViewState

data class LoadingHelpViewState(
    override val rows: List<FormattedTextRow> = listOf()
) : HelpViewState
