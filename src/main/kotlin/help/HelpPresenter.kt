package help

import arch.Presenter
import arch.RokyDispatchers
import help.page.FetchHelpPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HelpPresenter(
    private val windowScope: CoroutineScope,
    dispatchers: RokyDispatchers,
    ) : Presenter<HelpView>(dispatchers) {
    override fun onAttach(view: HelpView) {
        windowScope.launch(dispatchers.io) {
            val page = FetchHelpPage()
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
