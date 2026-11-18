package profile

import arch.Presenter
import arch.RokyDispatchers
import chatserver.ChatRepositories
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import profile.ProfileEvent.RequestUsername
import profile.ProfileViewState.*
import profile.ValidateUsernameRequest.Result.Ok

class ProfilePresenter(
    private val windowScope: CoroutineScope,
    private val requestUsername: ValidateUsernameRequest,
    private val usernames: ReadChatRepository<ProfileResult>,
    dispatchers: RokyDispatchers,
) : Presenter<ProfileView>(dispatchers) {
    constructor(
        windowScope: CoroutineScope,
        requestUsername: ValidateUsernameRequest,
        repositories: ChatRepositories,
        dispatchers: RokyDispatchers
    ) :
        this(windowScope, requestUsername, repositories.readProfiles(), dispatchers)

    private val state: MutableStateFlow<ProfileViewState> = MutableStateFlow(Idle)

    override fun onAttach(view: ProfileView) {
        windowScope.launch(dispatchers.main) {
            state.collect(::show)
        }
        windowScope.launch(dispatchers.io) {
            usernames.observe().drop(1).map { it.toViewState() }.collect { upd -> state.update { upd } }
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
            val result = requestUsername(event.username)
            if (result !is Ok)
                state.emit(Failed(result.message))
        }
    }

    private fun show(viewState: ProfileViewState) {
        withView { view -> view.show(viewState) }
    }

    private fun ProfileResult.toViewState(): ProfileViewState =
        if (isOk) {
            Success(MESSAGE_OK)
        } else {
            Failed(error.toErrorViewState())
        }

    companion object {
        @VisibleForTesting
        internal const val MESSAGE_OK = "Successfully changed username!"
        private fun Exception?.toErrorViewState(): String {
            if (this == null)
                return "Unsuccessful!"
            return when {
                this is PostgrestRestException && code == "23514" -> "Username contains illegal characters."
                else -> message ?: "Unsuccessful!"
            }
        }

    }
}
