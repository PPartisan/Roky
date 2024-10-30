import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.screen.Screen

class StopApp (
    private val screen: Screen,
    private val multiWindowTextGUI: MultiWindowTextGUI
) {
    operator fun invoke (){
        multiWindowTextGUI.removeWindow(multiWindowTextGUI.activeWindow)
        screen.stopScreen()
    }
}
