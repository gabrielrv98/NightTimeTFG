package com.esei.grvidal.nighttime

//import androidx.compose.foundation.AmbientContentColor


import android.os.Bundle
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.ui.tooling.preview.Preview

import com.esei.grvidal.nighttime.ui.NightTimeTheme
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.data.City
import com.esei.grvidal.nighttime.data.CityDao
import com.esei.grvidal.nighttime.data.User
import com.esei.grvidal.nighttime.pages.BarPageView
import com.esei.grvidal.nighttime.pages.CalendarPageView
import java.util.*
import kotlin.coroutines.coroutineContext


class MainActivity : AppCompatActivity() {

    //private val user by viewModels<User> { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NightTimeTheme {
                MainScreen(backDispatcher = onBackPressedDispatcher)
            }
        }
    }
}




@Composable
fun MainScreen(backDispatcher: OnBackPressedDispatcher) {

    //saving the sate of the NavButton selected selected
    val (icon, setIcon) = remember { mutableStateOf(NavButtonsIcon.Bar) }

    ScreenScaffolded(icon, setIcon) {

        val text: String = when (icon) {
            NavButtonsIcon.Bar -> stringResource(id = R.string.Bar_st)
            NavButtonsIcon.Calendar -> stringResource(id = R.string.Calendario)
            NavButtonsIcon.Friends -> stringResource(id = R.string.amigos)
            NavButtonsIcon.Chat -> stringResource(id = R.string.chat)
        }


        when (icon) {
            NavButtonsIcon.Calendar -> CalendarPageView(it)
            NavButtonsIcon.Bar -> BarPageView(it)
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
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












@Preview("Main Page")
@Composable
fun PreviewScreen() {
    NightTimeTheme {
        //MainScreen(backDispatcher = onBackPressedDispatcher)

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
