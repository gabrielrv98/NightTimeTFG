package com.esei.grvidal.nighttime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.google.android.material.chip.Chip
import java.util.*

@Composable
fun CalendarPageView() {

    val modifier = Modifier.fillMaxWidth()
    val (dia, setDia) = remember{ mutableStateOf(Calendar.getInstance()) }

    Column( modifier = modifier
    ){

        Row( modifier = modifier.weight(1.2f),
            horizontalArrangement = Arrangement.Center){


            CalendarWindow()

        }

        //todo Eliminar este Divider es solo una referencia
        Divider(thickness = 2.dp, modifier = modifier)

        Row( modifier = modifier.weight(1f)){
            Surface(modifier = Modifier.padding(6.dp),
                border= BorderStroke(2.dp, MaterialTheme.colors.primary)
            ){
                Text(text = "abc", modifier = Modifier.padding(12.dp))
            }
        }

    }

}

@Composable
fun CalendarWindow(){

    Surface(modifier = Modifier.padding(6.dp),
        border= BorderStroke(2.dp, MaterialTheme.colors.primary),
        shape = RoundedCornerShape(20.dp)
    ){
        Box( modifier = Modifier.padding(12.dp),
        alignment = Alignment.Center) {

            val myModifier = Modifier.padding(12.dp)

            Column {
                Row {
                    Column ( modifier = myModifier){
                        Text(text = "1.1")
                    }
                    Column ( modifier = myModifier){
                        Text(text = "1.2")
                    }
                    Column ( modifier = myModifier){
                        Text(text = "1.3")
                    }
                }
                Row {
                    Column ( modifier = myModifier){
                        Text(text = "2.1")
                    }
                    Column ( modifier = myModifier){
                        Text(text = "2.2")
                    }
                    Column ( modifier = myModifier){
                        Text(text = "2.3")
                    }
                }
                Row {
                    Column ( modifier = myModifier){
                        Text(text = "3.1")
                    }
                    Column ( modifier = myModifier){
                        Text(text = "3.2")
                    }
                    Column ( modifier = myModifier){
                        Text(text = "3.3")
                    }
                }
            }
        }
    }
}

@Preview( "CalendarWindow")
@Composable
fun CalendarWindowPreview(){
    CalendarWindow()
}

@Preview( "Calendar")
@Composable
fun CalendarPreview(){
    CalendarPageView()
}