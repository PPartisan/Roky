package login

import arch.Presenter
import arch.RokyDispatchers
import arch.WindowScope
import arch.WindowScopeProvider

class LoginPresenter(
    dispatchers: RokyDispatchers
) : Presenter<LoginView>(dispatchers), WindowScope by WindowScopeProvider() {
    override fun onAttach(view: LoginView) {
        // empty
    }

    override fun onDetach(view: LoginView) {
        TODO("Not yet implemented")
    }
    fun onEvent(event: LoginEvent) {
        println(event)
    }
}
