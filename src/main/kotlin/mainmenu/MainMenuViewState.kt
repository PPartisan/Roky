package mainmenu
import mainmenu.Loaded.Row
import mainmenu.MainMenuEvent.*

interface MainMenuViewState {

    companion object {
        private val login = Row("Login", SelectLogin)
        private val joinChatRoom = Row("Join Chat Room", SelectJoinChatroom)
        private val profile = Row("Profile", SelectProfile)
        private val help = Row("Help", SelectHelp)
        private val about = Row("About", SelectAbout)
        private val quit = Row("Quit", SelectQuit)
        val loggedOut = listOf(login, help, about, quit).let(::Loaded)
        val loggedIn = listOf(login, joinChatRoom, profile, help, about, quit).let(::Loaded)
    }

}

data class Loaded(val rows: List<Row>) : MainMenuViewState {
    data class Row(val title: String, val event: MainMenuEvent)
}

data object Loading : MainMenuViewState

