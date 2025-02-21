package chatroom.users

import arch.Presenter
import arch.RokyDispatchers
import chatroom.users.UsersViewState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersListPresenter(
    private val users: UsersListUseCase,
    private val scope: CoroutineScope,
    dispatchers: RokyDispatchers,
) : Presenter<UsersListView>(dispatchers) {
    override fun onAttach(view: UsersListView) {
        view.show(Empty)
        scope.launch(dispatchers.io) {
            users().map(::Users).collect { state ->
                withContext(dispatchers.main) {
                    show(state)
                }
            }
        }
    }

    private fun show(viewState: UsersViewState) {
        withView { it.show(viewState) }
    }

    override fun onDetach(view: UsersListView) {
        // Deliberately empty
    }
}
