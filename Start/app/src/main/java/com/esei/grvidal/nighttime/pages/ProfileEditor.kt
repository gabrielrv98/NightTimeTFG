package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.esei.grvidal.nighttime.scaffold.BottomNavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.User
import com.esei.grvidal.nighttime.data.meUser

@Composable
fun ProfileEditorPage(navController: NavHostController) {
    val goBack = {
        navController.popBackStack(navController.graph.startDestination, false)
        navController.navigate(BottomNavigationScreens.ProfileNav.route)
    }

    var name by remember { mutableStateOf(TextFieldValue(meUser.name)) }
    var status by remember { mutableStateOf(TextFieldValue(meUser.status)) }

    Column {
        Header(text = stringResource(R.string.edit_profile))
        AcceptDeclineButtons(
            accept = {
                saveData(user = meUser, name = name, status = status)
                goBack()
            },
            decline = { goBack() }
        )

        TextChanger(
            name = name,
            set = { text -> name = text },
            text = stringResource(id = R.string.display_name)
        )

        TextChanger(
            name = status,
            set = { text -> status = text },
            canBeEmpty = true,
            text = stringResource(id = R.string.status)
        )


    }

}


@Composable
private fun AcceptDeclineButtons(
    accept: () -> Unit,
    decline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButtonEditProfile(
            Icons.Outlined.Cancel,
            decline,
            stringResource(id = R.string.cancelar)
        )

        IconButtonEditProfile(Icons.Outlined.Check, accept, stringResource(id = R.string.aceptar))
    }
}

@Composable
private fun IconButtonEditProfile(
    icon: VectorAsset,
    accept: () -> Unit,
    text: String = ""
) {
    IconButton(
        modifier = Modifier.padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        onClick = accept
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(asset = icon)
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                Text(
                    text,
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 10.sp

                )
            }

        }

    }
}

fun saveData(user: User, name: TextFieldValue, status: TextFieldValue) {

    if (name.text.trim() != "")
        user.name = name.text.trim()
    user.status = status.text.trim()
}

@Composable
fun TextChanger(
    name: TextFieldValue,
    set: (TextFieldValue) -> Unit,
    canBeEmpty: Boolean = false,
    text: String
) {

    Row(
        modifier = Modifier.padding(horizontal = 24.dp)
            .padding(bottom = 6.dp, top = 6.dp)
    ) {
        Column {
            Row{
                Text(text = text)
                if (!canBeEmpty){ Advert( name.text, Modifier.align(Alignment.Bottom)) }
            }

            TextField(
                modifier = Modifier.padding(horizontal = 6.dp)
                    .padding(top = 4.dp),
                value = name, onValueChange = { set(it) })
        }
    }
}

@Composable
fun Advert( text: String, modifier :Modifier ){
    ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
        Text(
            modifier = modifier.padding(start = 6.dp)
                ,
            style = MaterialTheme.typography.subtitle1,
            fontSize = 12.sp,
            text = "*" + stringResource(id = R.string.cant_be_empty),
            color = if (text.isBlank()) MaterialTheme.colors.error else MaterialTheme.colors.onSurface
        )
    }
}