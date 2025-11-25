package utils

fun interface SmartWrap {
    operator fun invoke(input: String): String
}
