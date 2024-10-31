package mainmenu

import StopApp
import arch.Presenter
import arch.RokyDispatchers
import arch.WindowScope
import arch.WindowScopeProvider
import authentication.Authenticator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mainmenu.MainMenuEvent.*
import mainmenu.MainMenuViewState.Companion.loggedIn
import mainmenu.MainMenuViewState.Companion.loggedOut
import navigation.NavigateToAppWindow

class MainMenuPresenter(
    private val quit : StopApp,
    private val navigate : NavigateToAppWindow,
    private val authenticator: Authenticator,
    private val windowScope: CoroutineScope,
    dispatchers: RokyDispatchers
) : Presenter<MainMenuView>(dispatchers) {

    private val state : MutableStateFlow<MainMenuViewState> = MutableStateFlow(Loading)
    override fun onAttach(view: MainMenuView) {
        state.value = Loading
        windowScope.launch(dispatchers.io){
            state.value = if (authenticator.isLoggedIn()) loggedIn else loggedOut
        }
        windowScope.launch(dispatchers.main) {
            state.collect(::show)
        }
    }

    override fun onDetach(view: MainMenuView) {
        // empty
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

    private fun show(state: MainMenuViewState){
        withView {view -> view.show(state)}
    }
}
