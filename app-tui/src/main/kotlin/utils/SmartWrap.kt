package utils

import org.jetbrains.annotations.VisibleForTesting
import utils.SmartWrap.Companion.canFitOnALine
import java.lang.System.lineSeparator

class SmartWrap(
    private val maxCharsPerLine: Int,
    private val lineSeparator: String = lineSeparator(),
) {
    operator fun invoke(input: String): String =
        with(input) {
            if (length <= maxCharsPerLine) {
                return this
            }
            val words = split("\\s".toRegex())
            var paragraph = ""
            words.forEach { word ->
                if (paragraph.wouldOverFlow(maxCharsPerLine, word)) {
                    if (paragraph.isNotEmpty()) paragraph += lineSeparator
                    paragraph +=
                        if (word.canFitOnALine(maxCharsPerLine)) {
                            word
                        } else {
                            word.chunked(maxCharsPerLine - 1).joinToString(lineSeparator) { "$it-" }.trimEnd('-')
                        }
                } else {
                    paragraph += word
                }
                paragraph += if (paragraph.isLineFull(maxCharsPerLine)) lineSeparator else " "
            }
            return paragraph.trimEnd()
        }

    companion object {
        @VisibleForTesting
        fun String.isSingleLineParagraph() = !contains(lineSeparator())

        @VisibleForTesting
        fun String.canFitOnALine(maxCharsPerLine: Int) = length <= maxCharsPerLine

        @VisibleForTesting
        fun String.wouldOverFlow(
            maxCharsPerLine: Int,
            word: String,
        ) = charactersUsedInLastLine() + word.length > maxCharsPerLine

        @VisibleForTesting
        fun String.charactersUsedInLastLine(): Int =
            if (isSingleLineParagraph()) {
                length
            } else {
                lastIndex - (lastIndexOf(lineSeparator()) + (lineSeparator().length - 1))
            }

        @VisibleForTesting
        fun String.charactersRemainingInLastLine(maxCharsPerLine: Int) = maxCharsPerLine - charactersUsedInLastLine()

        @VisibleForTesting
        fun String.isLineFull(maxCharsPerLine: Int) = charactersRemainingInLastLine(maxCharsPerLine) <= 0
    }
}
