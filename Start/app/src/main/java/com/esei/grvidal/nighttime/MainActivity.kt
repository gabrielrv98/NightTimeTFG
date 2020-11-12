package com.esei.grvidal.nighttime

//import androidx.compose.foundation.AmbientContentColor


import android.os.Bundle
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
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
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.ui.tooling.preview.Preview

import com.esei.grvidal.nighttime.ui.NightTimeTheme
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.esei.grvidal.nighttime.data.City
import com.esei.grvidal.nighttime.data.CityDao
import com.esei.grvidal.nighttime.data.User
//import com.esei.grvidal.nighttime.navigation.Actions
//import com.esei.grvidal.nighttime.navigation.Destination
//import com.esei.grvidal.nighttime.navigation.Navigator
import com.esei.grvidal.nighttime.pages.BarPageView
import com.esei.grvidal.nighttime.pages.CalendarPageView
import java.util.*


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
//https://proandroiddev.com/implement-bottom-bar-navigation-in-jetpack-compose-b530b1cd9ee2
    val navController = rememberNavController()

    val bottomNavigationItems = listOf(
        BottomNavigationScreens.Bar,
        BottomNavigationScreens.Calendar,
        BottomNavigationScreens.Friends,
        BottomNavigationScreens.Profile
    )
/*
https://medium.com/google-developer-experts/how-to-handle-navigation-in-jetpack-compose-a9ac47f7f975
    val navigator: Navigator<Destination> = rememberSavedInstanceState(
        saver = Navigator.saver(backDispatcher)
    ) {
        Navigator(Destination.Calendar, backDispatcher)
    }
    val actions = remember(navigator) { Actions(navigator) }

    Crossfade(navigator.current) { destination ->
        when (destination) {
            Destination.Bar -> Bar
            Destination.Courses -> Courses(actions.selectCourse)
            is Destination.Course -> CourseDetails(
                destination.courseId,
                actions.selectCourse,
                actions.upPress
            )
        }
    }

 */


        //saving the sate of the NavButton selected selected
    val (icon, setIcon) = remember { mutableStateOf(NavButtonsIcon.Bar) }

    ScreenScaffolded(navController, bottomNavigationItems) {
        val city = it


        NavHost(navController, startDestination = BottomNavigationScreens.Calendar.route) {
            composable(BottomNavigationScreens.Calendar.route) {
                CalendarPageView(cityId = city )
            }
            composable(BottomNavigationScreens.Bar.route) {
                BarPageView(cityId = city)
            }
             
        }

/*
        val text: String = when (icon) {
            NavButtonsIcon.Bar -> stringResource(id = R.string.Bar_st)
            NavButtonsIcon.Calendar -> stringResource(id = R.string.Calendario)
            NavButtonsIcon.Friends -> stringResource(id = R.string.amigos)
            NavButtonsIcon.Chat -> stringResource(id = R.string.chat)
        }


        when (icon) {
            NavButtonsIcon.Calendar -> CalendarPageView(it, backDispatcher)
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

 */
    }
}












@Preview("Main Page")
@Composable
fun PreviewScreen() {
    NightTimeTheme {
        //MainScreen()

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
