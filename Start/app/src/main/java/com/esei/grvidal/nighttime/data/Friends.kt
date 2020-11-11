package com.esei.grvidal.nighttime.data

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.pages.Bar
import com.esei.grvidal.nighttime.pages.BarChip
import com.esei.grvidal.nighttime.pages.BarDAO

//import androidx.compose.material.Text


@Composable
fun FriendsPageView() {

    val barList = BarDAO().bares

    LazyColumnFor(
        items = barList,
        modifier = Modifier.fillMaxSize()
            .padding(24.dp)
    ) {

        Row(){
            BarChip(it)
        }

        Divider(thickness = 1.dp,color = Color.Red)
        Divider(thickness = 1.dp,color = Color.Green)
        Divider(thickness = 1.dp,color = Color.Blue)

    }
}


@Composable
fun FriendChip(bar : Bar){
    Column(
        modifier = Modifier.padding(8.dp)
    ){

        Text(bar.name)
        Text(text = bar.description)
    }
}