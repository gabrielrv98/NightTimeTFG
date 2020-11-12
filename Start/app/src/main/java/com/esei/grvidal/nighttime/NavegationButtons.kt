package com.esei.grvidal.nighttime

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.ButtonConstants.defaultButtonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.ui.tooling.preview.Preview


sealed class BottomNavigationScreens(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: VectorAsset
) {
    object Calendar :
        BottomNavigationScreens("Calendar", R.string.calendar_route, Icons.Default.Today)

    object Bar : BottomNavigationScreens("Bar", R.string.bar_route, Icons.Default.LocalBar)
    object Friends :
        BottomNavigationScreens("Friends", R.string.friends_route, Icons.Default.People)

    object Profile :
        BottomNavigationScreens("Profile", R.string.profile_route, Icons.Default.Person)
}

/**
 * Enum of vectorAsset of the navButtons
 */
enum class NavButtonsIcon(val vectorAsset: VectorAsset) {
    Bar(Icons.Default.LocalBar),
    Calendar(Icons.Default.Today),
    Friends(Icons.Default.People),
    Chat(Icons.Default.AddComment)
}

/**
 * NavigationBottomBar with a divider and the selectable icons
 *
 * @param icon selected enum of the NavButtonIcon
 * @param setIcon setter of the selected NavButtonIcon
 */
@Composable
fun bottomBar(icon: NavButtonsIcon, setIcon: (NavButtonsIcon) -> Unit) {


}

@Composable
fun bottomBarNavigation(
    navController: NavHostController,
    items: List<BottomNavigationScreens>
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

                val currentRoute = currentRoute(navController)
                items.forEach {screen ->
                    SelectableIconButton(
                        icon = screen.icon,
                        isSelected = currentRoute == screen.route,
                        onIconSelected = {
                            // This if check gives us a "singleTop" behavior where we do not create a
                            // second instance of the composable if we are already on that destination
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route)
                            }
                        }
                    )
                }



                //NavButtons(icon, setIcon)
            }
        }
    }
/*
    val currentRoute = currentRoute(navController)
    items.forEach { screen ->
        BottomNavigationItem(
            icon = { Icon(screen.icon) },
            label = { Text(stringResource(id = screen.resourceId)) },
            selected = currentRoute == screen.route,
            alwaysShowLabels = false, // This hides the title for the unselected items
            onClick = {
                // This if check gives us a "singleTop" behavior where we do not create a
                // second instance of the composable if we are already on that destination
                if (currentRoute != screen.route) {
                    navController.navigate(screen.route)
                }
            }
        )
    }

 */

}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.arguments?.getString(KEY_ROUTE)
}

/**
 * Formatted Icons
 *
 * @param icon Selected navButtonsIcon
 * @param onIconChange setter of the selected navButtonsIcon
 * @param asset enum of the NavButtonsIcon
 */
@Composable
fun NavButtons(
    icon: NavButtonsIcon,
    onIconChange: (NavButtonsIcon) -> Unit,
    asset: NavButtonsIcon
) {
    SelectableIconButton(
        icon = asset.vectorAsset,
        onIconSelected = {
            onIconChange(asset)
        },
        isSelected = icon == asset
    )
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
        //backgroundColor = Color.Transparent,
        colors = defaultButtonColors(
            MaterialTheme.colors.background
        ),
        border = null,
        elevation = null,
        modifier = modifier
            .background(Color.Transparent)
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
    val (icon, setIcon) = remember { mutableStateOf(NavButtonsIcon.Bar) }

    bottomBar(icon, setIcon)

}