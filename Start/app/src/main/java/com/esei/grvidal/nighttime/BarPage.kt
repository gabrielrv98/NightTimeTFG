package com.esei.grvidal.nighttime

import android.accounts.AuthenticatorDescription
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.ui.NightTimeTheme


class BarDAO{
    val bares : List<Bar> = listOf(
        Bar(0,"Lazaros", "un pub para gente pija"),
        Bar(1,"Lokal","Un lokal para escuchar rock"),
        Bar(2,"Urbe","Las mejores aspiradoras"),
        Bar(3,"Patio Andaluz","Otro gran local pero con una descripcion algo larga de mas que se acortara"),
        Bar(4,"Mil petalos","Chicas siempre listas para darlo todo")
    )
}

data class Bar(val id: Int, val name: String, val description: String)

/**
 * Show the Calendar page, with the calendar on the top and the information below it
 */
@Composable
fun BarPageView() {

    val barList = BarDAO().bares

    LazyColumnFor(
        items = barList,
        modifier = Modifier.fillMaxSize()
    ) {

        Row(){
            BarChip(it)
        }

        Divider(thickness = 1.dp,color = Color.Red)
        Divider(thickness = 1.dp,color = Color.Green)
        Divider(thickness = 1.dp,color = Color.Blue)

    }
}


@Composable fun BarChip(bar : Bar){
    Column{

        Text(text = bar.name)
        Text(text = bar.description)
    }
}

@Preview ("BarPage")
@Composable
fun BarPreview(){
    NightTimeTheme {
        BarPageView()
    }
}