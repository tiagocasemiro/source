package br.com.source.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowSize
import br.com.source.model.domain.Credential
import br.com.source.model.domain.LocalRepository
import br.com.source.model.domain.RemoteRepository
import br.com.source.model.util.emptyString
import br.com.source.model.util.emptyValidation
import br.com.source.model.util.validation
import br.com.source.view.common.ChooseFolderButton
import br.com.source.view.common.Fonts
import br.com.source.view.common.StatusStyle
import br.com.source.view.common.appPadding
import br.com.source.view.components.SourceButton
import br.com.source.view.components.SourceTextField
import br.com.source.view.components.SourceWindowDialog
import br.com.source.viewmodel.AddRepositoryViewModel
import org.koin.java.KoinJavaComponent
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JPanel

@Composable
fun AddRemoteRepositoryDialog(close: () -> Unit) {
    SourceWindowDialog(close,"Clone remote repository", size = WindowSize(600.dp, 470.dp)) {
        AddRemoteRepository(close)
    }
}

@Composable
fun AddRemoteRepository(close: () -> Unit) {
    val addRemoteRepositoryViewModel: AddRepositoryViewModel = KoinJavaComponent.get(AddRepositoryViewModel::class.java)
    val nameRemember = remember { mutableStateOf(emptyString()) }
    val pathRemember = remember { mutableStateOf(emptyString()) }
    val urlRemember = remember { mutableStateOf(emptyString()) }
    val usernameRemember = remember { mutableStateOf(emptyString()) }
    val passwordRemember = remember { mutableStateOf(emptyString()) }
    val nameValidationRemember = remember { mutableStateOf(emptyString()) }
    val pathValidationRemember = remember { mutableStateOf(emptyString()) }
    val usernameValidationRemember = remember { mutableStateOf(emptyString()) }
    val passwordValidationRemember = remember { mutableStateOf(emptyString()) }
    val urlValidationRemember = remember { mutableStateOf(emptyString()) }
    val openDialogFileChoose = remember { mutableStateOf(false) }
    if(openDialogFileChoose.value) {
        openDialogFileChoose.value = false
        SwingPanel(
            background = Color.Transparent,
            modifier = Modifier.size(0.dp, 0.dp),
            factory = {
                JPanel()
            },
            update = { pane ->
                val chooser = JFileChooser()
                chooser.currentDirectory = File(pathRemember.value.ifEmpty { System.getProperty("user.home") })
                chooser.dialogTitle = "Select root directory of repository"
                chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                val returnVal = chooser.showOpenDialog(pane)
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    val file = chooser.selectedFile
                    pathRemember.value = file.absolutePath
                }
            }
        )
    }

    Box(modifier = Modifier.background(StatusStyle.backgroundColor)) {
        Column(
            modifier = Modifier.padding(appPadding).background(StatusStyle.backgroundColor)
        ) {
            Text("Clone repository",
                fontFamily = Fonts.balooBhai2(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                style = TextStyle(
                    color = StatusStyle.titleAlertColor
                )
            )
            Spacer(modifier = Modifier.size(appPadding))
            SourceTextField(text = nameRemember, label = "Name", errorMessage = nameValidationRemember)
            Spacer(modifier = Modifier.size(6.dp))
            SourceTextField(text = pathRemember, label = "Path", trailingIcon = {
                ChooseFolderButton {
                    openDialogFileChoose.value = true
                }
            }, errorMessage = pathValidationRemember)
            Spacer(modifier = Modifier.size(6.dp))
            SourceTextField(text = urlRemember, label = "Url", errorMessage = urlValidationRemember)
            Spacer(modifier = Modifier.size(6.dp))
            SourceTextField(text = usernameRemember, label = "Username", errorMessage = usernameValidationRemember)
            Spacer(modifier = Modifier.size(6.dp))
            SourceTextField(text = passwordRemember, label = "Password", isPassword = true, errorMessage = passwordValidationRemember)
            Spacer(modifier = Modifier.fillMaxSize().weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                SourceButton("cancel", color = StatusStyle.negativeButtonColor) {
                    close()
                }
                Spacer(modifier = Modifier.width(10.dp))
                SourceButton("clone") {
                    val isFormValid = nameRemember.validation(listOf(emptyValidation()), nameValidationRemember, "Name is required") and
                            pathRemember.validation(listOf(emptyValidation()), pathValidationRemember, "Path to repository is required") and
                            urlRemember.validation(listOf(emptyValidation()), urlValidationRemember, "Url of repository is required") and
                            usernameRemember.validation(listOf(emptyValidation()), usernameValidationRemember, "Username is required") and
                            passwordRemember.validation(listOf(emptyValidation()), passwordValidationRemember, "Password is required")

                    if(isFormValid) {
                        val remoteRepository = RemoteRepository(
                            url = urlRemember.value,
                            localRepository = LocalRepository(
                                name = nameRemember.value,
                                workDir = pathRemember.value,
                                credential = Credential(
                                    username = usernameRemember.value,
                                    password = passwordRemember.value
                                )
                            )
                        )
                        addRemoteRepositoryViewModel.clone(remoteRepository)
                        close()
                    }
                }
            }
        }
    }

}