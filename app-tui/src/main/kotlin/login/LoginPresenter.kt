package login

import arch.Presenter
import arch.RokyDispatchers
import authentication.AuthState
import authentication.AuthState.*
import authentication.ReadAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import login.LoginEvent.Login
import login.LoginViewState.Authenticating
import login.LoginViewState.Idle

class LoginPresenter(
    private val windowScope: CoroutineScope,
    private val login: LoginUseCase,
    private val authState: ReadAuth,
    dispatchers: RokyDispatchers,
) : Presenter<LoginView>(dispatchers) {
    private val state: MutableStateFlow<LoginViewState> = MutableStateFlow(Idle())

    override fun onAttach(view: LoginView) {
        windowScope.launch(dispatchers.main) {
            state.collect(::show)
        }
        windowScope.launch(dispatchers.io) {
            authState.state().collect {
                state.value = it.toLoginViewState()
            }
        }
    }

    private fun AuthState.toLoginViewState(): LoginViewState =
        when (this) {
            Authenticating -> Idle(status = AUTHENTICATING)
            InitState -> Idle()
            InvalidCredentials -> Idle(status = LOGIN_FAILURE)
            is SignIn -> Idle(status = LOGIN_SUCCESS)
        }

    override fun onDetach(view: LoginView) {
        TODO("Not yet implemented")
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is Login -> onLogin(event)
        }
    }

    private fun onLogin(event: Login) =
        with(event) {
            windowScope.launch(dispatchers.io) {
                state.value = Authenticating(username, password, AUTHENTICATING)
                with(login(event)) {
                    if (!isSuccessful) {
                        state.value = Idle(status = message.orEmpty())
                    }
                }
            }
        }

    private fun show(state: LoginViewState) = withView { it.show(state) }

    companion object {
        const val AUTHENTICATING = "Authenticating…"
        const val LOGIN_SUCCESS = "Username and password is OK."
        const val LOGIN_FAILURE = "Could not authenticate."
    }
}
