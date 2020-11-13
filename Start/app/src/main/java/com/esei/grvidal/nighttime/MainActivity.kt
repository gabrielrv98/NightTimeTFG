package com.esei.grvidal.nighttime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview

import androidx.compose.ui.platform.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController 
import com.esei.grvidal.nighttime.pages.*

import com.esei.grvidal.nighttime.ui.NightTimeTheme


class MainActivity : AppCompatActivity() {

    //private val user by viewModels<User> { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NightTimeTheme {
                MainScreen()
            }
        }
    }
}

/**
 * MainScreen with the function that will allow it to manage the navigation system
 */
@Composable
private fun MainScreen() {
/* Actual Navigation system
        https://proandroiddev.com/implement-bottom-bar-navigation-in-jetpack-compose-b530b1cd9ee2

Navigation with their own files ( no dependencies )
    https://medium.com/google-developer-experts/how-to-handle-navigation-in-jetpack-compose-a9ac47f7f975
 */
    val navController = rememberNavController()

    val bottomNavigationItems = listOf(
        BottomNavigationScreens.Bar,
        BottomNavigationScreens.Calendar,
        BottomNavigationScreens.Friends,
        BottomNavigationScreens.Profile
    )

    ScreenScaffolded(navController, bottomNavigationItems) {
        val city = it


        NavHost(navController, startDestination = BottomNavigationScreens.Calendar.route) {
            composable(BottomNavigationScreens.Calendar.route) {
                CalendarPageView(cityId = city)
            }
            composable(BottomNavigationScreens.Bar.route) {
                BarPageView(cityId = city, navController)
            }
            composable(NavigationScreens.BarDetails.route){
                BarDetails()
            }

            composable(BottomNavigationScreens.Friends.route) {
                FriendsPageView()
            }

            composable(BottomNavigationScreens.Profile.route) {
                ProfilePageView()
            }

        }
    }

}


@Preview("Main Page")
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
