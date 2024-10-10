import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import navigation.NavigateToAppWindow
import navigation.NavigateToMainMenu
import navigation.NavigationController
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

fun main() {
startKoin{
    modules(mainModules)
}
}

val mainModules = module {
    single {
        DefaultTerminalFactory().createScreen()
    } bind Screen::class
   single { MultiWindowTextGUI(get()) }
    factory { NavigationController(get()) } binds arrayOf(NavigateToAppWindow::class, NavigateToMainMenu::class)

}