package help.page

import com.vladsch.flexmark.parser.Parser
import help.HelpViewState
import help.LoadedHelpViewState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class FetchHelpPage(
    private val client: HttpClient,
) {
    suspend operator fun invoke(): HelpViewState =
        client
            .use { it.get(URL).body<String>() }
            .let { Parser.builder().build().parse(it) }
            .let(LanternaMarkdown::render)
            .let(::LoadedHelpViewState)

    companion object {
        private const val URL = "https://gist.githubusercontent.com/PPartisan/95aa816faaec2a234d7069a48806d7cb/raw"
    }
}
