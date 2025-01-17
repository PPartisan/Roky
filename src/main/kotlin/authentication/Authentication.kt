package authentication

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authenticationModule =
    module {
        singleOf(::Authenticator)
    }
