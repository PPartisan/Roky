package utils

import java.lang.System.lineSeparator

class SmartWrapIndenting(
    private val maxCharsPerLine: Int = 54,
    private val lineSeparator: String = lineSeparator(),
    private val indentation: String = "   ",
    private val minFillFraction: Double = 0.4,
) : (String) -> String {
    init {
        require(maxCharsPerLine >= 3) {
            "Character limit should be at least 3"
        }
    }

    override operator fun invoke(text: String): String {
        return WrapState(maxCharsPerLine, indentation, minFillFraction, lineSeparator).wrap(text)
    }

    private class WrapState(
        private val maxCharsPerLine: Int,
        private val indentation: String,
        minFillFraction: Double,
        private val lineSeparator: String,
    ) {
        private var paragraph = ""
        private var currentLineStartIndex = 0
        private var currentLineLen = 0
        private var lastWhiteSpaceIndex = -1
        private val minFill =
            (maxCharsPerLine * minFillFraction)
                .toInt()
                .coerceAtMost(maxCharsPerLine - 1)

        fun wrap(text: String): String {
            text.forEach { ch ->
                when (ch) {
                    '\r' -> Unit
                    '\n' -> startNewLine()

                    else -> {
                        paragraph += ch
                        currentLineLen = paragraph.length - currentLineStartIndex

                        if (isBreakableWhiteSpace(ch)) {
                            lastWhiteSpaceIndex = paragraph.lastIndex
                        }

                        if (overflowed()) {
                            if (theresWhiteSpaceInCurrentLine()) {
                                if (!leavesLineTooEmpty()) {
                                    paragraph =
                                        paragraph.substituteCharAt(
                                            lastWhiteSpaceIndex,
                                            "$lineSeparator$indentation",
                                        )
                                    recomputeValsForNewLine()
                                } else {
                                    cutWordAndHyphenate()
                                }
                            } else {
                                cutWordAndHyphenate()
                            }
                        }
                    }
                }
            }

            return paragraph
        }

        private fun isBreakableWhiteSpace(ch: Char): Boolean = ch == ' ' || ch == '\t'

        private fun leavesLineTooEmpty(): Boolean = lastWhiteSpaceIndex - currentLineStartIndex < minFill

        private fun theresWhiteSpaceInCurrentLine(): Boolean = lastWhiteSpaceIndex >= currentLineStartIndex

        private fun recomputeValsForNewLine() {
            currentLineStartIndex =
                paragraph.lastIndexOf(lineSeparator) + lineSeparator.length

            currentLineLen =
                paragraph.length - currentLineStartIndex

            lastWhiteSpaceIndex = -1
        }

        private fun currentLineLastIndex(): Int = currentLineStartIndex + maxCharsPerLine - 1

        private fun cutWordAndHyphenate() {
            paragraph =
                paragraph.insertSubStringAt(
                    currentLineLastIndex(),
                    "-$lineSeparator$indentation",
                )

            recomputeValsForNewLine()
        }

        private fun startNewLine() {
            paragraph += lineSeparator + indentation
            recomputeValsForNewLine()
        }

        private fun overflowed(): Boolean = currentLineLen > maxCharsPerLine
    }
}
