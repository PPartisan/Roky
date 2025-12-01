package utils.personalizableSmartWrap

import org.jetbrains.annotations.VisibleForTesting
import utils.SmartWrap
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.insertSubStringAt
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.isBreakableWhiteSpace
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.leavesLineTooEmpty
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.lineLastIndex
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.lineStartIndex
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.overflowed
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.substituteSubStringAt
import utils.personalizableSmartWrap.PersonalizableSmartWrap.Companion.theresWhiteSpaceInCurrentLine
import java.lang.System.lineSeparator
import kotlin.math.roundToInt

class PersonalizableSmartWrap(
    private val maxCharsPerLine: Int,
    private val lineSeparator: String = lineSeparator(),
    private val minFillFraction: Double = 0.5
) : SmartWrap {
    override operator fun invoke(input: String): String =
        with(input){
            require(maxCharsPerLine >= 2) { "Character limit should be at least 2" }
            var paragraph = ""
            var currentLineStartIndex = 0                   // index in paragraph where current line starts
            var currentLineLen = 0                          // length of current line
            var lastWhiteSpaceIndex = -1                    // index in paragraph of last space/tab in current line
            val minFill = (maxCharsPerLine * minFillFraction).toDouble().roundToInt().coerceIn(0, maxCharsPerLine - 1) //I do toDouble() because when it's an Int it gives error

            for ( (chindex, ch) in input.withIndex()) {
                if (ch == lineSeparator.first() && input.substring(chindex, chindex + lineSeparator.length)==lineSeparator) {
                    paragraph += lineSeparator
                    currentLineStartIndex = paragraph.lineStartIndex(lineSeparator)
                    currentLineLen = paragraph.length - currentLineStartIndex           //Recompute current line length: from that new line start to the end.
                    lastWhiteSpaceIndex = -1
                } else {
                    paragraph+=ch
                    currentLineLen++
                    if (ch.isBreakableWhiteSpace()) {
                        lastWhiteSpaceIndex = paragraph.lastIndex                       //save the position of the white space
                    }
                    if (overflowed(currentLineLen, maxCharsPerLine)) {                  //If this has made us go over the line limit
                        if(theresWhiteSpaceInCurrentLine(lastWhiteSpaceIndex, currentLineStartIndex) && !leavesLineTooEmpty(lastWhiteSpaceIndex, currentLineStartIndex, minFill)) {  // There's a space and cutting there wouldn't leave the line too empty, we go to new line
                            paragraph=paragraph.substituteSubStringAt(lastWhiteSpaceIndex, lineSeparator)   //substitutes the white space with the lineseparator
                        } else { // if no whitespace in the current line  or if it would leave line too empty, we hyphenate.
                            paragraph = paragraph.insertSubStringAt(lineLastIndex(currentLineStartIndex, maxCharsPerLine), "-$lineSeparator")
                        }
                        // In all cases we start a new line
                        currentLineStartIndex = paragraph.lineStartIndex(lineSeparator)
                        currentLineLen = paragraph.length - currentLineStartIndex                       //Recompute current line length: from that new line start to the end.
                        lastWhiteSpaceIndex = -1                                                        //reset white space index inside current line to negative (there's none): there can't be one because the one we just substituted was the last one in the paragraph
                    }
                }
            }
            return paragraph
        }

    companion object {
        @VisibleForTesting
        fun String.insertSubStringAt(index: Int, subString: String ) = substring(0,index) + subString + substring(index)

        @VisibleForTesting
        fun String.substituteSubStringAt(index: Int, stringToInsert: String) = substring(0,index) + stringToInsert + substring(index+1) //Replace space/tab with a new line char.

        @VisibleForTesting
        fun Char.isBreakableWhiteSpace() = ((this == ' ') || (this == '\t'))  //with tab, we might have to change stuff

        @VisibleForTesting
        fun leavesLineTooEmpty(lastWhiteSpaceIndex: Int, currentLineStartIndex: Int, minFill: Int) = lastWhiteSpaceIndex - currentLineStartIndex < minFill

        @VisibleForTesting
        fun String.lineStartIndex(lineSeparator: String) = if(lastIndexOf(lineSeparator)<0) 0 else lastIndexOf(lineSeparator) + (lineSeparator.length-1) + 1

        @VisibleForTesting
        fun theresWhiteSpaceInCurrentLine(lastWhiteSpaceIndex: Int, currentLineStartIndex: Int) = lastWhiteSpaceIndex >= currentLineStartIndex

        @VisibleForTesting
        fun lineLastIndex(currentLineStartIndex: Int, currentLineLen: Int) = currentLineStartIndex + (currentLineLen - 1)


        @VisibleForTesting
        fun overflowed(currentLineLen: Int, maxCharsPerLine: Int) = currentLineLen > maxCharsPerLine
    }

}



