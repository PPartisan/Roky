package app

import com.googlecode.lanterna.screen.Screen
import navigation.NavigateToMainMenu

/**
 * Terminal UI Application Bootstrap Command Loop
 *
 * JUSTIFICATION FOR THE TEAM:
 * Enforces a command pattern wrapper for terminal session lifecycle control. Decouples the
 * initialization of primitive visual screen frames away from the main composition root function.
 */
class StartApp(
    private val screen: Screen,
    private val navigateToMainMenu: NavigateToMainMenu
) : Runnable {
    override fun run() {
        screen.startScreen()

        // RESOLUTION: Explicitly invokes the matching contract port method on our navigation interface
        navigateToMainMenu.toMainMenu()
    }
}
