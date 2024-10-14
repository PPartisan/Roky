package mainmenu

import com.sun.tools.javac.Main

sealed interface MainMenuEvent {
    data object SelectLogin : MainMenuEvent
    data object SelectJoinChatroom : MainMenuEvent
    data object SelectProfile : MainMenuEvent
    data object SelectHelp : MainMenuEvent
    data object SelectAbout : MainMenuEvent
    data object SelectQuit : MainMenuEvent
}