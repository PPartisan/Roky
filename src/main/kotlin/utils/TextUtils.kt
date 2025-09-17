package utils

//splits a string into a list of strings each a word. It combines words back together into lines of maximum maxCharPerLine
//characters and collects these lines into a new list of strings (each element of this new list is a line).
//finally rejoins the list to a single string interjecting /n between lines.
//I did it this way because smartWrap when used is expected to return a String and I didn't want to touch or affect too much code
//and I felt it was probably more intuitive. For example in ViewMessagePresenter we create a wrapped text from a string. Since to display the new text
//we need to call view.show(Messages(wrapped)) and Messages wants a string, I thought it better to have the joinToString here instead of in that line of code

//fun String.smartWrap(maxCharsPerLine: Int = 30): String { //BUGGIE
//    if (this.length < maxCharsPerLine - 4) return this
//
//    val words = this.split(Regex("[ \\n]+")) // split on spaces and newlines
//    val lines = mutableListOf<String>()
//    var currentLine = ""
//    var leftover = ""
//
//    for (word in words) {
//        var tentativeLine = ""
//        println("leftover: "+leftover)
//        if (leftover.isNotEmpty()) {
//            tentativeLine = "$leftover $word" //if leftover is empty we don't need to worry about current line also being not empty because we always assign currentLine to "" when we put leftover
//            println("leftover Not Empty. TentativeLine: "+tentativeLine)
//        }
//        else {
//            if (currentLine.isEmpty()) {
//                tentativeLine = word
//                println("currentLine is Empty. tentativeLine: "+tentativeLine)
//            }
//            else {
//                tentativeLine = "$currentLine $word"
//                println("currentLine is Not Empty. TentativeLine: "+tentativeLine)
//            }
//        }
//
//        if (tentativeLine.length <= maxCharsPerLine - 4) { //I need this 4 because when the vertical scrollbar appears if I don't limit the word length to a bit less than the real space, then the scrollbar covers it and forces the horizontal scrollbar to appear
//            // Safe to add the word to the current line
//            currentLine = tentativeLine
//        } else {
//            println("39: currentLine= "+currentLine +". maxCharsPerLine= "+maxCharsPerLine+". word= "+word)
//            // Adding the word would make the line too long
//            // Check if current line (before adding new word) is long enough (I arbitrarily decided half of max width)
//            if (currentLine.length >= maxCharsPerLine / 2) { //then skips adding the word to the line
//                println("we don't add the new word")
//                lines.add(currentLine)            //add the current line without the word into the new list
//                leftover = word              //start new line with the word
//                currentLine = ""
//            } else { //if the line without the word would be too small
//                // Break the word
//                println("I have to split the word")
//                val spaceLeft = maxCharsPerLine - 4 - currentLine.length
//                if (spaceLeft > 0) { //&& word.length > spaceLeft
//                    println("there's space left")
//                    val part = tentativeLine.substring(0, maxCharsPerLine-4)
//                    currentLine = if (currentLine.isEmpty()) "$part-" else " $part-" //adds space to beginning of word only if it's not the first word in the line
//                    lines.add(currentLine)
//                    currentLine = ""
//                    leftover = tentativeLine.substring(maxCharsPerLine-4) //we save the leftover (we don't put it directly in the new line in case the leftover is still bige
//                } else {
//                    println("else line 59")
//                    lines.add(currentLine)
//                    leftover = word
//                    currentLine = ""
//                }
//
//
//                // Add last line if non-empty
//                if (currentLine.isNotEmpty()) {
//                    lines.add(currentLine)
//                }
//
//            }
//        }
//    }
//    return lines.joinToString("\n")
//}

//line 24=true: Roky is great a|nd I'm learning a lot
//line 24=false: Hey Suprfragilistica...whatever

//Problem: if the window size is adjusted, the width changes with it, so new messages will wrap to this new
// size, but the old messages still appear as before. If window size is reduced: old messages might make the
// scrollbar appear anyway. If size is increased, old messages look weird 'cause they are wrapped too early


fun String.cutOff(maxCharsPerLine: Int): String {
    return if (this.length <= maxCharsPerLine) this else this.take(maxCharsPerLine - 2) + "…"
}



fun String.smartWrap(maxCharsPerLine: Int = 30): String {
    if (this.length < maxCharsPerLine - 4) return this

    val words = this.split(Regex("[ \\n]+")) // split on spaces and newlines
    val lines = mutableListOf<String>()
    var currentLine = ""

    for (word in words) {
        val tentativeLine = if (currentLine.isEmpty()) word else "$currentLine $word"
        if (tentativeLine.length <= maxCharsPerLine - 4) { //I need this 4 because when the vertical scrollbar appears if I don't limit the word length to a bit less than the real space, then the scrollbar covers it and forces the horizontal scrollbar to appear
            // Safe to add the word to the current line
            currentLine = tentativeLine
        } else {
            // Adding the word would make the line too long
            // Check if current line (before adding new word) is long enough (I arbitrarily decided half of max width)
            if (currentLine.length >= maxCharsPerLine / 2) { //then skips adding the word to the line
                lines.add(currentLine)            //add the current line without the word into the new list
                currentLine = word                //start new line with the word   !!! WORD MIGHT BE TOO LONG BY ITSELF, AND THIS IS NEVER CHECKED
            } else { //if the line without the word would be too small
                // Break the word
                val spaceLeft = maxCharsPerLine - 4 - currentLine.length
                if (spaceLeft > 0) { //&& word.length > spaceLeft
                    val part = word.substring(0, spaceLeft)
                    currentLine += if (currentLine.isEmpty()) "$part-" else " $part-" //adds space to beginning of word only if it's not the first word in the line
                    lines.add(currentLine)
                    currentLine = word.substring(spaceLeft) //we start a new line with the leftover
                } else {
                    lines.add(currentLine)
                    currentLine = word          //HERE's ONE OF THE PROBLEMS. IF WORD IS TOO LONG IT DOESN?T GET CHECKED AGAIN AND IT'S JUST ADDED
                }
            }
        }
    }
    return lines.joinToString("\n")
}

//fun cutLineAndSaveLeftover(line: String, word: String, maxCharsPerLine: Int): String{
//    val newline = (line + " " + word).substring(0, maxCharsPerLine-4)
//    val leftover = (line + " " + word).substring(maxCharsPerLine-4)
//    return newline
//}
