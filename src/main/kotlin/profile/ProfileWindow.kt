package profile

import arch.WindowScope
import arch.WindowScopeProvider
import login.LoginView
import navigation.NavigateToMainMenu
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope
import view.AppWindow

class ProfileWindow(
    menu: NavigateToMainMenu
) : AppWindow("Profile", menu), ProfileView, KoinScopeComponent, WindowScope by WindowScopeProvider() {
    override val scope: Scope by lazy { createScope(this) }

    override fun show(state: ProfileViewState) {
        TODO("Not yet implemented")
    }
}
