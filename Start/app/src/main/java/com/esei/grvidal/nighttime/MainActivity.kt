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
import androidx.compose.ui.res.stringResource
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
    //saving the sate of the NavButton selected selected

    val (icon, setIcon) = remember { mutableStateOf(NavButtonsIcon.Calendar.vectorAsset) }
    val text: String = when (icon) {
        NavButtonsIcon.Bar.vectorAsset -> stringResource(id = R.string.Bar_st)
        NavButtonsIcon.Calendar.vectorAsset -> stringResource(id = R.string.Calendario)
        NavButtonsIcon.Friends.vectorAsset -> stringResource(id = R.string.amigos)
        NavButtonsIcon.Chat.vectorAsset -> stringResource(id = R.string.chat)
        else -> stringResource(id = R.string.error)
    }
    ScreenScaffolded(icon, setIcon) {
        MainView(text = text)
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
                bottomBar(icon, setIcon)
        }
    ) {
        content()

    }
}

@Composable
fun MainView(text: String = "empty") {
    // A surface container using the 'background' color from the theme
    Surface(
        color = MaterialTheme.colors.background,
        elevation = 1.dp
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











@Preview ("Main Page")
@Composable
fun PreviewScreen() {
    NightTimeTheme {
        MainScreen()

    }
}



//Weights
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
