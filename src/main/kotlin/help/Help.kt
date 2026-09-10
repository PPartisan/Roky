package help

import help.page.FetchHelpPage
import org.koin.dsl.module

val helpModule =
    module {
        scope<HelpWindow> {
            scoped {
                HelpPresenter(
                    dispatchers = get(),
                    windowScope = get<HelpWindow>(),
                    page = FetchHelpPage(get())::invoke,
                )
            }
        }
    }
