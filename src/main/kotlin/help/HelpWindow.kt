package help

import arch.WindowScope
import arch.WindowScopeProvider
import com.googlecode.lanterna.SGR.*
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.Direction.VERTICAL
import com.googlecode.lanterna.gui2.LinearLayout.Alignment.Fill
import com.googlecode.lanterna.gui2.Window.Hint.CENTERED
import help.FormattedTextRow.*
import kotlinx.coroutines.cancel
import navigation.NavigateToMainMenu
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import view.AppWindow
import java.awt.Desktop
import java.awt.Desktop.Action.BROWSE
import java.net.URI

class HelpWindow(
    menu: NavigateToMainMenu,
) : AppWindow("Help", menu), HelpView, KoinScopeComponent, WindowScope by WindowScopeProvider() {
    override val scope: Scope by lazy { createScope(this) }
    private val content: Panel
    private val presenter: HelpPresenter by inject()

    init {
        setHints(listOf(CENTERED))
        content = Panel(LinearLayout(VERTICAL)).setLayoutData(LinearLayout.createLayoutData(Fill))
        component = content
        presenter.attach(this)
    }

    override fun show(state: HelpViewState) {
        content.removeAllComponents()
        when (state) {
            is LoadingHelpViewState -> onLoading()
            is LoadedHelpViewState -> onLoaded(state)
        }
    }

    private fun onLoading() {
        listOf(LineBreak(), PlainText("Loading…"), LineBreak())
            .map { it.toComponent() }
            .map { content.addComponent(it) }
            .forEach {
                it.setLayoutData(LinearLayout.createLayoutData(Fill))
            }
    }

    private fun onLoaded(state: LoadedHelpViewState) {
        state.rows.map { it.toComponent() }.map { content.addComponent(it) }.forEach {
            it.setLayoutData(LinearLayout.createLayoutData(Fill))
        }
    }

    private fun openLink(url: String) {
        if (!Desktop.isDesktopSupported()) return
        val desktop: Desktop = Desktop.getDesktop()
        if (!desktop.isSupported(BROWSE)) return
        desktop.browse(URI(url))
    }

    private fun FormattedTextRow.toComponent(): Component =
        when (this) {
            is Bold -> Label(text).addStyle(BOLD)
            is Header -> Label(text).addStyle(BOLD).addStyle(UNDERLINE)
            is Hyperlink -> Button(text) { openLink(url) }
            is Italic -> Label(text).addStyle(ITALIC)
            is LineBreak -> EmptySpace(TerminalSize(0, 1))
            is PlainText -> Label(text)
        }

    override fun close() {
        scope.close()
        cancel()
        super.close()
    }
}
