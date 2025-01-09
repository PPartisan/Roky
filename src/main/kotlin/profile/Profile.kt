package profile

import org.koin.dsl.module


val profileModules = module {
    scope<ProfileWindow>{
        scoped {
            ProfilePresenter(windowScope = get<ProfileWindow>().windowScope, dispatchers = get())
        }
        scoped { RequestUserNameUseCase(get()) }
    }
    factory { RequestUserNameUseCase.RequestUserName() }
}
