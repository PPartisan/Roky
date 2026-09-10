package mainmenu

import org.koin.dsl.module

val mainMenuModules =
    module {
        scope<MainMenuWindow> {
            scoped {
                MainMenuPresenter(
                    quit = get(),
                    navigate = get(),
                    authenticator = get(),
                    windowScope = get<MainMenuWindow>(),
                    dispatchers = get(),
                )
            }
        }
    }
