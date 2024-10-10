package navigation

import com.googlecode.lanterna.gui2.AbstractTextGUI
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.Window

class NavigationController (

    private val gui: MultiWindowTextGUI

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
        println("Main Menu")
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