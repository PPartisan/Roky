import arch.rokyDispatchersModule
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import mainmenu.mainMenuModules
import navigation.NavigateToAppWindow
import navigation.NavigateToMainMenu
import navigation.NavigationController
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import view.rokyTheme

fun main() {
    startKoin{
        modules(mainModules)
    }
    val start = get<StartApp>(StartApp::class.java)
    start()
}

val mainModules = module {
    includes(mainMenuModules, authenticationModule, rokyDispatchersModule)
    single {
        DefaultTerminalFactory().createScreen()
    } bind Screen::class
    single { MultiWindowTextGUI(get()).also { it.theme = rokyTheme } }
    factory { NavigationController(get()) } binds arrayOf(NavigateToAppWindow::class, NavigateToMainMenu::class)
    single {StartApp(get(), get())}
    single {StopApp(get(), get())}
}
