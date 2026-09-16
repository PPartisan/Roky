package help

import arch.Presenter
import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HelpPresenter(
    private val windowScope: CoroutineScope,
    dispatchers: RokyDispatchers,
    private val page: suspend () -> HelpViewState,
) : Presenter<HelpView>(dispatchers) {
    override fun onAttach(view: HelpView) {
        view.show(LoadingHelpViewState())
        windowScope.launch(dispatchers.io) {
            val page = page()
            withContext(dispatchers.main) {
                withView { view ->
                    view.show(page)
                }
            }
        }
    }

    override fun onDetach(view: HelpView) {
        TODO("Not yet implemented")
    }
}
