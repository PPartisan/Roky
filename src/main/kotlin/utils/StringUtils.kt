package utils

fun String.cutOff(maxCharsPerLine: Int): String {
    require(maxCharsPerLine > 0) { "Character limit should be at least 1" }
    return if (length <= maxCharsPerLine) this else "${take(maxCharsPerLine - 1)}…"
}

fun String.insertSubStringAt(index: Int, subString: String ) = substring(0,index) + subString + substring(index)

fun String.substituteCharAt(index: Int, stringToInsert: String) = substring(0,index) + stringToInsert + substring(index+1) //Replace space/tab with a new line char.
