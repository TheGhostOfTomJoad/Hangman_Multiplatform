import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import Hangman_Multiplatform_Group.common.App


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
