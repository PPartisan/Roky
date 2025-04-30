package profile

import arch.Presenter
import arch.RokyDispatchers
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import profile.ProfileEvent.RequestUsername
import profile.ProfileViewState.*

class ProfilePresenter(
    private val windowScope: CoroutineScope,
    private val requestUsername: RequestUsernameUseCase,
    private val usernames: ReadChatRepository<ProfileResult>,
    dispatchers: RokyDispatchers,
) : Presenter<ProfileView>(dispatchers) {
    private val state: MutableStateFlow<ProfileViewState> = MutableStateFlow(Idle)

    override fun onAttach(view: ProfileView) {
        windowScope.launch(dispatchers.main) {
            state.collect(::show)
        }
        windowScope.launch(dispatchers.io) {
            usernames.observe().drop(1).map {it.toViewState()}.collect{state.value=it}
        }
    }

    override fun onDetach(view: ProfileView) {
        // Deliberately empty
    }

    fun onEvent(event: ProfileEvent) {
        if (event is RequestUsername) {
            onRequestUsername(event)
        }
    }

    private fun onRequestUsername(event: RequestUsername) {
        state.value = Pending
        windowScope.launch(dispatchers.io) {
            requestUsername(event.username)
        }
    }

    private fun show(viewState: ProfileViewState) {
        withView { view -> view.show(viewState) }
    }

    private fun ProfileResult.toViewState(): ProfileViewState =
        if (isOk) Success("Successfully changed username!")
        else Failed(error?.message ?: "Unsuccessful!")

}
