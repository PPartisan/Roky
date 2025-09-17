package chatroom.users

import arch.Presenter
import arch.RokyDispatchers
import chatroom.users.UsersViewState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.cutOff

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
//withView wants a function (V -> stuff) where V is the X we have passed inside the < > on line 16: so when I write "view -> do stuff" I'm saying "in the following code I'm calling X ""view"", now do what I say after"
//or when before we said "it.stuff()" it meant that ""it"" is the X
    private fun show(viewState: UsersViewState) {
        withView { view ->
            val width = view.getWidth()
            if (viewState is Users) {
                val truncated = viewState.users.map { it.cutOff(width) }
                view.show(Users(truncated))
            } else {
                view.show(viewState)
            }
        }
    }

    override fun onDetach(view: UsersListView) {
        // Deliberately empty
    }
}
