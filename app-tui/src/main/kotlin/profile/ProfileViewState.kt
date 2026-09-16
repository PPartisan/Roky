package profile

interface ProfileViewState {
    val status: String

    data object Idle : ProfileViewState {
        override val status: String = ""
    }

    data object Pending : ProfileViewState {
        override val status: String = "Waiting for response"
    }

    data class Success(
        override val status: String,
    ) : ProfileViewState

    data class Failed(
        override val status: String,
    ) : ProfileViewState
}
