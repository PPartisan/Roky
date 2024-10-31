package login

import arch.Presenter
import arch.RokyDispatchers
import arch.WindowScope
import arch.WindowScopeProvider

class LoginPresenter(
    dispatchers: RokyDispatchers
) : Presenter<LoginView>(dispatchers), WindowScope by WindowScopeProvider() {
    override fun onAttach(view: LoginView) {
        TODO("Not yet implemented")
    }

    override fun onDetach(view: LoginView) {
        TODO("Not yet implemented")
    }
}