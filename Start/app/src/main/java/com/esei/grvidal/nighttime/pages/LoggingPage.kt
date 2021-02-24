package com.esei.grvidal.nighttime.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BaseTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focusObserver
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.UserViewModel


private const val TAG = "LoginPage"

@Composable
fun LoginPage(userToken: UserViewModel) {

    val (username, setUsername) = remember { mutableStateOf(TextFieldValue()) }
    val (password, setPassword) = remember { mutableStateOf(TextFieldValue()) }


    val validateOnClick = {
        userToken.doLoginRefreshed(username.text,password.text)
    }

    LoginScreen(
        username = username,
        setUsername = setUsername,
        password = password,
        setPassword = setPassword,
        onClick = validateOnClick
    )

/*
    LoginScreen(
        username = username,
        setUsername = setUsername,
        password = password,
        setPassword = setPassword,
        onClick = validateOnClick
    )
*/

}

@Composable
fun LoginScreen(
    username: TextFieldValue,
    setUsername: (TextFieldValue) -> Unit,
    password: TextFieldValue,
    setPassword: (TextFieldValue) -> Unit,

    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Header(
            title = stringResource(id = R.string.app_name),
            modifier = Modifier.weight(1f)
        )

        LoggingForm(
            modifier = Modifier.weight(1f),
            username = username,
            setUsername = setUsername,
            password = password,
            setPassword = setPassword,
            onClick = {
                if(username.text.isNotEmpty() &&
                    password.text.isNotEmpty()
                ) onClick()
            }
        )

        Footer(
            modifier = Modifier.weight(1f),
            text = "¿No tienes cuenta?\nRegistrate ya!"
        )

    }

}

