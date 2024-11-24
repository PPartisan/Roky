package help

import arch.WindowScope
import arch.WindowScopeProvider
import kotlinx.coroutines.cancel
import navigation.NavigateToMainMenu
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope
import view.AppWindow

class HelpWindow(
    menu:NavigateToMainMenu
): AppWindow("Help",menu),HelpView,KoinScopeComponent,WindowScope by WindowScopeProvider() {
    override fun show(state: HelpViewState) = when(state) {
        is LoadingHelpViewState -> onLoading(state)
        is LoadedHelpViewState -> onLoaded(state)
    }

    private fun onLoading(state: LoadingHelpViewState) {
        println("onLoading:: $state")
    }

    private fun onLoaded(state: LoadedHelpViewState) {
        println("onLoaded:: $state")
    }

    override val scope: Scope by lazy { createScope(this) }
    override fun close() {
        scope.close()
        windowScope.cancel()
        super.close()
    }
}
