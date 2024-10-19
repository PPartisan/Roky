package mainmenu

import org.koin.dsl.module

val mainMenuModules = module {
    scope<MainMenuWindow> {
        scoped { MainMenuPresenter(get(), get(), get(), get()) }
    }
}