@Composable
private fun LoggingForm(
    modifier: Modifier = Modifier,
    username: TextFieldValue,
    setUsername: (TextFieldValue) -> Unit,
    password: TextFieldValue,
    setPassword: (TextFieldValue) -> Unit,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        Column {
            LoginFields(
                username = username,
                setUsername = setUsername,
                password = password,
                setPassword = setPassword
            )
            Spacer(modifier = Modifier.size(15.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick
            ) {
                Text(
                    text = stringResource(id = R.string.log_in),
                    modifier = Modifier.padding(vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier, title: String) {

    Box(
        modifier = modifier.fillMaxSize(),
        alignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h2,
            fontFamily = FontFamily.Cursive
        )
    }
}

@Composable
fun Footer(modifier: Modifier = Modifier, text: String) {

    Box(
        modifier = modifier.fillMaxSize(),
        alignment = Alignment.TopStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body1
        )
    }
}


@OptIn(ExperimentalFocus::class)
@Composable
private fun LoginFields(
    username: TextFieldValue,
    setUsername: (TextFieldValue) -> Unit,
    password: TextFieldValue,
    setPassword: (TextFieldValue) -> Unit
) {

    val focusRequester = remember { FocusRequester() }

    TextWithInput(
        text = username,
        setText = setUsername,
        onImePerformed = {
            focusRequester.requestFocus()
        }
    ) {
        Text(text = stringResource(id = R.string.username))

    }

    Spacer(modifier = Modifier.size(15.dp))

    TextWithInput(
        modifier = Modifier.focusRequester(focusRequester = focusRequester),
        text = password,
        setText = setPassword,
        isPassword = true,
        onImePerformed = { softwareController ->
            softwareController?.hideSoftwareKeyboard()
        }
    ) {
        Text(text = stringResource(id = R.string.password))
    }

}

@Composable
fun TextWithInput(
    modifier: Modifier = Modifier,
    text: TextFieldValue,
    setText: (TextFieldValue) -> Unit,
    isPassword: Boolean = false,
    maxLetters: Int = 25,
    onImePerformed: (SoftwareKeyboardController?) -> Unit = {},
    placeholder: @Composable (() -> Unit)? = null
) {
    Row {

        TextField(
            modifier = modifier.fillMaxWidth()
                .wrapContentHeight(),
            value = text,
            onValueChange = { newText ->

                var trimmedText = newText.text.trim()

                if (trimmedText.length >= maxLetters) //TODO ADJUST THIS
                    trimmedText= trimmedText.dropLast(trimmedText.length - maxLetters )

                if (trimmedText.contains('\n'))
                    trimmedText = trimmedText.filter { letter -> letter != '\n' }

                setText(
                    TextFieldValue(
                        text = trimmedText,
                        selection = TextRange(trimmedText.length)
                    )
                )
            },
            placeholder = placeholder,
            /** if it's [KeyboardType.Text] characters duplicates */
            keyboardType = KeyboardType.Password,
            visualTransformation = if (isPassword) PasswordVisualTransformation()
            else VisualTransformation.None,
            imeAction = ImeAction.Done,
            textStyle = TextStyle(color = Color.DarkGray),
            onImeActionPerformed = { action, softwareController ->

                Log.d(TAG, "TextWithInput: action= $action")
                onImePerformed(softwareController)
            }
        )
    }
}

///-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

//                                                    Pro way

///-----------------------------------------------------------------------------------------------------------------------------------------------------------------------


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    username: TextFieldValue,
    setUsername: (TextFieldValue) -> Unit,
    password: TextFieldValue,
    setPassword: (TextFieldValue) -> Unit
) {

///    Text("borrar esto")//todo borrar esta linea

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }

    UserInputText(
        modifier = Modifier.preferredWidth(200.dp),//.weight(1f),
        textFieldValue = username,
        onTextChanged = { setUsername(it) },
        // Only show the keyboard if there's no input selector and text field has focus
        keyboardShown = textFieldFocusState,
        // Close extended selector if text field receives focus
        onTextFieldFocused = { focused ->
            textFieldFocusState = focused
            Log.d("check1", "UserInput1: focus = $focused")
        },
        focusState = textFieldFocusState
    )

    Spacer(modifier = Modifier.size(15.dp))

    UserInputText(
        modifier = Modifier.preferredWidth(200.dp),//.weight(1f),
        textFieldValue = password,
        onTextChanged = { setPassword(it) },
        // Only show the keyboard if there's no input selector and text field has focus
        keyboardShown = textFieldFocusState,
        // Close extended selector if text field receives focus
        onTextFieldFocused = { focused ->

            textFieldFocusState = focused
            Log.d("check1", "UserInput2: focus = $focused")
        },
        focusState = textFieldFocusState
    )
}

@OptIn(ExperimentalFocus::class)
@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean
) {
    // Grab a reference to the keyboard controller whenever text input starts
    var keyboardController by remember { mutableStateOf<SoftwareKeyboardController?>(null) }

    // Show or hide the keyboard
    onCommit(keyboardController, keyboardShown) { // Guard side-effects against failed commits
        keyboardController?.let {
            if (keyboardShown) it.showSoftwareKeyboard() else it.hideSoftwareKeyboard()
        }
    }

    Row(
        modifier = modifier
            //.fillMaxWidth()
            .preferredHeight(48.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier.preferredHeight(48.dp).weight(1f).align(Alignment.Bottom)
        ) {
            var lastFocusState by remember { mutableStateOf(FocusState.Inactive) }
            BaseTextField(
                value = textFieldValue,
                onValueChange = { onTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .align(Alignment.CenterStart)
                    .focusObserver { state ->
                        if (lastFocusState != state) {
                            onTextFieldFocused(state == FocusState.Active)
                        }
                        lastFocusState = state
                    },
                keyboardType = keyboardType,
                imeAction = ImeAction.Send,
                onTextInputStarted = { controller -> keyboardController = controller }
            )

            val disableContentColor =
                AmbientEmphasisLevels.current.disabled.applyEmphasis(MaterialTheme.colors.onSurface)
            if (textFieldValue.text.isEmpty() && !focusState) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp),
                    text = stringResource(id = R.string.TextFieldHint),
                    style = MaterialTheme.typography.body1.copy(color = disableContentColor)
                )
            }
        }
    }
}