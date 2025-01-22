import arch.rokyDispatchersModule
import authentication.authenticationModule
import chatroom.chatroomModules
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import help.helpModule
import login.loginModules
import mainmenu.mainMenuModules
import navigation.NavigateToAppWindow
import navigation.NavigateToMainMenu
import navigation.NavigationController
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import profile.profileModules
import view.rokyTheme

fun main() {
    startKoin {
        modules(mainModules)
    }
    val start = get<StartApp>(StartApp::class.java)
    start()
}

val mainModules =
    module {
        includes(
            mainMenuModules,
            authenticationModule,
            rokyDispatchersModule,
            loginModules,
            helpModule,
            profileModules,
            chatroomModules,
        )
        single {
            DefaultTerminalFactory().createScreen()
        } bind Screen::class
        single { MultiWindowTextGUI(get()).also { it.theme = rokyTheme } }
        factoryOf(::NavigationController) binds arrayOf(NavigateToAppWindow::class, NavigateToMainMenu::class)
        singleOf(::StartApp)
        singleOf(::StopApp)
    }
