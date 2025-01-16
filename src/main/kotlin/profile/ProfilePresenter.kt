package profile

import arch.Presenter
import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import profile.ProfileEvent.RequestUsername
import profile.ProfileViewState.Idle
import profile.ProfileViewState.Pending

class ProfilePresenter(
    private val windowScope: CoroutineScope,
    private val requestUsername: RequestUserNameUseCase,
    dispatchers: RokyDispatchers
) : Presenter<ProfileView>(dispatchers) {

    private val state: MutableStateFlow<ProfileViewState> = MutableStateFlow(Idle)

    override fun onAttach(view: ProfileView) {
        windowScope.launch(dispatchers.main) {
            state.collect(::show)
        }
    }

    override fun onDetach(view: ProfileView) {
        //Deliberately empty
    }

    fun onEvent(event: ProfileEvent) {
        if (event is RequestUsername) {
            onRequestUsername(event)
        }
    }

    private fun onRequestUsername(event: RequestUsername) {
        state.value = Pending
        windowScope.launch(dispatchers.io) {
            state.value = requestUsername(event.username)
        }
    }

    private fun show(viewState: ProfileViewState) {
        withView { view -> view.show(viewState) }
    }
}
