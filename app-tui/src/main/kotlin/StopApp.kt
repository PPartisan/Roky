package app

import com.googlecode.lanterna.screen.Screen

class StopApp(
    private val screen: Screen
) : Runnable {
    override fun run() {
        screen.stopScreen()
    }
}
