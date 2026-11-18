package profile

import chatserver.ChatRepositories
import chatserver.LoggedInUserId
import org.koin.dsl.module

val profileModules =
    module {
        scope<ProfileWindow> {
            scoped {
                ProfilePresenter(
                    windowScope = get<ProfileWindow>().windowScope,
                    requestUsername = get(),
                    repositories = get(),
                    dispatchers = get(),
                )
            }
            scoped { ValidateUsernameRequest(
                write = get<ChatRepositories>().writeProfiles(),
                read = get<ChatRepositories>().readProfiles(),
                userId = get()
            ) }
        }
    }
