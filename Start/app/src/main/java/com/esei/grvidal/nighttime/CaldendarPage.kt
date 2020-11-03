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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun CalendarPageView() {

    val modifier = Modifier.fillMaxWidth()
    val (dia, setDia) = remember{ mutableStateOf(Calendar.getInstance()) }

    Column( modifier = modifier.fillMaxWidth()
    ){

        Row( modifier = modifier.weight(1f),
            horizontalArrangement = Arrangement.Center){

            CalendarVidow()

        }

        //todo Eliminar este Divider es solo una referencia
        Divider(thickness = 2.dp, modifier = Modifier.fillMaxWidth())

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
fun CalendarVidow(){
    Surface(modifier = Modifier.padding(6.dp),
        border= BorderStroke(2.dp, MaterialTheme.colors.primary),
        shape = RoundedCornerShape(20)
    ){

        Text(text = "abc", modifier = Modifier.padding(12.dp))
    }
}

data class MyDay(val date: Calendar)