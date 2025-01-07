package profile

import arch.Presenter
import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope

class ProfilePresenter(
    private val windowScope: CoroutineScope,
    dispatchers: RokyDispatchers
) : Presenter<ProfileView>(dispatchers) {
    override fun onAttach(view: ProfileView) {
        TODO("Not yet implemented")
    }

    override fun onDetach(view: ProfileView) {
        TODO("Not yet implemented")
    }

    fun onEvent(event: ProfileEvent) {
        TODO("Not yet implemented")
    }

}
