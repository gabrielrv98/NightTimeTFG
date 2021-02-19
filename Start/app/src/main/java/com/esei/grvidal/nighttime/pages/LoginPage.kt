package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.R

@Composable
fun LoginPage() {
    val (username,setUsername) = remember { mutableStateOf("") }
    val (password,setPassword) = remember { mutableStateOf("") }

    LoginScreen(
        username = username,
        setUsername = setUsername,
        password = password,
        setPassword = setPassword
    )
}

@Composable
fun LoginScreen(
    username: String,
    setUsername: (String) -> Unit,
    password: String,
    setPassword: (String) -> Unit,
){
    Column {
        LoginForm(username, setUsername, password, setPassword)

        Spacer(modifier = Modifier.size(15.dp))

        Button(onClick = {
            //save data
        }) {

            Text(text= stringResource(id = R.string.acceptar))
        }
    }
}

@Composable
private fun LoginForm(
    username: String,
    setUsername: (String) -> Unit,
    password: String,
    setPassword: (String) -> Unit
) {
    TextWithInput(
        text = username,
        setText = setUsername
    )

    Spacer(modifier = Modifier.size(15.dp))

    TextWithInput(
        text = password,
        setText = setPassword
    )
}

@Composable
fun TextWithInput(text: String, setText: (String) -> Unit) {
    Row {
        Text(
            text = "username: ",
            modifier = Modifier
        )
        TextField(
            value = text,
            onValueChange = { newText->  setText (newText) },
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
            textStyle = TextStyle(color = Color.DarkGray),
            onImeActionPerformed = { action, softwareController ->
                if (action == ImeAction.Done) {
                    softwareController?.hideSoftwareKeyboard()
                }
            }
        )
    }
}