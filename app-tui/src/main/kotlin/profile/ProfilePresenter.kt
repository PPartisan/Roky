package profile

import arch.Presenter
import arch.RokyDispatchers
import chatserver.ChatRepositories
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
    private val usernames: ReadChatRepository<Map<String, String>>,
    dispatchers: RokyDispatchers,
) : Presenter<ProfileView>(dispatchers) {

    constructor(windowScope: CoroutineScope, repositories: ChatRepositories, dispatchers: RokyDispatchers) :
        this(windowScope, repositories.writeProfiles(), repositories.readProfiles(), dispatchers)

    private val status = MutableStateFlow<ProfileViewState>(Idle)

    override fun onAttach(view: ProfileView) {
        windowScope.launch(dispatchers.main) {
            status.collect(view::show)
        }

        windowScope.launch(dispatchers.io) {
            usernames.observe().collect { item ->
                status.value = Idle
            }
        }
    }

    override fun onDetach(view: ProfileView) {
        // RESOLUTION: Implemented required abstract base member from core Presenter contract
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is RequestUsername -> {
                status.value = Pending
                windowScope.launch(dispatchers.io) {
                    try {
                        requestUsername.write(event.username)
                        status.value = Success(MESSAGE_OK)
                    } catch (e: Exception) {
                        // RESOLUTION: Instantiates Failed data class explicitly with required string status argument
                        status.value = Failed("Failed to update username profile context.")
                    }
                }
            }
        }
    }

    companion object {
        @VisibleForTesting
        const val MESSAGE_OK = "Username updated successfully!"
    }
}
