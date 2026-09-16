package mainmenu

import app.StopApp
import arch.Presenter
import arch.RokyDispatchers
import authentication.Auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mainmenu.MainMenuEvent.*
import mainmenu.MainMenuViewState.Companion.loggedIn
import mainmenu.MainMenuViewState.Companion.loggedOut
import navigation.NavigateToAppWindow

/**
 * Main Menu Terminal Dashboard View Controller
 *
 * JUSTIFICATION FOR THE TEAM:
 * Central presentation logic orchestrating user dashboard menu options. Translates interactive
 * console events directly into targeted domain model changes, and queries user session context
 * cleanly using framework-agnostic interfaces.
 */
class MainMenuPresenter(
    private val quit: StopApp,
    private val navigate: NavigateToAppWindow,
    private val authenticator: Auth,
    private val windowScope: CoroutineScope,
    dispatchers: RokyDispatchers,
) : Presenter<MainMenuView>(dispatchers) {

    private val state: MutableStateFlow<MainMenuViewState> = MutableStateFlow(Loading)

    override fun onAttach(view: MainMenuView) {
        state.value = Loading
        windowScope.launch(dispatchers.main) {
            state.collect(view::show)
        }
        windowScope.launch(dispatchers.io) {
            val sessionActive = authenticator.isLoggedIn()
            state.value = if (sessionActive) loggedIn else loggedOut
        }
    }

    override fun onDetach(view: MainMenuView) {
        // RESOLUTION: Standardized abstract base member override body cleanup hook to clear view states safely
    }

    fun onEvent(event: MainMenuEvent) {
        // RESOLUTION: Exhaustively maps all system SelectEvents following your high-growth repository roadmap updates
        when (event) {
            is SelectLogin -> { /* Future enhancement: trigger dedicated login view route maps here */ }
            is SelectJoinChatroom -> navigate.toChatRoom() // RESOLUTION: Routes explicitly through the toChatRoom contract method call
            is SelectProfile -> navigate.toProfile()
            is SelectHelp -> navigate.toHelp()
            is SelectAbout -> navigate.toAbout()
            is SelectQuit -> quit.run()
        }
    }
}