//class PersonalizableSmartWrap(
//    private val maxCharsPerLine: Int,
//    private val lineSeparator: String = lineSeparator(),
//    private val minFillFraction: Double = 0.5
//) : SmartWrap {
//    override operator fun invoke(input: String): String =
//        with(input){
//            require(maxCharsPerLine >= 2) { "Character limit should be at least 2" }
//            var paragraph = ""
//            var currentLineStartIndex = 0                   // index in paragraph where current line starts
//            var currentLineLen = 0                          // length of current line
//            var lastWhiteSpaceIndex = -1                    // index in paragraph of last space/tab in current line
//            val minFill = (maxCharsPerLine * minFillFraction).toDouble().roundToInt().coerceIn(0, maxCharsPerLine - 1) //I do toDouble() because when it's an Int it gives error
//
//            for ( (chindex, ch) in input.withIndex()) {
//
//                if (ch == lineSeparator.first() && input.substring(chindex, chindex + lineSeparator.length)==lineSeparator) {
//                    println("Character is first character of Line Separator")
//                    paragraph += lineSeparator
//                    currentLineStartIndex = paragraph.lineStartIndex(lineSeparator)
//                    currentLineLen = paragraph.length - currentLineStartIndex //Recompute current line length: from that new line start to the end.
//                    lastWhiteSpaceIndex = -1
//
//                } else {
//                    paragraph+=ch
//                    currentLineLen++
//                    if (ch.isBreakableWhiteSpace()) {
//                        lastWhiteSpaceIndex = paragraph.lastIndex           //save the position of the white space
//                        println("Character at index $chindex is white space. lastWhiteSpaceIndex = $lastWhiteSpaceIndex")
//                    }
//                    if (overflowed(currentLineLen, maxCharsPerLine)) { //If this has made us go over the line limit
//                        println("we overflowed")
//                        println("Paragraph: $paragraph")
//
////                        if(!theresWhiteSpaceInCurrentLine(currentLineStartIndex, currentLineLen)) paragraph = paragraph.insertSubStringAt(lineLastIndex(currentLineStartIndex, maxCharsPerLine), "-$lineSeparator")
////                        else if (leavesLineTooEmpty(lastWhiteSpaceIndex, currentLineStartIndex, minFill)) paragraph = paragraph.substituteSubStringAt(lineLastIndex(currentLineStartIndex, maxCharsPerLine), "-$lineSeparator")
////                            else paragraph=paragraph.substituteSubStringAt(lastWhiteSpaceIndex, lineSeparator)   //substitutes the white space with the lineseparator
//                        val whiteSpaceInLine = theresWhiteSpaceInCurrentLine(lastWhiteSpaceIndex, currentLineStartIndex)
//                        val tooEmpty = leavesLineTooEmpty(lastWhiteSpaceIndex, currentLineStartIndex, minFill)
//                        if(theresWhiteSpaceInCurrentLine(lastWhiteSpaceIndex, currentLineStartIndex) && !leavesLineTooEmpty(lastWhiteSpaceIndex, currentLineStartIndex, minFill)) {  // There's a space and cutting there wouldn't leave the line too empty, we go to new line
//                            println("there's a whitespace in current line AND it wouldn't leave too much empty space => we go to new line")
//                            println("currentLineStartIndex= $currentLineStartIndex, currentLineLen = $currentLineLen, lastWhiteSpaceIndex = $lastWhiteSpaceIndex, minFill = $minFill")
//                            paragraph=paragraph.substituteSubStringAt(lastWhiteSpaceIndex, lineSeparator)   //substitutes the white space with the lineseparator
//                            println("paragraph = $paragraph")
//                        } else { // if no whitespace in the current line  or if it would leave line too empty, we hyphenate.
//                            println("there's no whitespace in current line OR it would leave too much empty space => we hyphenate")
//                            println("currentLineStartIndex= $currentLineStartIndex, currentLineLen = $currentLineLen, lastWhiteSpaceIndex = $lastWhiteSpaceIndex, minFill = $minFill")
//                            println("theresWhiteSpiceInCurrentLine = $whiteSpaceInLine, tooEmpty = $tooEmpty")
//                            paragraph = paragraph.insertSubStringAt(lineLastIndex(currentLineStartIndex, maxCharsPerLine), "-$lineSeparator")
//                            println("paragraph = $paragraph")
//                        }
//
//                        // In all cases we start a new line
//                        currentLineStartIndex = paragraph.lineStartIndex(lineSeparator)
//                        currentLineLen = paragraph.length - currentLineStartIndex                       //Recompute current line length: from that new line start to the end.
//                        lastWhiteSpaceIndex = -1                                                        //reset white space index inside current line to negative (there's none): there can't be one because the one we just substituted was the last one in the paragraph
//                    }
//                }
//            }
//            return paragraph
//        }

