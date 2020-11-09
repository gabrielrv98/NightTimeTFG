package com.esei.grvidal.nighttime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


/**
 * Show the Calendar page, with the calendar on the top and the information below it
 */
@Composable
fun BarPageView() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {

        Divider(thickness = 1.dp,color = Color.Red)
        Divider(thickness = 1.dp,color = Color.Green)
        Divider(thickness = 1.dp,color = Color.Blue)

    }
}