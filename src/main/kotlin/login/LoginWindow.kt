package login

import arch.WindowScope
import arch.WindowScopeProvider
import kotlinx.coroutines.cancel
import navigation.NavigateToMainMenu
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope
import view.AppWindow

class LoginWindow(menu: NavigateToMainMenu) : AppWindow("Login", menu),
    KoinScopeComponent, WindowScope by WindowScopeProvider() {
    override val scope: Scope by lazy { createScope(this) }
    override fun close() {
        super.close()
        scope.close()
        windowScope.cancel()
    }
}
