package com.esei.grvidal.nighttime.pages.login_pages

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.esei.grvidal.nighttime.CustomDialog
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.viewmodels.LoginViewModel
import com.esei.grvidal.nighttime.viewmodels.UserViewModel
import com.esei.grvidal.nighttime.scaffold.*


private const val TAG = "LoginPage"


@Composable
fun LoginArchitecture(
    loginVM: LoginViewModel,
    userVM: UserViewModel,
    messageError: String = "",
    searchImage: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = NavigationScreens.LoginPage.route) {
        composable(NavigationScreens.LoginPage.route) {// Login
            LoginPage(
                navController = navController,
                loginVM = loginVM,
                messageError = messageError
            )
        }

        composable(NavigationScreens.RegisterPage.route) {// Register
            RegisterPage(
                loginVM = loginVM,
                userVM = userVM,
                searchImageButton = searchImage
            )
        }
    }
}

@Composable
fun LoginPage(
    navController: NavHostController,
    loginVM: LoginViewModel,
    messageError: String = ""
) {

    val (username, setUsername) = remember { mutableStateOf(TextFieldValue()) }
    val (password, setPassword) = remember { mutableStateOf(TextFieldValue()) }

    val (showError, setShowError) = remember { mutableStateOf(false) }

    if (showError) {
        CustomDialog(
            onClose = { setShowError(false) }
        ) {
            Text(
                text = stringResource(id = R.string.errorConnectionTitle),
                style = MaterialTheme.typography.h6,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = stringResource(id = R.string.errorConnectionDescription),
                style = MaterialTheme.typography.body1
            )

        }

    }

    LoginScreen(
        showMessageError = messageError,
        username = username,
        setUsername = setUsername,
        password = password,
        setPassword = setPassword,
        doLogin = {
            // TODO: 06/09/2021 Faked Info register
            //loginVM.doLoginRefreshed(username.text, password.text)
            if (username.text == "grvidal" && password.text == "1234")
                loginVM.doFakeLoginRefreshed(username.text, password.text)
            else
                setShowError(true)

        },
        register = {
            navController.navigate(NavigationScreens.RegisterPage.route)
        }
    )
}

@Composable
fun LoginScreen(
    showMessageError: String,
    username: TextFieldValue,
    setUsername: (TextFieldValue) -> Unit,
    password: TextFieldValue,
    setPassword: (TextFieldValue) -> Unit,
    doLogin: () -> Unit,
    register: () -> Unit
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
            showMessageError = showMessageError,
            onClick = doLogin

        )

        Footer(
            text = stringResource(id = R.string.registerAdd),
            modifier = Modifier.weight(1f),
            register = register
        )

        Box(
            modifier = Modifier,//.weight(1f),
            alignment = Alignment.TopStart
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier,
                    text = "Accede con grvidal:1234",
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }


    }

}

@Composable
private fun LoggingForm(
    modifier: Modifier = Modifier,
    username: TextFieldValue,
    setUsername: (TextFieldValue) -> Unit,
    password: TextFieldValue,
    setPassword: (TextFieldValue) -> Unit,
    showMessageError: String,
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

            if (showMessageError.isNotEmpty()) {
                Text(
                    text = showMessageError,
                    style = MaterialTheme.typography.body2,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.size(15.dp))
            Button(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 10.dp),
                onClick = onClick,
                enabled = !(username.text.isEmpty() ||
                        password.text.isEmpty())
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
fun Footer(
    text: String,
    modifier: Modifier = Modifier,
    register: () -> Unit
) {

    Box(
        modifier = modifier.fillMaxSize(),
        alignment = Alignment.TopStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                text = text,
                style = MaterialTheme.typography.body1
            )

            Icon(
                asset = Icons.Rounded.PersonAdd,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clickable(
                        onClick = { register() }
                    )
            )
        }

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
    canContainSpace: Boolean = false,
    maxLetters: Int = 25,
    onImePerformed: (SoftwareKeyboardController?) -> Unit = {},
    placeholder: @Composable (() -> Unit)? = null
) {

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        value = text,
        onValueChange = { newText ->

            var trimmedText = newText.text

            if (!canContainSpace)
                trimmedText = trimmedText.trim()

            if (trimmedText.length >= maxLetters)
                trimmedText = trimmedText.dropLast(trimmedText.length - maxLetters)

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

