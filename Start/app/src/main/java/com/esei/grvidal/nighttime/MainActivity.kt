package com.esei.grvidal.nighttime

//import androidx.compose.foundation.AmbientContentColor


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.twotone.LocalBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.ui.NightTimeTheme


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
                bottomBar()
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
                Row(){
                    Text(text = "Night Time main page")
                }
                Row{
                    Text(text = ContextAmbient.current.getString(R.string.Bar_st))
                }
            }


        }

    }
}


@Composable
fun bottomBar(){

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
            //NavButtons(modifier = Modifier)
            val buttonModifier = Modifier.padding(horizontal = 6.dp)

            Column{
                Button(  modifier = buttonModifier,
                    onClick = {}
                ){
                    Icon(asset = Icons.Default.LocalBar)
                }
            }
            Column{
                Button( modifier = buttonModifier,
                    onClick = {}
                ){
                    Icon(asset = Icons.Default.Today)
                }
            }
            Column{
                Button( modifier = buttonModifier,
                    onClick = {}
                ){
                    Icon(asset = Icons.Default.People)
                }
            }
            Column{
                Button( modifier = buttonModifier,
                    onClick = {}
                ){
                    Icon(asset = Icons.TwoTone.LocalBar)
                }
            }

        }
    }
}

@Composable
fun NavButtons(modifier : Modifier = Modifier){
    val buttonModifier = modifier.padding(horizontal = 6.dp)

    Column{
        Button(  modifier = buttonModifier,
            onClick = {}
        ){
            Icon(asset = Icons.Default.LocalBar)
        }
    }
    Column{
        Button( modifier = buttonModifier,
            onClick = {}
        ){
            Icon(asset = Icons.Default.Today)
        }
    }
    Column{
        Button( modifier = buttonModifier,
            onClick = {}
        ){
            Icon(asset = Icons.Default.People)
        }
    }
    Column{
        Button( modifier = buttonModifier,
            onClick = {}
        ){
            Icon(asset = Icons.TwoTone.LocalBar)
        }
    }



/*
            Surface(
                modifier = Modifier.padding(top = 0.dp)
                    .drawShadow(elevation = 2.dp, shape = CircleShape)

                    .clickable(onClick = {}),
                color = MaterialTheme.colors.primary,
                elevation = 5.dp,
                shape = RectangleShape,

                ){
                Text(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
                    text = "Bares2"
                )


            }
*/
}

@Preview
@Composable
fun PreviewScreen() {
    ScreenScaffolded()
}

@Preview
@Composable
fun PreviewBottomBar(){
    bottomBar()
}