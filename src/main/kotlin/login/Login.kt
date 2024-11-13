package login

import org.koin.dsl.module

val loginModules = module {
    scope<LoginWindow>{
        scoped {
            LoginPresenter(windowScope = get<LoginWindow>().windowScope, dispatchers = get(), login = LoginUseCase())
        }
    }
}
