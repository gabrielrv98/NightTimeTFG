package com.esei.grvidal.nighttime

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview

import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.esei.grvidal.nighttime.chatutil.ChatConversationPage
import com.esei.grvidal.nighttime.data.CityDao
import com.esei.grvidal.nighttime.data.ProfileViewModel
import com.esei.grvidal.nighttime.data.User
import com.esei.grvidal.nighttime.pages.*

import com.esei.grvidal.nighttime.ui.NightTimeTheme
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    //val chat by viewModels<ChatViewModel>()
    //todo finish

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user: ProfileViewModel by viewModels()
        setContent {
            NightTimeTheme {
                MainScreen(
                    //chat,
                    //onAddItem = chat::addItem,
                )
            }
        }
    }
}

/**
 * MainScreen with the function that will allow it to manage the navigation system
 */
@Composable
private fun MainScreen(
    //chat : ChatViewModel,
    //onAddItem: (Message) -> Unit,
 ) {
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

    val (cityDialog, setCityDialog) = remember { mutableStateOf(false) }
    val (cityId, setCityId) = remember {
        mutableStateOf(CityDao().getAllCities()[0])
    }//todo cambiar, inicia siempre en ourense, deberia ser con sharedPreferences o algo asi


    NavHost(navController, startDestination = BottomNavigationScreens.Calendar.route) {
        composable(BottomNavigationScreens.Calendar.route) {
            ScreenScaffolded(
                topBar = {
                    TopBarConstructor(
                        setCityDialog = setCityDialog,
                        nameCity = cityId.name
                    )
                },
                bottomBar = { bottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                CityDialogConstructor(cityDialog, setCityDialog, setCityId)
                CalendarPage(cityId = cityId)
            }
        }

        composable(BottomNavigationScreens.Bar.route) {
            ScreenScaffolded(
                topBar = {
                    TopBarConstructor(
                        setCityDialog = setCityDialog,
                        nameCity = cityId.name
                    )
                },
                bottomBar = { bottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                CityDialogConstructor(cityDialog, setCityDialog, setCityId)
                BarPage(cityId = cityId, navController)
            }

        }
        composable(
            NavigationScreens.BarDetails.route + "/{barId}",
            arguments = listOf(navArgument("barId") { type = NavType.IntType })
        ) { backStackEntry ->

            ScreenScaffolded(
                modifier = Modifier
            ) {
                BarDetails(backStackEntry.arguments?.getInt("barId"), navController)
            }

        }

        composable(BottomNavigationScreens.Friends.route) {

            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) },
                bottomBar = { bottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                FriendsPageView(navController)
            }
        }

        composable(
            NavigationScreens.ChatConversation.route + "/{ChatId}",
            arguments = listOf(navArgument("ChatId") { type = NavType.IntType })
        ) { backStackEntry ->
                ChatConversationPage(navController, backStackEntry.arguments?.getInt("ChatId"))

            //ChatConversationPage(navController, backStackEntry.arguments?.getInt("ChatId"))

        }

        composable(
            BottomNavigationScreens.Profile.route
        ) {
            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) },
                bottomBar = { bottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                ProfilePageView(navController, User("me").id)//todo is hardcoded
            }
        }

        composable(
            BottomNavigationScreens.Profile.route + "/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) {backStackEntry ->

            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) },
                bottomBar = { bottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                ProfilePageView(navController, backStackEntry.arguments?.getInt("userId"))
            }
        }

        composable(
            NavigationScreens.ProfileEditor.route ) {

            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) },
                bottomBar = { bottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                ProfileEditorPage(navController)
            }
        }
    }
}

fun NavHostController.navigateWithId(route: String, id: Int) {

    val navString = StringBuilder()
        .append(route)
        .append("/")
        .append(id)
        .toString()
    this.navigate(navString)
}
@Composable
fun bottomBarNavConstructor(
    navController: NavHostController,
    bottomNavigationItems: List<BottomNavigationScreens>
) {
    BottomBarNavigation {
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

@Preview("Main Page")
@Composable
fun PreviewScreen() {
    NightTimeTheme {
        ScreenScaffolded{}


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
