package mainmenu

import StopApp
import arch.Presenter
import mainmenu.MainMenuEvent.SelectLogin
import navigation.NavigateToAppWindow

class MainMenuPresenter(private val quit : StopApp, private val navigate : NavigateToAppWindow) :Presenter<MainMenuView> () {
    override fun onAttach(view: MainMenuView) {
        TODO("Not yet implemented")
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