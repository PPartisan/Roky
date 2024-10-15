import authentication.Authenticator
import org.koin.dsl.module

val authenticationModule = module {
    single{Authenticator()}
}