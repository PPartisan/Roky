package utils

fun String.cutOff(maxCharsPerLine: Int): String {
    require(maxCharsPerLine > 0) { "Character limit should be at least 1" }
    return if (length <= maxCharsPerLine) this else "${take(maxCharsPerLine - 1)}…"
}
