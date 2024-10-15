package navigation

import StopApp
import authentication.Authenticator
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.Window
import com.sun.tools.javac.Main
import mainmenu.MainMenuPresenter
import mainmenu.MainMenuWindow

class NavigationController (
    private val gui: MultiWindowTextGUI,
    private val quit : StopApp,
    private val authenticator: Authenticator
) : NavigateToAppWindow, NavigateToMainMenu {

    override fun toLogin() {
        println("Login")
    }

    override fun toChatRoom() {
        println("Chatroom")
    }

    override fun toProfile() {
        println("Profile")
    }

    override fun toHelp() {
        println("Help")
    }

    override fun toAbout() {
        println("About")
    }

    override fun toMainMenu() {
        setActiveWindow(MainMenuWindow(presenter = MainMenuPresenter(quit, navigate = this, authenticator)))
    }

    private fun setActiveWindow(window:Window) {
        val activeWindow = gui.activeWindow
        if (activeWindow!=null) {
            gui.removeWindow(activeWindow)
            activeWindow.close()
        }
        gui.addWindowAndWait(window)
    }

}
