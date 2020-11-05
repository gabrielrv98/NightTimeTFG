package com.esei.grvidal.nighttime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.ui.ChipDayFactory
import com.esei.grvidal.nighttime.ui.MyDay
import com.google.android.material.chip.Chip
import java.util.*

@Composable
fun CalendarPageView() {

    val modifier = Modifier.fillMaxWidth()
    val (date, setDate) = remember { mutableStateOf(MyDay(5, 11, 2020)) }

    Column(
        modifier = modifier
    ) {

        Row(
            modifier = modifier.weight(1.2f),
            horizontalArrangement = Arrangement.Center
        ) {


            CalendarWindow(date, setDate)

        }

        //todo Eliminar este Divider es solo una referencia
        Divider(thickness = 2.dp, modifier = modifier)

        Row(modifier = modifier.weight(1f)) {
            Surface(
                modifier = Modifier.padding(6.dp),
                border = BorderStroke(2.dp, MaterialTheme.colors.primary)
            ) {
                Text(text = "abc", modifier = Modifier.padding(12.dp))
            }
        }

    }

}

@Composable
fun CalendarWindow(date: MyDay, setDate: (MyDay) -> Unit) {
    Surface(
        modifier = Modifier.padding(6.dp),
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {

            val myModifier = Modifier.padding(horizontal = 20.dp)

            val calendar = ChipDayFactory.datesCreator()
            val chunkedList = calendar.chunked(7)

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "L",
                    modifier = myModifier
                )
                Text(
                    "M",
                    modifier = myModifier
                )
                Text(
                    "Mi",
                    modifier = myModifier
                )
                Text(
                    "J",
                    modifier = myModifier
                )
                Text(
                    "V",
                    modifier = myModifier
                )
                Text(
                    "S",
                    modifier = myModifier
                )
                Text(
                    "D",
                    modifier = myModifier
                )
            }

            Divider(thickness = 1.dp)
            LazyColumnFor(items = chunkedList) {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(vertical = 9.dp)
                        .padding(top = 15.dp)
                ) {
                    for (days in it) {
                        val letterColor =
                            if (days.month != date.month) Color.Gray else MaterialTheme.colors.onSurface
                        Text(
                            text = days.day.toString(),
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .weight(1f)
                                .clickable(onClick = {}),
                            textAlign = TextAlign.Center,
                            lineHeight = TextUnit.Companion.Sp(20),
                            style = MaterialTheme.typography.h5,
                            fontSize = TextUnit.Companion.Sp(18),
                            color = letterColor
                        )
                    }
                }

            }


/*
            Column {
                Row {
                    Column ( modifier = myModifier.clickable(onClick = {})){
                        Text(text = "1.1")
                    }
                    Column ( modifier = myModifier.clickable(onClick = {})){
                        Text(text = "1.2")
                    }
                    Column ( modifier = myModifier.clickable(onClick = {})){
                        Text(text = "1.3")
                    }
                }
                Row {
                    Column ( modifier = myModifier.clickable(onClick = {})){
                        Text(text = "2.1")
                    }
                    Column ( modifier = myModifier.clickable(onClick = {})){
                        Text(text = "2.2")
                    }
                    Column ( modifier = myModifier.clickable(onClick = {})){
                        Text(text = "2.3")
                    }
                }
                Row {
                    Column ( modifier = myModifier.clickable(onClick = {})){
                        Text(text = "3.1")
                    }
                    Column ( modifier = myModifier.clickable(onClick = {})){
                        Text(text = "3.2")
                    }
                    Column ( modifier = myModifier.clickable(onClick = {})){
                        Text(text = "3.3")
                    }
                }
            }
            */

        }
    }
}

@Preview("CalendarWindow")
@Composable
fun CalendarWindowPreview() {
    val (date, setDate) = remember { mutableStateOf(MyDay(5, 11, 2020)) }
    CalendarWindow(date, setDate)
}

@Preview("Calendar")
@Composable
fun CalendarPreview() {
    CalendarPageView()
}