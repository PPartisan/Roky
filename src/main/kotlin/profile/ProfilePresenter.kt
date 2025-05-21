package profile

import arch.Presenter
import arch.RokyDispatchers
import chatserver.ChatRepositories
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import profile.ProfileEvent.RequestUsername
import profile.ProfileViewState.*

class ProfilePresenter(
    private val windowScope: CoroutineScope,
    private val requestUsername: WriteChatRepository<String>,
    private val usernames: ReadChatRepository<ProfileResult>,
    dispatchers: RokyDispatchers,
) : Presenter<ProfileView>(dispatchers) {
    constructor(windowScope: CoroutineScope, repositories: ChatRepositories, dispatchers: RokyDispatchers) :
        this(windowScope, repositories.writeProfiles(), repositories.readProfiles(), dispatchers)

    private val state: MutableStateFlow<ProfileViewState> = MutableStateFlow(Idle)

    override fun onAttach(view: ProfileView) {
        windowScope.launch(dispatchers.main) {
            state.collect(::show)
        }
        windowScope.launch(dispatchers.io) {
            usernames.observe().drop(1).map { it.toViewState() }.collect { state.value = it }
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
            requestUsername.write(event.username)
        }
    }

    private fun show(viewState: ProfileViewState) {
        withView { view -> view.show(viewState) }
    }

    private fun ProfileResult.toViewState(): ProfileViewState =
        if (isOk) {
            Success(MESSAGE_OK)
        } else {
            Failed(error?.message ?: "Unsuccessful!")
        }

    companion object {
        @VisibleForTesting
        internal const val MESSAGE_OK = "Successfully changed username!"
    }
}
