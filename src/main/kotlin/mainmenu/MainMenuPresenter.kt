package mainmenu

import StopApp
import arch.Presenter
import arch.RokyDispatchers
import arch.WindowScope
import arch.WindowScopeProvider
import authentication.Authenticator
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mainmenu.MainMenuEvent.*
import mainmenu.MainMenuViewState.Companion.loggedIn
import mainmenu.MainMenuViewState.Companion.loggedOut
import navigation.NavigateToAppWindow

class MainMenuPresenter(
    private val quit : StopApp,
    private val navigate : NavigateToAppWindow,
    private val authenticator: Authenticator,
    dispatchers: RokyDispatchers
) : Presenter<MainMenuView>(dispatchers), WindowScope by WindowScopeProvider() {

    override fun onAttach(view: MainMenuView) {
        view.show(Loading)
        windowScope.launch(dispatchers.io) {
            val isLoggedIn = authenticator.isLoggedIn()
            val viewState = if(isLoggedIn) loggedIn else loggedOut
            withContext(dispatchers.main) {
                withView { view -> view.show(viewState) }
            }
        }
    }

    override fun onDetach(view: MainMenuView) {
        TODO("Not yet implemented")
    }

    fun onEvent(event: MainMenuEvent) {
        when (event) {
            SelectLogin -> navigate.toLogin()
            SelectAbout -> navigate.toAbout()
            SelectHelp -> navigate.toHelp()
            SelectJoinChatroom -> navigate.toChatRoom()
            SelectProfile -> navigate.toProfile()
            SelectQuit -> quit()
        }

    }

}