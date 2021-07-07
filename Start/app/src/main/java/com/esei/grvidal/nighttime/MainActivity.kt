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
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.chatutil.ChatConversationInit
import com.esei.grvidal.nighttime.viewmodels.*
import com.esei.grvidal.nighttime.datastore.DataStoreManager
import com.esei.grvidal.nighttime.network.ChatListener
import com.esei.grvidal.nighttime.pages.*
import com.esei.grvidal.nighttime.pages.bar_pages.BarDetails
import com.esei.grvidal.nighttime.pages.bar_pages.BarPage
import com.esei.grvidal.nighttime.pages.login_pages.LoginArchitecture
import com.esei.grvidal.nighttime.pages.profile_pages.PERMISSION_CODE
import com.esei.grvidal.nighttime.pages.profile_pages.ProfileEditorPage
import com.esei.grvidal.nighttime.pages.profile_pages.ProfilePageView
import com.esei.grvidal.nighttime.pages.profile_pages.pickImageFromGallery
import com.esei.grvidal.nighttime.scaffold.*
import com.esei.grvidal.nighttime.ui.NightTimeTheme
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    /** Using kotlin delegate by viewModels returns an instance of ViewModel by lazy
     * so the object don't initialize until needed and if the Activity is destroyed and recreated afterwards
     * it will receive the same instance of ViewModel as it had previously
     * */
    private val userVM by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * This ViewModels need arguments in their constructors so we need to
         * use a Fabric to return a lazy initialization of the ViewModel
         *
         * [LoginViewModel] and [CityViewModel] constructor requires a DataStoreManager instance, so we use [ViewModelProvider] with a
         * Factory [LoginViewModelFactory] and [CityViewModelFactory] respectively to return a ViewModel by lazy
         */
        val loginVM = ViewModelProvider(
            this,
            LoginViewModelFactory(DataStoreManager.getInstance(this))
        ).get(LoginViewModel::class.java)

        val cityVM = ViewModelProvider(
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

                        userVM.setUserToken(loginVM.loggedUser)

                        Log.d(TAG, "onCreate: pulling MainScreen")
                        NightTimeApp(
                            loginVM,
                            userVM,
                            cityVM,
                            searchImage = { selectImageLauncher.launch("image/*") }
                        )

                    }

                    LoginState.NO_NETWORK -> {

                        if (loginVM.credentialsChecked) {

                            Log.d(TAG, "onCreate: pulling MainScreen")
                            NightTimeApp(
                                loginVM,
                                userVM,
                                cityVM,
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
                    pickImageFromGallery { selectImageLauncher.launch("image/*") }

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
private fun NightTimeApp(
    login: LoginViewModel,
    userVM: UserViewModel,
    cityVM: CityViewModel,
    searchImage: () -> Unit
) {

    val chatListener = remember {
        ChatListener(
            CoroutineScope(context = EmptyCoroutineContext),
            login.loggedUser
        )
    }

    val barVM: BarViewModel = viewModel()

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

                CalendarInit(userToken = login.loggedUser, cityId = cityVM.city.id)
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

                BarPage(
                    navController = navController,
                    barVM = barVM,
                    city = cityVM.city
                )
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
                    barVM = barVM,
                    barId = backStackEntry.arguments?.getInt("barId")?.toLong() ?: -1L,
                    navController = navController
                )
            }

        }

        composable(BottomNavigationScreens.FriendsNav.route) { //Friends

            // Dialog to add new friends
            val showDialog = remember { mutableStateOf(false) }


            ScreenScaffolded(
                topBar = {

                    TopBarConstructor(
                        buttonText = "",
                        icon = Icons.Default.PersonSearch,
                        action = { showDialog.value = true },
                    )
                },
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {

                FriendsInit(
                    navController = navController,
                    userToken = login.loggedUser,
                    flow = chatListener.events,
                    showDialog = showDialog
                )


            }
        }

        composable(
            NavigationScreens.ChatConversation.route + "/{ChatId}",  // Chat
            arguments = listOf(navArgument("ChatId") { type = NavType.IntType })
        ) { backStackEntry ->

            ChatConversationInit(
                navController = navController,
                flow = chatListener.events,
                friendshipId = backStackEntry.arguments?.getInt("ChatId")?.toLong() ?: -1L,
                userToken = login.loggedUser
            )

        }

        composable(
            BottomNavigationScreens.ProfileNav.route // Profile client user
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
                ProfilePageView(
                    navController = navController,
                    userId = login.loggedUser.id,
                    userVM = userVM
                )
            }
        }

        composable(
            BottomNavigationScreens.ProfileNav.route + "/{userId}", // Profile other user
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->

            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) },
                bottomBar = { BottomBarNavConstructor(navController, bottomNavigationItems) },
            ) {
                ProfilePageView(
                    navController = navController,
                    userId = backStackEntry.arguments?.getInt("userId")?.toLong() ?: -1L,
                    userVM = userVM
                )

            }
        }

        composable(
            NavigationScreens.ProfileEditor.route // Profile editor
        ) {

            ScreenScaffolded(
                topBar = { TopAppBar(title = { Text(text = stringResource(R.string.edit_profile)) }) },
                bottomBar = {},
            ) {
                ProfileEditorPage(
                    navController = navController,
                    searchImage = searchImage,
                    setLoginCredentials = login::setPassword,
                    user = userVM
                )
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