package help.page

import com.vladsch.flexmark.parser.Parser
import help.HelpViewState
import help.LoadedHelpViewState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get

object FetchHelpPage {
    private const val URL = "https://gist.githubusercontent.com/PPartisan/95aa816faaec2a234d7069a48806d7cb/raw"

    suspend operator fun invoke(): HelpViewState {
        val markdown = HttpClient(CIO).use { it.get(URL).body<String>() }
        return Parser.builder().build().parse(markdown)
            .let(LanternaMarkdown::render)
            .let(::LoadedHelpViewState)
    }
}
