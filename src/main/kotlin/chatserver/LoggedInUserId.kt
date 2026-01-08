package chatserver

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

class LoggedInUserId(private val client: SupabaseClient) {
    operator fun invoke(): String = client.auth.currentUserOrNull()?.id.orEmpty()
}
