package utils

class LoggingSmartWrap : SmartWrap {
    override fun invoke(input: String): String {
        println(input)
        return input
    }
}
