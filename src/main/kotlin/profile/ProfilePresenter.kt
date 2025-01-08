package profile

import arch.Presenter
import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import profile.ProfileEvent.RequestUsername

class ProfilePresenter(
    private val windowScope: CoroutineScope,
    dispatchers: RokyDispatchers
) : Presenter<ProfileView>(dispatchers) {
    override fun onAttach(view: ProfileView) {
        TODO("Not yet implemented")
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
        println(event)
    }

}
