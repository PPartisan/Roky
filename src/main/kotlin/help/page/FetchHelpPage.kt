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
            .use { it.get(URL2).body<String>() }
            .let { Parser.builder().build().parse(it) }
            .let(LanternaMarkdown::render)
            .let(::LoadedHelpViewState)

    companion object {
        private const val URL = "https://gist.githubusercontent.com/PPartisan/95aa816faaec2a234d7069a48806d7cb/raw"
        private const val URL2 =
            "https://gist.githubusercontent.com/sorellla/8dca8a069985dfc30171ff036b4d833a/raw/acb1cd132b4999568f07026d5bcc5ba66c55f5a0/MarkdownTest.md"
        private const val URL3 = "https://gist.githubusercontent.com/sorellla/ecbba799722bf1e99203f054872e1b19/raw/725331bd9fa1f55845ff1c3b00079e63eba40680/provaHelpPageRoky.md"
        private const val URL4 ="https://gist.githubusercontent.com/sorellla/e6e5806360b701e64eba5cf06570aa55/raw/b9278188e3be5bc3a56e7362dca695c437bee751/mutableListOfFormattedRows.md"
    }
}
