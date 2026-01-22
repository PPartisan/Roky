package chatserver

import chatserver.ReadChatRepository.ReadResult
import chatserver.profiles.SupabaseProfilesRepository.Profile

data class ProfileResult(
    override val isOk: Boolean,
    override val item: Map<String, Profile>,
    override val error: Exception?,
) :
    ReadResult<Map<String, Profile>> {
    companion object {
        fun ok(item: Map<String, Profile>): ProfileResult = ProfileResult(true, item, null)

        fun fail(e: Exception? = null): ProfileResult = ProfileResult(false, emptyMap(), e)
    }
}
