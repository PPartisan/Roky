package mainmenu

import mainmenu.MainMenuEvent.*

data class MainMenuViewState(val rows: List<Row>) {
    data class Row(val title: String, val event: MainMenuEvent)
    companion object {
        private val login = Row("Login", SelectLogin)
        private val joinChatRoom = Row("Join Chat Room", SelectJoinChatroom)
        private val profile = Row("Profile", SelectProfile)
        private val help = Row("Help", SelectHelp)
        private val about = Row("About", SelectAbout)
        private val quit = Row("Quit", SelectQuit)
        val loggedOut = listOf(login, help, about, quit).let(::MainMenuViewState)
        val loggedIn = listOf(login, joinChatRoom, profile, help, about, quit).let(::MainMenuViewState)
    }
}