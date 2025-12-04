package chatroom.users

import arch.Presenter
import arch.RokyDispatchers
import chatroom.users.UsersViewState.*
import chatserver.ChatRepositories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersListPresenter(
    private val truncate: UsersListTruncation,
    private val repository: ChatRepositories,
    private val scope: CoroutineScope,
    dispatchers: RokyDispatchers,
) : Presenter<UsersListView>(dispatchers) {
    private var job: Job? = null

    override fun onAttach(view: UsersListView) {
        view.show(Empty)

        job =
            scope.launch(dispatchers.io) {
                repository.subscribePresence().subscribe()
                repository.readPresence().observe()
                    .map { it.item.toList() }
                    .truncateUsernames()
                    .map(::Users)
                    .collect { state ->
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
        repository.subscribePresence().unsubscribe()
        job?.cancel()
    }

    private fun Flow<List<String>>.truncateUsernames() =
        map { username ->
            username.map(truncate::invoke)
        }
}
