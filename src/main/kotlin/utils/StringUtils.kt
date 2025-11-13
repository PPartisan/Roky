package utils

import java.lang.System.lineSeparator

fun String.cutOff(maxCharsPerLine: Int): String {
    require(maxCharsPerLine > 0) { "Character limit should be at least 1" }
    return if (length <= maxCharsPerLine) this else "${take(maxCharsPerLine - 1)}…"
}

fun String.smartWrap(maxCharsPerLine: Int): String {
    if (length<=maxCharsPerLine) {
        return this
    }
    val words = split("\\s".toRegex())
    var paragraph=""
    words.forEach{
        if (paragraph.wouldOverFlow(maxCharsPerLine, it)) {
            if(paragraph.isNotEmpty()) paragraph+= lineSeparator()
            if(it.canFitOnALine(maxCharsPerLine)) {
                 paragraph+= it
            }else{
                paragraph+= it.chunked(maxCharsPerLine-1).joinToString(lineSeparator()) { "$it-" }.trimEnd('-')
            }
        }else{
            paragraph+= it
        }
        paragraph += if(paragraph.isLineFull(maxCharsPerLine))  lineSeparator() else " "
    }
    return paragraph.trimEnd()
}

fun String.isSingleLineParagraph() = !contains(lineSeparator())

fun String.canFitOnALine(maxCharsPerLine: Int) = length<=maxCharsPerLine

fun String.wouldOverFlow(maxCharsPerLine: Int, word: String) = charactersUsedInLastLine() + word.length > maxCharsPerLine //we're using charactersUsedInLastLine on the word not on the paragraph

fun String.charactersUsedInLastLine(): Int = if(isSingleLineParagraph())
    length else lastIndex-(lastIndexOf(lineSeparator())+(lineSeparator().length-1))

fun String.charactersRemainingInLastLine(maxCharsPerLine: Int) = maxCharsPerLine - charactersUsedInLastLine()

fun String.isLineFull(maxCharsPerLine: Int) = charactersRemainingInLastLine(maxCharsPerLine)<=0










// _________________________________________________________________________________________________________________________________________

fun String.smartWrapNew(maxCharsPerLine: Int): String {
    if (length<=maxCharsPerLine) {
        return this
    }
    val words = split("\\s".toRegex())
    var paragraph=""
    words.forEach{
        val charactersRemainingInLine = paragraph.charactersRemainingInLastLine2(maxCharsPerLine)
        if (it.wouldOverFlow2(maxCharsPerLine, paragraph)) {
            val firstSliceOfWord = it.slice(0..<charactersRemainingInLine-1)
            val secondSliceOfWord = it.slice((charactersRemainingInLine-1).coerceAtLeast(0) ..<it.length)  //added coerce because when paragraph ends exactly on last available char for the current line, remainingChar is 0, but we still need to get the second slice
            paragraph += if(firstSliceOfWord.isNotEmpty()) "$firstSliceOfWord-\n" else " "  //added this condition for when the paragraph ends right at maxChar
            paragraph += secondSliceOfWord.chunked(maxCharsPerLine-1).joinToString("\n") { "$it-" }.trimEnd('-')
        } else {
            paragraph += if (paragraph=="" || paragraph.endsWith("\n") || paragraph.endsWith(' ')) it
            else " $it"
        }
        paragraph += if (paragraph.charactersRemainingInLastLine2(maxCharsPerLine)<=2) "\n" //first condition: if we are just at the end of the line (paragraph's length is a multiple of maxCharPerLine) or if we have only 2 or fewer characters remaining, we shouldn't add any space. 2 characters remaining would mean adding a space and then, unless the next word is a single character, also a hyphen. first condition is not needed in theory, but if
        else " "
    }
    return paragraph.trimEnd(' ', '\n', '\r')
}

private fun String.isFirstLine2() = !contains(lineSeparator())  //I think we're mixing to what string we're applying it to. the word should never contain \n but checking for the length<=maxChar makes sense only for the word

private fun String.canFitOnALine2(maxCharsPerLine: Int) = length<=maxCharsPerLine

private fun String.wouldOverFlow2(maxCharsPerLine: Int, paragraph: String) = paragraph.charactersUsedInLastLine2() + length > maxCharsPerLine

fun String.charactersUsedInLastLine2() =  (length-(lastIndexOf("\n")+1))  //lastIndexOf returns -1 if it can't find it

private fun String.charactersRemainingInLastLine2(maxCharsPerLine: Int) = maxCharsPerLine - charactersUsedInLastLine2()

// _________________________________________________________________________________________________________________________________

fun String.insertSubStringAt(index: Int, subString: String ) = substring(0,index) + subString + substring(index)

fun String.substituteCharAt(index: Int, stringToInsert: String) = substring(0,index) + stringToInsert + substring(index+1) //Replace space/tab with a new line char.

fun String.smartWrapMartine(maxCharsPerLine: Int, minFillFraction: Double = 0.4): String {
    require(maxCharsPerLine >= 2) { "Character limit should be at least 2" }

    var paragraph = ""
    var currentLineStartIndex = 0          // index in paragraph where current line starts
    var currentLineLen = 0                       // length of current line
    var lastWhiteSpaceIndex = -1                    // index in paragraph of last space/tab in current line
    val minFill = (maxCharsPerLine * minFillFraction).toInt().coerceAtMost(maxCharsPerLine - 1)

    fun isBreakableWhiteSpace(ch: Char) = ((ch == ' ') || (ch == '\t'))  //with tab, we might have to change stuff
    fun leavesLineTooEmpty() = lastWhiteSpaceIndex - currentLineStartIndex < minFill
    fun theresWhiteSpaceInCurrentLine() = lastWhiteSpaceIndex >= currentLineStartIndex
    fun recomputeValsForNewLine() {
        currentLineStartIndex = paragraph.lastIndexOf(lineSeparator()) + (lineSeparator().length-1) + 1
        currentLineLen = paragraph.length - currentLineStartIndex  //Recompute current line length: from that new line start to the end.
        lastWhiteSpaceIndex = -1 //reset white space index inside current line to negative (there's none): there can't be one because the one we just substituted was the last one in the paragraph
    }
    fun currentLineLastIndex() = currentLineStartIndex + (maxCharsPerLine - 1)
    fun cutWordAndHyphenate() {
        paragraph = paragraph.insertSubStringAt(currentLineLastIndex(), "-${lineSeparator()}")
        recomputeValsForNewLine()
    }
    fun startNewLine(){
        paragraph+=lineSeparator()
        recomputeValsForNewLine()
    }
    fun overflowed() = currentLineLen > maxCharsPerLine

    forEach { ch ->
        when (ch) {
            '\n' -> startNewLine()
            else -> {
                paragraph+=ch
                currentLineLen++
                if (isBreakableWhiteSpace(ch)) lastWhiteSpaceIndex = paragraph.lastIndex  //save the position of the white space
                if (overflowed()) {        //If this has made us go over the line limit
                    if (theresWhiteSpaceInCurrentLine()) {    //If we have a space/tab to break at inside the current line
                        if (!leavesLineTooEmpty()) {          //If it wouldn't leave the line too empty, we can go to new line
                            paragraph=paragraph.substituteCharAt(lastWhiteSpaceIndex, lineSeparator())  //substitutes the white space with the \n char
                            recomputeValsForNewLine()
                        } else cutWordAndHyphenate() // if it would leave line too empty we need to hyphenate
                    } else cutWordAndHyphenate() // if no whitespace in the current line => we just cut.
                }
            }
        }
    }
    return paragraph
}
