package com.esei.grvidal.nighttime

import androidx.compose.foundation.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.util.*

//todo eliminar
data class NavButton(
    val icon: NavButtonsIcon,
    val id: UUID = UUID.randomUUID()
)

enum class NavButtonsIcon (val vectorAsset: VectorAsset) {
    Bar(Icons.Default.LocalBar),
    Calendar(Icons.Default.Today),
    Friends(Icons.Default.People),
    Chat(Icons.Default.AddComment)
}


@Composable
fun bottomBar(icon: VectorAsset, setIcon: (VectorAsset) -> Unit) {

    Column {

        //Row with a divider line
        Row( horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(3.dp)
        ) {

            Surface(
                modifier = Modifier.fillMaxWidth()
                    .preferredHeight(1.dp),
                color = MaterialTheme.colors.onSurface,
                shape = RoundedCornerShape(25),
                elevation = 1.dp
            ){

            }
            //Divider(color = MaterialTheme.colors.onSurface, thickness = 1.dp)
        }

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


//Stateful composable with the logic
@Composable
fun NavButtons(icon: VectorAsset, setIcon: (VectorAsset) -> Unit) {


    //da forma
    NavButtons(icon, setIcon, asset = NavButtonsIcon.Bar.vectorAsset)

    NavButtons(icon, setIcon, asset = NavButtonsIcon.Calendar.vectorAsset)

    NavButtons(icon, setIcon, asset = NavButtonsIcon.Friends.vectorAsset)

    NavButtons(icon, setIcon, asset = NavButtonsIcon.Chat.vectorAsset)

}

//TODO Navegar a la sigueinte pestaña
@Composable
fun NavButtons(
    icon: VectorAsset,
    onIconChange: (VectorAsset) -> Unit,
    asset: VectorAsset
) {
    SelectableIconButton(
        icon = asset,
        onIconSelected = {
            onIconChange(asset)
        },
        isSelected = icon == asset
    )
}

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

@Preview( "bottomBar")
@Composable
fun bottomBarPreview(){
    val (icon, setIcon) = remember { mutableStateOf(NavButtonsIcon.Bar.vectorAsset) }

    bottomBar(icon, setIcon)

}