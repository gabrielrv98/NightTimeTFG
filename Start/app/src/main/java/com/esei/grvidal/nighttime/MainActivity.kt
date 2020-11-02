package com.esei.grvidal.nighttime

//import androidx.compose.foundation.AmbientContentColor


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.graphics.RectangleShape
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
            BottomAppBar(

            ) {
                bottomBar()
            }
        },
        contentColor = MaterialTheme.colors.onSurface

    ){
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colors.background
        ) {
            Column(
                Modifier.fillMaxWidth().fillMaxHeight(),
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
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(bottom = 5.dp)
            .fillMaxWidth(1f)
    ){

        Button(modifier = Modifier.padding(horizontal = 20.dp),
            onClick = {}
        ){
            Text(text = "Bares1")
        }

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
    }
}

@Preview
@Composable
fun PreviewFirstTry(){
    ScreenScaffolded()
}