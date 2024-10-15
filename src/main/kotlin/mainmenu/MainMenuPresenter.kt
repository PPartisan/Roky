package mainmenu

import StopApp
import arch.Presenter
import authentication.Authenticator
import mainmenu.MainMenuEvent.SelectLogin
import mainmenu.MainMenuViewState.Companion.loggedIn
import mainmenu.MainMenuViewState.Companion.loggedOut
import navigation.NavigateToAppWindow

class MainMenuPresenter(
    private val quit : StopApp,
    private val navigate : NavigateToAppWindow,
    private val authenticator: Authenticator) :Presenter<MainMenuView> () {

    override fun onAttach(view: MainMenuView) {
        if(authenticator.isLoggedIn()){
            view.show(loggedIn)
        }
        else{
            view.show(loggedOut)
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