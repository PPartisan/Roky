package profile

import org.koin.dsl.module

val profileModules =
    module {
        scope<ProfileWindow> {
            scoped {
                ProfilePresenter(
                    windowScope = get<ProfileWindow>(),
                    repositories = get(),
                    dispatchers = get(),
                )
            }
        }
    }
