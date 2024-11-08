package login

import arch.Presenter
import arch.RokyDispatchers
import arch.WindowScope
import arch.WindowScopeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import login.LoginViewState.Idle

class LoginPresenter(
    private val windowScope: CoroutineScope,
    dispatchers: RokyDispatchers
) : Presenter<LoginView>(dispatchers) {
    private val state: MutableStateFlow<LoginViewState> = MutableStateFlow(Idle())
    override fun onAttach(view: LoginView) {
        windowScope.launch(dispatchers.main) {
            state.collect(::show)
        }
    }

    override fun onDetach(view: LoginView) {
        TODO("Not yet implemented")
    }
    fun onEvent(event: LoginEvent) {
        println(event)
    }

    private fun show(state: LoginViewState) = withView {it.show(state)}
}
