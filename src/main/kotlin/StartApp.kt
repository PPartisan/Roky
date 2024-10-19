import com.googlecode.lanterna.screen.Screen
import navigation.NavigateToMainMenu

class StartApp (
    private val screen: Screen,
    private val menu: NavigateToMainMenu

) {
   operator fun invoke (){
       screen.startScreen()
       menu.toMainMenu()
   }
}
