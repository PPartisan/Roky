package chatroom

import app.StartApp
import app.StopApp
import Secrets
import authentication.authenticationModule
import chatroom.arch.lanternaDispatchersModule
import chatroom.chatroomModules
import chatserver.messages.chatServerMessagesModule
import chatserver.supabase.infrastructureAuthModule
import chatserver.supabase.infrastructureSupabaseModule
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
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import profile.profileModules
import view.rokyTheme

fun main() {
    startKoin {
        properties(
            mapOf(
                "SUPABASE_URL" to Secrets.SERVER_URL,
                "SUPABASE_KEY" to Secrets.CLIENT_KEY,
                "USE_LOCAL_MOCKS" to Secrets.USE_LOCAL_MOCKS.toString()
            )
        )
        modules(mainModules, infrastructureAuthModule, infrastructureSupabaseModule)
    }
    val start = get<StartApp>(StartApp::class.java)
    start.run() // RESOLUTION: StartApp implements Runnable, executed via run()
}

val mainModules = module {
    includes(
        mainMenuModules,
        authenticationModule,
        lanternaDispatchersModule,
        loginModules,
        helpModule,
        profileModules,
        chatroomModules,
        chatServerMessagesModule
    )

    single { DefaultTerminalFactory().createScreen() } binds arrayOf(Screen::class)
    single { MultiWindowTextGUI(get()).also { it.theme = rokyTheme } }

    factoryOf(::NavigationController) binds arrayOf(NavigateToAppWindow::class, NavigateToMainMenu::class)

    singleOf(::StartApp)
    singleOf(::StopApp)
}
