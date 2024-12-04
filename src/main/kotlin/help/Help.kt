package help

import org.koin.dsl.module


val helpModule = module {
    scope<HelpWindow> {
        scoped { HelpPresenter(dispatchers = get(), windowScope = get<HelpWindow>().windowScope) }
    }
}