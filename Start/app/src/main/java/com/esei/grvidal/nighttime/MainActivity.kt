package com.esei.grvidal.nighttime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview

import androidx.compose.ui.platform.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.*
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

    ScreenScaffolded(
        bottomBar = {
            bottomBarNavigation {
                val currentRoute = currentRoute(navController)
                bottomNavigationItems.forEach { screen ->
                    SelectableIconButton(
                        icon = screen.icon,
                        isSelected = currentRoute == screen.route,
                        onIconSelected = {
                            // This is the equivalent to popUpTo the start destination
                            navController.popBackStack(navController.graph.startDestination, false)

                            // This if check gives us a "singleTop" behavior where we do not create a
                            // second instance of the composable if we are already on that destination
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route)
                            }
                        }
                    )
                }
            }
        }
    ) {
        val city = it


        NavHost(navController, startDestination = BottomNavigationScreens.Calendar.route) {
            composable(BottomNavigationScreens.Calendar.route) {
                CalendarPage(cityId = city)
            }
            composable(BottomNavigationScreens.Bar.route) {
                BarPage(cityId = city, navController)
            }
            composable(
                NavigationScreens.BarDetails.route + "/{barId}",
                arguments = listOf(navArgument("barId") { type = NavType.IntType })
            ) { backStackEntry ->
                BarDetails(backStackEntry.arguments?.getInt("barId"))
            }

            composable(BottomNavigationScreens.Friends.route) {
                FriendsPageView(navController)
            }

            composable(BottomNavigationScreens.Profile.route) {
                ProfilePageView(navController)
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
