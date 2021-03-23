package com.esei.grvidal.nighttime.scaffold

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.ButtonConstants.defaultButtonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.R

/**
 * Bottom navigation icons with their route to the view
 *
 * @param route String that represents the route of the View
 * @param resourceId String from resources used to backtrack the users view
 * @param icon VectorAsset of the represented icon
 */
sealed class BottomNavigationScreens(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: VectorAsset
) {
    object CalendarNav :
        BottomNavigationScreens("Calendar", R.string.calendar_route, Icons.Default.Today)

    object BarNav :
        BottomNavigationScreens("Bar", R.string.bar_route, Icons.Default.LocalBar)

    object FriendsNav :
        BottomNavigationScreens("Friends", R.string.friends_route, Icons.Default.People)

    object ProfileNav :
        BottomNavigationScreens("Profile", R.string.profile_route, Icons.Default.Person)
}

/**
 * Sub screens of the App
 *
 * @param route String that represents the route of the View
 * @param resourceId String from resources used to backtrack the users view
 */
sealed class NavigationScreens(
    val route: String,
    @StringRes val resourceId: Int
) {
    object LogginPage:
            NavigationScreens("Logging",R.string.login)
    object RegisterPage:
        NavigationScreens("Register",R.string.register)
    object BarDetails :
        NavigationScreens("BarDetails", R.string.barDetails_route)
    object ChatConversation :
        NavigationScreens("ChatConversation", R.string.ChatConversation)
    object ProfileEditor :
        NavigationScreens("ProfileEditor", R.string.ProfileEditor)

}


/**
 *  Formatted view of the BottomBar
 *
 *  @param content Content of the bottom Row
 *
 */
@Composable
fun BottomBarNavigation(
    content : @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colors.background
    ) {
        Column {
            Divider(
                modifier = Modifier.padding(3.dp),
                color = MaterialTheme.colors.onSurface,
                thickness = 1.dp
            )

            //Navigation Buttons
            Row(
                modifier = Modifier.padding(top = 6.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
    }

}


/**
 * Method to recover the navigation's backtrack and return it as a string
 *
 * @param navController controller to be analyzed
 */
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.arguments?.getString(KEY_ROUTE)
}


/**
 * Formatted Icons with the right color and the underline if they are selected
 *
 * @param icon VectorAsset of the own icon
 * @param onIconSelected setter of the selected navButtonsIcon
 * @param isSelected boolean that is true if the icon is selected
 * @param modifier Modifier
 *
 */
@Composable
fun SelectableIconButton(
    icon: VectorAsset,
    onIconSelected: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val tint = if (isSelected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    }
    Button(
        onClick = { onIconSelected() },
        shape = CircleShape,
        colors = defaultButtonColors(
            MaterialTheme.colors.background
        ),
        border = null,
        elevation = null,
        modifier = modifier
    ) {
        Column {
            Icon(icon, tint = tint)

            if (isSelected) {
                Box(
                    Modifier
                        .padding(top = 3.dp)
                        .preferredWidth(icon.defaultWidth)
                        .preferredHeight(1.dp)
                        .background(tint)
                )
            } else {
                Spacer(modifier = Modifier.preferredHeight(4.dp))
            }
        }
    }
}

@Preview("bottomBar")
@Composable
fun bottomBarPreview() {
    SelectableIconButton(Icons.Default.LocalBar, {}, true)

}