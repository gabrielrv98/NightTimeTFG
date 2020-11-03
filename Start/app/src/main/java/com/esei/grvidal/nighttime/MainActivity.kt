package com.esei.grvidal.nighttime

//import androidx.compose.foundation.AmbientContentColor


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview

import com.esei.grvidal.nighttime.ui.NightTimeTheme
import androidx.compose.ui.platform.setContent
import androidx.compose.foundation.Icon as ComposeFoundationIcon
import androidx.compose.material.Button as Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NightTimeTheme {
                ScreenScaffolded()
            }
        }
    }
}

@Composable
fun ScreenScaffolded(){

    val (icon, setIcon) = remember { mutableStateOf(Icons.Default.LocalBar) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text="NightTime")
            })
        },


        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.background
            ) {
                bottomBar(icon, setIcon)
            }
        },


    ){
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colors.background
        ) {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.Center ,
                horizontalAlignment  = Alignment.CenterHorizontally){
                Row{
                    Text(text = "Night Time main page")
                }
                Row{
                    if (icon == Icons.Default.LocalBar)
                        Text(text = ContextAmbient.current.getString(R.string.Bar_st))
                    else if(icon == Icons.Default.Today)
                        Text(text = ContextAmbient.current.getString(R.string.Calendario))
                    else if(icon == Icons.Default.People)
                        Text(text = ContextAmbient.current.getString(R.string.amigos))
                    else if(icon == Icons.Default.AddComment)
                        Text(text = ContextAmbient.current.getString(R.string.chat))
                }
            }


        }

    }
}


@Composable
fun bottomBar(icon: VectorAsset, setIcon: (VectorAsset) -> Unit){

    Column{

        //Row with a divider line
        Row{
            Divider(color = Color.Black, thickness = 1.dp)
        }

        //Navigation Buttons
        Row( modifier = Modifier.padding(top = 6.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically

        ){
            NavButtons(icon,setIcon )


        }
    }
}


//Stateful composable with the logic
@Composable
fun NavButtons(icon: VectorAsset, setIcon: (VectorAsset) -> Unit){



    //da forma
    NavButtons(icon,setIcon, asset = Icons.Default.LocalBar)

    NavButtons(icon,setIcon, asset = Icons.Default.Today)

    NavButtons(icon,setIcon, asset = Icons.Default.People )

    NavButtons(icon,setIcon, asset = Icons.Default.AddComment)

}

@Composable
fun NavButtons(
    icon: VectorAsset,
    onIconChange: (VectorAsset) -> Unit,
    asset: VectorAsset,
    modifier: Modifier = Modifier
) {
    SelectableIconButton(
                icon = asset,
                onIconSelected = { //TODO Navegar a la sigueinte pestaña
                    onIconChange(asset) },
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
    ){
        Column {
            androidx.compose.foundation.Icon(icon, tint = tint)

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





@Preview
@Composable
fun PreviewScreen() {
    ScreenScaffolded()
}

@Preview
@Composable
fun PreviewBottomBar(){
    val (icon, setIcon) = remember { mutableStateOf(Icons.Default.LocalBar) }
    bottomBar(icon, setIcon)
}