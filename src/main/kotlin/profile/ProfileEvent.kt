package profile

interface ProfileEvent {
    data class RequestUsername(val username: String) : ProfileEvent
}
