package mainmenu

import StopApp
import arch.Presenter
import arch.RokyDispatchers.Gui
import arch.WindowScope
import arch.WindowScopeProvider
import authentication.Authenticator
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mainmenu.MainMenuEvent.SelectLogin
import mainmenu.MainMenuViewState.Companion.loggedIn
import mainmenu.MainMenuViewState.Companion.loggedOut
import navigation.NavigateToAppWindow

class MainMenuPresenter(
    private val quit : StopApp,
    private val navigate : NavigateToAppWindow,
    private val authenticator: Authenticator
) : Presenter<MainMenuView>(), WindowScope by WindowScopeProvider() {

    override fun onAttach(view: MainMenuView) {
        view.show(Loading)
        windowScope.launch(IO) {
            val isLoggedIn = authenticator.isLoggedIn()
            val viewState = if(isLoggedIn) loggedIn else loggedOut
            withContext(Gui) {
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
            MainMenuEvent.SelectAbout -> navigate.toAbout()
            MainMenuEvent.SelectHelp -> navigate.toHelp()
            MainMenuEvent.SelectJoinChatroom -> navigate.toChatRoom()
            MainMenuEvent.SelectProfile -> navigate.toProfile()
            MainMenuEvent.SelectQuit -> quit()
        }

    }

}