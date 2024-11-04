package login

import arch.WindowScope
import arch.WindowScopeProvider
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.Direction.VERTICAL
import com.googlecode.lanterna.gui2.LinearLayout.Alignment.End
import com.googlecode.lanterna.gui2.Window.Hint.CENTERED
import kotlinx.coroutines.cancel
import login.LoginEvent.Login
import navigation.NavigateToMainMenu
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import org.w3c.dom.Text
import view.AppWindow
import view.linearLayoutFill
import view.paddingHorizontal
import view.paddingTop
import javax.sound.sampled.Line

class LoginWindow(menu: NavigateToMainMenu) : AppWindow("Login", menu), LoginView,
    KoinScopeComponent, WindowScope by WindowScopeProvider() {
    override val scope: Scope by lazy { createScope(this) }
    private val presenter: LoginPresenter by inject()
    private val username: TextBox
    private val password: TextBox
    private val ok: Button
    private val status: Label
    init {
        setHints(listOf(CENTERED))

        username = TextBox().linearLayoutFill()
        password = TextBox().setMask('*').linearLayoutFill()
        status = Label("").linearLayoutFill()

        val usernamePanel = Panel(LinearLayout()).apply{
            addComponent(username)
        }.withBorder(Borders.singleLine("Username")).linearLayoutFill()

        val passwordPanel = Panel(LinearLayout()).apply{
            addComponent(password)
        }.withBorder(Borders.singleLine("Password")).linearLayoutFill()

        val statusPanel = Panel(LinearLayout()).apply{
            addComponent(status)
        }.linearLayoutFill()

        val buttonPanel = Panel().apply{
            ok = Button("OK"){
                presenter.onEvent(Login(username.text, password.text))
            }
            addComponent(ok)
        }.setLayoutData(LinearLayout.createLayoutData(End))
        val login = Panel(LinearLayout(VERTICAL)) .apply {
            paddingTop()
            addComponent(usernamePanel)
            paddingTop()
            addComponent(passwordPanel)
            paddingTop()
            addComponent(buttonPanel)
            paddingTop()
            addComponent(statusPanel)
        }
        val root = Panel(BorderLayout()).setPreferredSize(TerminalSize(40, 20))
            .apply {
                paddingHorizontal()
                addComponent(login)
            }
        component = root
        presenter.attach(this)
    }
    override fun close() {
        super.close()
        scope.close()
        windowScope.cancel()
    }

    override fun show(state: LoginViewState) {
        // empty
    }
}
