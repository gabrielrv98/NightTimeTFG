package com.esei.grvidal.nighttime.pages

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.esei.grvidal.nighttime.BottomNavigationScreens
import com.esei.grvidal.nighttime.NavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.ProfileViewModel
import com.esei.grvidal.nighttime.data.User
import com.esei.grvidal.nighttime.data.meUser
import androidx.lifecycle.ViewModel

@Composable
fun ProfileEditorPage(navController : NavHostController){
    val goBack = {
        navController.popBackStack(navController.graph.startDestination, false)
        navController.navigate(BottomNavigationScreens.Profile.route)
    }

    var name by remember { mutableStateOf(TextFieldValue(meUser.name))}
    var status by remember { mutableStateOf(TextFieldValue(meUser.status))}

    Column{
        Header(text = stringResource(R.string.edit_profile))

        TextChanger(
            name = name,
            set = {text -> name = text},
            text = stringResource(id = R.string.display_name))

        TextChanger(
            name = status,
            set = {text -> status = text},
            text = stringResource(id = R.string.status))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Button(onClick = goBack
            ) {
                Text("Cancelar")
            }

            Button(onClick = {
                saveData(user = meUser, name = name, status = status)
                goBack()
            }
            ) {
                Text("Aceptar")
            }
        }

    }
}

fun saveData(user: User, name: TextFieldValue, status : TextFieldValue){

    user.name = name.text
    user.status = status.text
}

@Composable
fun TextChanger(name: TextFieldValue, set : (TextFieldValue) -> Unit, text : String){

    Row(
        modifier = Modifier.padding(horizontal = 24.dp)
            .padding(bottom = 6.dp)
    ){
        Column{
            Text( text = text)
            TextField(
                modifier = Modifier.padding(horizontal = 4.dp),
                value = name, onValueChange = { set(it)} )
        }
    }
}