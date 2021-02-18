package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun LoginPage() {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    LoginScreen(username = username, password = password)
}

@Composable
fun LoginScreen(
    username: MutableState<String>,
    password: MutableState<String>
){
    Column {
        TextWithInput(text = username)
        Spacer(modifier = Modifier.size(15.dp))
        TextWithInput(text = password)
    }
}

@Composable
fun TextWithInput(text: MutableState<String>) {
    Row {
        Text(
            text = "username: ",
            modifier = Modifier
        )
        TextField(
            value = text.value,
            onValueChange = {
                text.value = it
            },
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