package profile

import arch.Presenter
import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import profile.ProfileEvent.RequestUsername
import profile.ProfileViewState.*

class ProfilePresenter(
    private val windowScope: CoroutineScope,
    private val requestUsername: RequestUserNameUseCase,
    dispatchers: RokyDispatchers
) : Presenter<ProfileView>(dispatchers) {
    override fun onAttach(view: ProfileView) {
       println("ATTACH PRESENT00R")
    }

    override fun onDetach(view: ProfileView) {
        windowScope.cancel()
    }

    fun onEvent(event: ProfileEvent) {
        if (event is RequestUsername) {
            onRequestUsername(event)
        }
    }

    private fun onRequestUsername(event: RequestUsername) {
        withView { it.show(Pending) }
        windowScope.launch(dispatchers.io) {
            val result = requestUsername(event.username)
            withContext(dispatchers.main){
                withView { it.show(result) }
            }
        }
    }
}
