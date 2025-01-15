package profile

import arch.WindowScope
import arch.WindowScopeProvider
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.Direction.VERTICAL
import com.googlecode.lanterna.gui2.LinearLayout.Alignment.End
import com.googlecode.lanterna.gui2.Window.Hint.CENTERED
import kotlinx.coroutines.cancel
import navigation.NavigateToMainMenu
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import profile.ProfileViewState.Pending
import view.*

class ProfileWindow(
    menu: NavigateToMainMenu
) : AppWindow("Profile", menu), ProfileView, KoinScopeComponent, WindowScope by WindowScopeProvider() {
    override val scope: Scope by lazy { createScope(this) }
    private val presenter: ProfilePresenter by inject()

    private val username: TextBox
    private val submit: Button
    private val status: Label

    init {
        setHints(listOf(CENTERED))

        username = TextBox().linearLayoutFill()
        status = Label("").linearLayoutFill()

        val usernamePanel = Panel(LinearLayout()).apply {
            addComponent(username)
        }.withBorder(Borders.singleLine("Username")).linearLayoutFill()

        val statusPanel = Panel(LinearLayout()).apply {
            addComponent(status)
        }.linearLayoutFill()

        val buttonPanel = Panel().apply {
            submit = Button("Submit") {
                presenter.onEvent(ProfileEvent.RequestUsername(username.text))
            }
            addComponent(submit)
        }.setLayoutData(LinearLayout.createLayoutData(End))
        val profile = Panel(LinearLayout(VERTICAL)).apply {
            paddingTop()
            addComponent(usernamePanel)
            paddingTop()
            addComponent(buttonPanel)
            paddingTop()
            addComponent(statusPanel)

        }
        val root = Panel(BorderLayout())
            .setPreferredSize(TerminalSize(DEFAULT_TERMINAL_WIDTH, DEFAULT_TERMINAL_HEIGHT))
            .apply {
                paddingHorizontal()
                addComponent(profile)
            }
        component = root
        presenter.attach(this)
    }

    override fun show(state: ProfileViewState) {
        username.text = ""
        status.text = state.status
        setUiInteractable(state !is Pending)
    }

    override fun close() {
        super.close()
        scope.close()
        windowScope.cancel()
    }

    private fun setUiInteractable(isInteractable: Boolean){
        username.isEnabled = isInteractable
        submit.isEnabled = isInteractable
    }
}
