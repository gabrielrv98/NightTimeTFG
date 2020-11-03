package com.esei.grvidal.nighttime

//import androidx.compose.foundation.AmbientContentColor


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.ui.tooling.preview.Preview

import com.esei.grvidal.nighttime.ui.NightTimeTheme
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import java.util.*
import androidx.compose.material.Button as Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NightTimeTheme {
                MainScreen()
            }
        }
    }
}

//todo añadir cambiar de region


@Composable
fun MainScreen() {
    val (icon, setIcon) = remember { mutableStateOf(Icons.Default.LocalBar) }
    val text: String = when (icon) {
        Icons.Default.LocalBar -> ContextAmbient.current.getString(R.string.Bar_st)
        Icons.Default.Today -> ContextAmbient.current.getString(R.string.Calendario)
        Icons.Default.People -> ContextAmbient.current.getString(R.string.amigos)
        Icons.Default.AddComment -> ContextAmbient.current.getString(R.string.chat)
        else -> "error"
    }
    ScreenScaffolded(icon, setIcon) {
        MainView(text = text)
    }
}


@Composable
fun MainView(text: String = "empty") {
    // A surface container using the 'background' color from the theme
    Surface(
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) {
            if (text == "Calendario")
                CalendarPageView()
            else {
                Row {
                    Text(text = "Night Time main page")
                }
                Row {
                    Text(text = text)
                }

            }

        }


    }
}

@Composable
fun CalendarPageView() {

    val modifier = Modifier.fillMaxWidth()
    val (dia, setDia) = remember{ mutableStateOf(Calendar.getInstance())}

    Column( modifier = modifier.fillMaxWidth()
    ){

        Row( modifier = modifier.weight(1f),
            horizontalArrangement = Arrangement.Center){

            CalendarVidow()

        }

        //todo Eliminar este Divider es solo una referencia
        Divider(thickness = 2.dp, modifier = Modifier.fillMaxWidth())

        Row( modifier = modifier.weight(1f)){
            Surface(modifier = Modifier.padding(6.dp),
                border= BorderStroke(2.dp, MaterialTheme.colors.primary)
            ){
                Text(text = "abc", modifier = Modifier.padding(12.dp))
            }
        }

    }

}

@Composable
fun CalendarVidow(dias : Calendar){
    Surface(modifier = Modifier.padding(6.dp),
        border= BorderStroke(2.dp, MaterialTheme.colors.primary),
        shape = RoundedCornerShape(20)
    ){

        Text(text = "abc", modifier = Modifier.padding(12.dp))
    }
}

@Composable
fun ScreenScaffolded(
    icon: VectorAsset,
    setIcon: (VectorAsset) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "NightTime")
            })
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.background
            ) {
                bottomBar(icon, setIcon)
            }
        }
        ) {
        content()

    }
}







@Preview
@Composable
fun PreviewScreen() {
    NightTimeTheme {
        MainView("Calendario")

    }
}

@Preview
@Composable
fun PreviewBottomBar() {
    val (icon, setIcon) = remember { mutableStateOf(Icons.Default.LocalBar) }
    NavButtons(icon, setIcon, asset = Icons.Default.LocalBar)
}

//Wights
/*
Row() {
    Box(
        Modifier.weight(1f),
        backgroundColor = Color.Blue) {
        Text(text = "Weight = 1", color = Color.White)
    }
    Box(
        Modifier.weight(2f),
        backgroundColor = Color.Yellow
    ) {
        Text(text = "Weight = 2")
    }
}
*/