//class PersonalizableSmartWrap(
//    private val maxCharsPerLine: Int,
//    private val lineSeparator: String = lineSeparator(),
//    private val minFillFraction: Double = 0.5
//) : SmartWrap {
//    override operator fun invoke(input: String): String =
//        with(input){
//            require(maxCharsPerLine >= 2) { "Character limit should be at least 2" }
//            var paragraph = ""
//            var currentLineStartIndex = 0          // index in paragraph where current line starts
//            var currentLineLen = 0                       // length of current line
//            var lastWhiteSpaceIndex = -1                    // index in paragraph of last space/tab in current line
//            val minFill = (maxCharsPerLine * minFillFraction).toInt().coerceAtMost(maxCharsPerLine - 1)
//
//            for (ch in input) {
//                when (ch) {
//                    lineSeparator.first() -> {
//                        paragraph.startNewLine()
//                        currentLineStartIndex = paragraph.lineStartIndex()
//                        currentLineLen = paragraph.length - currentLineStartIndex //Recompute current line length: from that new line start to the end.
//                        lastWhiteSpaceIndex = -1
//                    }
//                    else -> {
//                        paragraph+=ch
//                        currentLineLen++
//                        if (ch.isBreakableWhiteSpace()) lastWhiteSpaceIndex = paragraph.lastIndex               //save the position of the white space
//                        if (overflowed(currentLineLen)) {                                                       //If this has made us go over the line limit
//                            if(theresWhiteSpaceInCurrentLine(currentLineStartIndex, currentLineLen) && !leavesLineTooEmpty(lastWhiteSpaceIndex, currentLineStartIndex, minFill)) {  // There's a space and cutting there wouldn't leave the line too empty, we go to new line
//                                paragraph=paragraph.substituteSubStringAt(lastWhiteSpaceIndex, lineSeparator)   //substitutes the white space with the lineseparator
//                            } else { // if no whitespace in the current line  or if it would leave line too empty we hyphenate.
//                                paragraph = paragraph.insertSubStringAt(newLineLastIndex(currentLineStartIndex), "-$lineSeparator")
//                            }
//                            // In all cases we start a new line
//                            currentLineStartIndex = paragraph.lineStartIndex()
//                            currentLineLen = paragraph.length - currentLineStartIndex                       //Recompute current line length: from that new line start to the end.
//                            lastWhiteSpaceIndex = -1                                                        //reset white space index inside current line to negative (there's none): there can't be one because the one we just substituted was the last one in the paragraph
//                        }
//                    }
//                }
//            }
//            return paragraph
//
//        }
//
//    @VisibleForTesting
//    fun String.insertSubStringAt(index: Int, subString: String ) = substring(0,index) + subString + substring(index)
//
//    @VisibleForTesting
//    fun String.substituteSubStringAt(index: Int, stringToInsert: String) = substring(0,index) + stringToInsert + substring(index+1) //Replace space/tab with a new line char.
//
//    @VisibleForTesting
//    fun Char.isBreakableWhiteSpace() = ((this == ' ') || (this == '\t'))  //with tab, we might have to change stuff
//
//    @VisibleForTesting
//    fun leavesLineTooEmpty(lastWhiteSpaceIndex: Int, currentLineStartIndex: Int, minFill: Int) = lastWhiteSpaceIndex - currentLineStartIndex < minFill
//
//
//    @VisibleForTesting
//    fun String.lineStartIndex() = lastIndexOf(lineSeparator) + (lineSeparator.length-1) + 1
//
//    @VisibleForTesting
//    fun theresWhiteSpaceInCurrentLine(lastWhiteSpaceIndex: Int, currentLineStartIndex: Int) = lastWhiteSpaceIndex >= currentLineStartIndex
//
//    @VisibleForTesting
//    fun newLineLastIndex(currentLineStartIndex: Int) = currentLineStartIndex + (maxCharsPerLine - 1)
//
//
//    @VisibleForTesting
//    fun String.startNewLine(){
//        insertSubStringAt(length, lineSeparator)
//    }
//
//    @VisibleForTesting
//    fun overflowed(currentLineLen: Int) = currentLineLen > maxCharsPerLine
//
//
//
//
//}
