package com.esei.grvidal.nighttime

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.chatutil.ChatConversationPage
import com.esei.grvidal.nighttime.data.*
import com.esei.grvidal.nighttime.datastore.DataStoreManager
import com.esei.grvidal.nighttime.pages.*
import com.esei.grvidal.nighttime.scaffold.*
import com.esei.grvidal.nighttime.ui.NightTimeTheme


private const val TAG = "MainActivity"

// Iconos personales
// https://developer.android.com/studio/write/image-asset-studio?hl=es-419
class MainActivity : AppCompatActivity() {

    /** Using kotlin delegate by viewModels returns an instance of ViewModel by lazy
     * so the object don't initialize until needed and if the Activity is destroyed and recreated afterwards
     * it will receive the same instance of ViewModel as it had previously
     * */
    private val barVM by viewModels<BarViewModel>()
    private val calendarVM by viewModels<CalendarViewModel>()
    private val userVM by viewModels<UserViewModel>()
    private val chatVM by viewModels<ChatViewModel>()

    /** This ViewModels need arguments in their constructors so we need to
     * use a Fabric to return a lazy initialization of the ViewModel
     */
    private lateinit var loginVM: LoginViewModel
    private lateinit var cityVM: CityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "{tags: AssistLogging} onCreate: userToken is going to be created")

        /**
         * [LoginViewModel] and [CityViewModel] constructor requires a DataStoreManager instance, so we use [ViewModelProvider] with a
         * Factory [LoginViewModelFactory] and [CityViewModelFactory] respectively to return a ViewModel by lazy
         */
        loginVM = ViewModelProvider(
            this,
            LoginViewModelFactory(DataStoreManager.getInstance(this))
        ).get(LoginViewModel::class.java)

        cityVM = ViewModelProvider(
            this,
            CityViewModelFactory(DataStoreManager.getInstance(this))
        ).get(CityViewModel::class.java)

        setContent {

            NightTimeTheme {

                when (loginVM.loggingState) {
                    LoginState.LOADING -> {

                        Log.d(TAG, "onCreate: pulling LoadingPage")
                        LoadingScreen()

                    }

                    LoginState.NO_DATA_STORED -> {

                        Log.d(TAG, "onCreate: pulling LoginPage")
                        LoginArchitecture(
                            loginVM = loginVM,
                            userVM = userVM,
                            searchImage = { selectImageLauncher.launch("image/*") }
                        )

                    }
                    LoginState.REFUSED -> {
                        Log.d(TAG, "onCreate: pulling LoginPage with message")
                        LoginArchitecture(
                            loginVM = loginVM,
                            userVM = userVM,
                            messageError = stringResource(id = R.string.loginError),
                            searchImage = { selectImageLauncher.launch("image/*") }
                        )


                    }

                    LoginState.ACCEPTED -> {

                        calendarVM.setUserToken(loginVM.loggedUser)
                        userVM.setUserToken(loginVM.loggedUser)
                        chatVM.setUserToken(loginVM.loggedUser)

                        Log.d(TAG, "onCreate: pulling MainScreen")
                        MainScreen(
                            loginVM,
                            userVM,
                            cityVM,
                            calendarVM,
                            barVM,
                            chatVM,
                            searchImage = { selectImageLauncher.launch("image/*") }
                        )

                    }

                    LoginState.NO_NETWORK -> {

                        if (loginVM.credentialsChecked) {

                            Log.d(TAG, "onCreate: pulling MainScreen")
                            MainScreen(
                                loginVM,
                                userVM,
                                cityVM,
                                calendarVM,
                                barVM,
                                chatVM,
                                searchImage = { selectImageLauncher.launch("image/*") }
                            )
                        } else {
                            Log.d(
                                TAG,
                                "onCreate: pulling LoginPage, credentials ${loginVM.credentialsChecked}"
                            )
                            LoginArchitecture(
                                loginVM = loginVM,
                                userVM = userVM,
                                messageError = stringResource(id = R.string.serverIsDown),
                                searchImage = { selectImageLauncher.launch("image/*") }
                            )
                        }

                    }

                    LoginState.EXCEPTION -> {
                        ErrorPage("Unexpected error")
                    }
                }

            }
        }

    }

    /**
     * Invoke an Activity for result
     */
    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d(TAG, "settingNewURi:  uri = $uri")
            userVM.uriPhotoPicasso = uri
        }

    /**
     * Handle requested permission result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery{selectImageLauncher.launch("image/*")}

                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
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
    login: LoginViewModel,
    userVM: UserViewModel,
    cityVM: CityViewModel,
    calendarVM: CalendarViewModel,
    barVM: BarViewModel,
    chatVM : ChatViewModel,
    searchImage: () -> Unit
) {
/* Actual Navigation system
        https://proandroiddev.com/implement-bottom-bar-navigation-in-jetpack-compose-b530b1cd9ee2

Navigation with their own files ( no dependencies )
    https://medium.com/google-developer-experts/how-to-handle-navigation-in-jetpack-compose-a9ac47f7f975
 */
    val navController = rememberNavController()

    val bottomNavigationItems = listOf(
        BottomNavigationScreens.BarNav,
        BottomNavigationScreens.CalendarNav,
        BottomNavigationScreens.FriendsNav,
        BottomNavigationScreens.ProfileNav
    )

    Log.d(TAG, "MainScreen: Starting navigation graph")
    NavHost(navController, startDestination = BottomNavigationScreens.CalendarNav.route) {
        composable(BottomNavigationScreens.CalendarNav.route) {// Calendar
            ScreenScaffolded(
                topBar = {
                    TopBarConstructor(
                        buttonText = cityVM.city.name,
                        icon = Icons.Default.Search,
                        action = { cityVM.setDialog(true) },
                    )
                },
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                CityDialogConstructor(
                    cityDialog = cityVM.showDialog,
                    items = cityVM.allCities,
                    setCityDialog = cityVM::setDialog,
                    setCityId = cityVM::setCity
                )

                //Setting city to CalendarVM to make calls to api
                calendarVM.cityId = (cityVM.city.id)

                CalendarPage(calendarVM = calendarVM)
            }
        }

        composable(BottomNavigationScreens.BarNav.route) {// Bar
            ScreenScaffolded(
                topBar = {
                    TopBarConstructor(
                        action = { cityVM.setDialog(true) },
                        icon = Icons.Default.Search,
                        buttonText = cityVM.city.name
                    )
                },
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                CityDialogConstructor(
                    cityDialog = cityVM.showDialog,
                    items = cityVM.allCities,
                    setCityDialog = cityVM::setDialog,
                    setCityId = cityVM::setCity
                )
                barVM.city = cityVM.city
                BarPage(navController, barVM)
            }

        }
        composable(  // Bar details
            NavigationScreens.BarDetails.route + "/{barId}",
            arguments = listOf(navArgument("barId") { type = NavType.IntType })
        ) { backStackEntry ->
            //Sometimes Android would reorganize backStackEntry.arguments?.getLong  as an int and showing
            // W/Bundle: Key barId expected Long but value was a java.lang.Integer.  The default value 0 was returned.
            // So we send an int then transform it to long
            Log.d(TAG, "MainScreen: Pulling BarDetails")

            ScreenScaffolded(
                modifier = Modifier
            ) {
                BarDetails(
                    barVM,
                    backStackEntry.arguments?.getInt("barId")?.toLong() ?: -1L,
                    navController
                )
            }

        }

        composable(BottomNavigationScreens.FriendsNav.route) {

            ScreenScaffolded(
                topBar = {

                    TopBarConstructor(
                        buttonText = "",
                        icon = Icons.Default.PersonAdd,
                        action = { chatVM.setDialog(true) },
                    )
                     },
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                FriendsPage(navController,chatVM)
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
            BottomNavigationScreens.ProfileNav.route
        ) {
            ScreenScaffolded(
                topBar = {
                    TopBarConstructor(
                        buttonText = stringResource(id = R.string.logoff),
                        icon = vectorResource(id = R.drawable.ic_logout_24px),
                        action = login::logOffAndExit
                    )
                },
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                ProfilePageView(navController, login.loggedUser.id, userVM
                )
            }
        }

        composable(
            BottomNavigationScreens.ProfileNav.route + "/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->

            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) },
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                ProfilePageView(
                    navController,
                    backStackEntry.arguments?.getInt("userId")?.toLong() ?: -1L, userVM
                )

            }
        }

        composable(
            NavigationScreens.ProfileEditor.route
        ) {

            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text =  stringResource(R.string.edit_profile)) }) },
                bottomBar = {},
            ) {
                ProfileEditorPage(navController, searchImage, userVM,login::setPassword)
            }
        }
    }
}

fun NavHostController.navigateWithId(route: String, id: Long) {

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
