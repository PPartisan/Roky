package utils

import java.lang.System.lineSeparator

class SmartWrapIndenting(
    private val maxCharsPerLine: Int= 54,
    private val lineSeparator: String = lineSeparator(),
    private val indentation: String = "   ",
    private val minFillFraction: Double = 0.4) : (String) -> String {

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
        private val lineSeparator: String) {

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
                                    paragraph = paragraph.substituteCharAt(
                                        lastWhiteSpaceIndex,
                                        "$lineSeparator$indentation**"
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

        private fun isBreakableWhiteSpace(ch: Char): Boolean =
            ch == ' ' || ch == '\t'

        private fun leavesLineTooEmpty(): Boolean =
            lastWhiteSpaceIndex - currentLineStartIndex < minFill

        private fun theresWhiteSpaceInCurrentLine(): Boolean =
            lastWhiteSpaceIndex >= currentLineStartIndex

        private fun recomputeValsForNewLine() {
            currentLineStartIndex =
                paragraph.lastIndexOf(lineSeparator) + lineSeparator.length

            currentLineLen =
                paragraph.length - currentLineStartIndex

            lastWhiteSpaceIndex = -1
        }

        private fun currentLineLastIndex(): Int =
            currentLineStartIndex + maxCharsPerLine - 1

        private fun cutWordAndHyphenate() {
            paragraph = paragraph.insertSubStringAt(
                currentLineLastIndex(),
                "-$lineSeparator$indentation**"
            )

            recomputeValsForNewLine()
        }

        private fun startNewLine() {
            paragraph += lineSeparator + indentation +"UU"
            recomputeValsForNewLine()
        }

        private fun overflowed(): Boolean =
            currentLineLen > maxCharsPerLine
    }
}



//fun String.smartWrapIndenting(maxCharsPerLine: Int, minFillFraction: Double = 0.4): String {
//    require(maxCharsPerLine >= 2) { "Character limit should be at least 2" }
//
//    var paragraph = ""
//    var currentLineStartIndex = 0          // index in paragraph where current line starts
//    var currentLineLen = 0                       // length of current line
//    var lastWhiteSpaceIndex = -1                    // index in paragraph of last space/tab in current line
//    val minFill = (maxCharsPerLine * minFillFraction).toInt().coerceAtMost(maxCharsPerLine - 1)
//
//
//    fun isBreakableWhiteSpace(ch: Char) = ((ch == ' ') || (ch == '\t'))  //with tab, we might have to change stuff
//
//    fun leavesLineTooEmpty() = lastWhiteSpaceIndex - currentLineStartIndex < minFill
//
//    fun theresWhiteSpaceInCurrentLine() = lastWhiteSpaceIndex >= currentLineStartIndex
//
//    fun recomputeValsForNewLine() {
//        currentLineStartIndex = paragraph.lastIndexOf(lineSeparator()) + (lineSeparator().length-1) + 1
//        currentLineLen = paragraph.length - currentLineStartIndex  //Recompute current line length: from that new line start to the end.
//        lastWhiteSpaceIndex = -1 //reset white space index inside current line to negative (there's none): there can't be one because the one we just substituted was the last one in the paragraph
//    }
//
//    fun currentLineLastIndex() = currentLineStartIndex + (maxCharsPerLine - 1)
//
//    fun cutWordAndHyphenate() {
//        paragraph = paragraph.insertSubStringAt(currentLineLastIndex(), "-${lineSeparator()}   ")
//        recomputeValsForNewLine()
//    }
//
//    fun startNewLine(){
//        paragraph+=lineSeparator()
//        recomputeValsForNewLine()
//    }
//
//    fun overflowed() = currentLineLen > maxCharsPerLine
//
//    forEach { ch ->
//        when (ch) {
//            '\n' -> startNewLine()
//            else -> {
//                paragraph+=ch
//                currentLineLen++
//                if (isBreakableWhiteSpace(ch)) lastWhiteSpaceIndex = paragraph.lastIndex  //save the position of the white space
//                if (overflowed()) {        //If this has made us go over the line limit
//                    if (theresWhiteSpaceInCurrentLine()) {    //If we have a space/tab to break at inside the current line
//                        if (!leavesLineTooEmpty()) {          //If it wouldn't leave the line too empty, we can go to new line
//                            paragraph=paragraph.substituteCharAt(lastWhiteSpaceIndex, "${lineSeparator()}   ")  //substitutes the white space with the \n char
//                            recomputeValsForNewLine()
//                        } else cutWordAndHyphenate() // if it would leave line too empty we need to hyphenate
//                    } else cutWordAndHyphenate() // if no whitespace in the current line => we just cut.
//                }
//            }
//        }
//    }
//    return paragraph
//}
