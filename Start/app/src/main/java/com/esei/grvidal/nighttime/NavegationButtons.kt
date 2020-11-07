package com.esei.grvidal.nighttime

import androidx.compose.foundation.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview

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
                NavButtons(icon, setIcon)
            }
        }
    }


}


/**
 * Function that creates the navButtons in the right order
 *
 * @param icon Selected navButtonsIcon
 * @param setIcon setter of the selected navButtonsIcon
 */
@Composable
fun NavButtons(icon: NavButtonsIcon, setIcon: (NavButtonsIcon) -> Unit) {

    NavButtons(icon, setIcon, asset = NavButtonsIcon.Bar)
    NavButtons(icon, setIcon, asset = NavButtonsIcon.Calendar)
    NavButtons(icon, setIcon, asset = NavButtonsIcon.Friends)
    NavButtons(icon, setIcon, asset = NavButtonsIcon.Chat)
}

//TODO Nav to the next View
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
        backgroundColor = Color.Transparent,
        border = null,
        elevation = 0.dp,
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
    val (icon, setIcon) = remember { mutableStateOf(NavButtonsIcon.Bar) }

    bottomBar(icon, setIcon)

}