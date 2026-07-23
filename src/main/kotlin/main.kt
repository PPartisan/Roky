import arch.rokyDispatchersModule
import authentication.authenticationModule
import chatroom.chatroomModules
import chatserver.chatServerModule
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import help.helpModule
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
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
            chatServerModule,
        )
        single { DefaultTerminalFactory().createScreen() } bind Screen::class
        single { MultiWindowTextGUI(get()).also { it.theme = rokyTheme } }
        factory<HttpClient> { HttpClient(CIO) { default() } }
        factoryOf(::NavigationController) binds arrayOf(NavigateToAppWindow::class, NavigateToMainMenu::class)
        singleOf(::StartApp)
        singleOf(::StopApp)
        single<SupabaseClient> {
            createSupabaseClient(Secrets.SERVER_URL, Secrets.CLIENT_KEY) {
                defaultLogLevel = io.github.jan.supabase.logging.LogLevel.NONE
                install(Auth)
                install(Postgrest)
                install(Realtime)
            }
        }
    }

fun HttpClientConfig<*>.default() {
    install(Logging) {
        logger = Logger.EMPTY
        level = LogLevel.ALL
    }
}
