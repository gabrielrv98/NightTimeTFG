package com.esei.grvidal.nighttime

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.esei.grvidal.nighttime.chatutil.ChatConversationPage
import com.esei.grvidal.nighttime.data.*
import com.esei.grvidal.nighttime.datastore.DataStoreManager
import com.esei.grvidal.nighttime.pages.*

import com.esei.grvidal.nighttime.ui.NightTimeTheme
import java.lang.StringBuilder

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    //val chat by viewModels<ChatViewModel>()

    private lateinit var userToken: UserViewModel

    public val calendarData by viewModels<CalendarViewModel>()
    public val barData by viewModels<BarViewModel>()//How to avoid the init until the page is called
    //public val barData :BarViewModel by navGraphViewModels(R.id.nav_controller_view_tag)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "{tags: AssistLoggin} onCreate: userToken is going to be created")

        userToken = ViewModelProvider(
            this,
            UserViewModelFactory(DataStoreManager.getInstance(this))
        ).get(UserViewModel::class.java)

        userToken.doLogin()

/*
        if (userToken.networkState.value == NetworkState.ERROR)
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show() // network-State is null so its not ERROR
        else Toast.makeText(this, "All cool ${userToken.networkState.value?.name}", Toast.LENGTH_SHORT).show()

https://developer.android.com/codelabs/android-room-with-a-view-kotlin?hl=es#6
 */

        setContent {

            NightTimeTheme {

                when (userToken.loggingState) {
                    LoginState.LOADING -> {

                        Log.d(TAG, "{tags: AssistLoggin} onCreate: pulling LoadingPage")
                        LoadingScreen()

                    }

                    LoginState.NO_DATA_STORED -> {

                        Log.d(TAG, "{tags: AssistLoggin} onCreate: pulling LoggingPage")
                        LoginPage(userToken)

                    }
                    else -> {
                        Log.d(TAG, "{tags: AssistLoggin} onCreate: pulling MianScreen")
                        MainScreen(
                            userToken,
                            calendarData,
                            barData
                            //chat,
                            //onAddItem = chat::addItem,
                        )

                    }
                }

            }
        }
    }
}

/**
 * MainScreen with the function that will allow it to manage the navigation system
 */
@Composable
private fun MainScreen(
    user: UserViewModel,
    calendar: CalendarViewModel,
    bar: BarViewModel
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
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                CityDialogConstructor(cityDialog, setCityDialog, setCityId)
                CalendarPage(cityId = cityId, calendar)
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
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                CityDialogConstructor(cityDialog, setCityDialog, setCityId)
                BarPage(cityId = cityId, navController, bar)
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
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
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
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                ProfilePageView(navController, User("me").id, user)//todo is hardcoded
            }
        }

        composable(
            BottomNavigationScreens.Profile.route + "/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->

            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) },
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                ProfilePageView(navController, backStackEntry.arguments?.getInt("userId"), user)
            }
        }

        composable(
            NavigationScreens.ProfileEditor.route
        ) {

            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) },
                bottomBar = {},
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
fun BottomBarNavConstructor(
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
        ScreenScaffolded {}


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

/*
Concepto principal: cuando agregues un estado interno a un elemento que admite composición,
evalúa si debe conservarse luego de los cambios de configuración o las interrupciones como las llamadas telefónicas.

De ser así, usa savedInstanceState para almacenar el estado.

var expanded by savedInstanceState { false }

 */
