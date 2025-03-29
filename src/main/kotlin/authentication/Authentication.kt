package authentication

import org.koin.dsl.module

val authenticationModule =
    module {
        factory { Auth.Factory() }
        factory<Auth> { get<Auth.Factory>().create() }
    }
