package mainmenu

import StartApp
import StopApp
import authentication.Authenticator
import com.sun.tools.javac.Main
import io.mockk.MockK
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mainmenu.MainMenuEvent.*
import mainmenu.MainMenuViewState.Companion.loggedIn
import mainmenu.MainMenuViewState.Companion.loggedOut
import navigation.NavigateToAppWindow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MainMenuPresenterTest {
    private lateinit var quit : StopApp
    private lateinit var authenticator: Authenticator
    private lateinit var presenter: MainMenuPresenter
    private lateinit var navigate : NavigateToAppWindow
    private lateinit var view : MainMenuView

    @BeforeEach
    fun setUp() {
        navigate = mockk(relaxed = true)
        quit = mockk(relaxed = true)
        authenticator = mockk(relaxed = true)
        view = mockk(relaxed = true)
        presenter = MainMenuPresenter(quit, navigate, authenticator)
    }

    @Test
    fun `when user clicks quit button, then quit application`() {
        presenter.onEvent(SelectQuit)
        verify { quit() }
    }

    @Test
    fun `when user clicks login button, then open the login page`() {
        presenter.onEvent(SelectLogin)
        verify { navigate.toLogin() }
    }

    @Test
    fun `when user clicks help button, then open the help page`() {
       presenter.onEvent(SelectHelp)
        verify { navigate.toHelp() }
    }

    @Test
    fun `when user clicks about button, then open the about page`() {
        presenter.onEvent(SelectAbout)
        verify { navigate.toAbout() }
    }

    @Test
    fun `when user clicks profile button, then open the profile page`() {
        presenter.onEvent(SelectProfile)
        verify { navigate.toProfile() }
    }

    @Test
    fun `when user clicks chatroom button, then open the chatroom page`() {
        presenter.onEvent(SelectJoinChatroom)
        verify { navigate.toChatRoom() }
    }

    @Test
    fun `when user is logged out, then show logged out options`() {
        every { authenticator.isLoggedIn() } returns false
        presenter.attach(view)
        verify { view.show(loggedOut) }
    }

    @Test
    fun `when user is logged in, then show logged in options`() {
        every { authenticator.isLoggedIn() } returns true
        presenter.attach(view)
        verify { view.show(loggedIn) }
    }
}
