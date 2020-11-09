package com.esei.grvidal.nighttime

//import androidx.compose.foundation.AmbientContentColor


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.ui.tooling.preview.Preview

import com.esei.grvidal.nighttime.ui.NightTimeTheme
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


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

//todo add Change of City


@Composable
fun MainScreen() {

    //saving the sate of the NavButton selected selected
    val (icon, setIcon) = remember { mutableStateOf(NavButtonsIcon.Calendar) }

    ScreenScaffolded(icon, setIcon) {
        MainView(icon)
    }
}

@Composable
fun ScreenScaffolded(
    icon: NavButtonsIcon,
    setIcon: (NavButtonsIcon) -> Unit,
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
        val fillMaxModifier = Modifier.fillMaxSize()
        Surface(
            modifier = fillMaxModifier.padding(bottom = 57.dp),//TODO Bottom padding of the size of the bottomBar
            color = MaterialTheme.colors.background
        ){
            Column(
                modifier = fillMaxModifier
            ){
                content()
            }

        }


    }
}

@Composable
fun MainView(selectedIcon: NavButtonsIcon) {
    // A surface container using the 'background' color from the theme
    Surface(
        color = MaterialTheme.colors.background,
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) {
            val text: String = when (selectedIcon) {
                NavButtonsIcon.Bar -> stringResource(id = R.string.Bar_st)
                NavButtonsIcon.Calendar -> stringResource(id = R.string.Calendario)
                NavButtonsIcon.Friends -> stringResource(id = R.string.amigos)
                NavButtonsIcon.Chat -> stringResource(id = R.string.chat)
            }
            if (text == "Calendario")
                CalendarPageView()
            else if (text == "Bar")
                BarPageView()
            else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
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
