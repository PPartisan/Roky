package navigation

import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.Window
import help.HelpWindow
import login.LoginWindow
import mainmenu.MainMenuWindow
import profile.ProfileWindow

class NavigationController (
    private val gui: MultiWindowTextGUI
) : NavigateToAppWindow, NavigateToMainMenu {

    override fun toLogin() {
        setActiveWindow(LoginWindow(this))
    }

    override fun toChatRoom() {
        println("Chatroom")
    }

    override fun toProfile() {
       setActiveWindow(ProfileWindow(this))
    }

    override fun toHelp() {
        setActiveWindow(HelpWindow(this))
    }

    override fun toAbout() {
        println("About")
    }

    override fun toMainMenu() {
        setActiveWindow(MainMenuWindow())
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
