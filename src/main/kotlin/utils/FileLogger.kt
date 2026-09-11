package utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FileLogger {
    private val logFile = File("debug.log")

    init {
        logFile.writeText("--- App Started at ${LocalDateTime.now()} ---\n")
    }

    @Synchronized
    fun log(message: String) {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
        logFile.appendText("[$time] $message\n")
    }
}
