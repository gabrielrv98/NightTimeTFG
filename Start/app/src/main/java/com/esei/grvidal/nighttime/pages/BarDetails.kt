package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BarDetails(barId: Int?) {
    if (barId == null) {
        errorComposable()
    } else {
        ShowDetails(BarDAO().bares.get(barId))
        Text("Estos son los detalles de $barId")
    }
}

@Composable
fun errorComposable() {
    Text(
        text = "Ha habido un error con el bar seleccionado",
        color = MaterialTheme.colors.error
    )
}

@Composable
fun ShowDetails(bar: Bar) {
    Column {
        Header(text = bar.name, border = BorderStroke(0.dp, MaterialTheme.colors.background))

        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = bar.description,
                style = MaterialTheme.typography.body2
            )
        }
    }
}