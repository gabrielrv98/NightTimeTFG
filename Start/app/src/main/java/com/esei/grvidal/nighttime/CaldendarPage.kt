package com.esei.grvidal.nighttime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ContentColorAmbient
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.ui.ChipDayFactory
import com.esei.grvidal.nighttime.ui.MyDay
import com.esei.grvidal.nighttime.ui.NightTimeTheme
import com.esei.grvidal.nighttime.ui.grayBlue

@Composable
fun CalendarPageView() {

    val modifier = Modifier.fillMaxWidth()
    val (date, setDate) = remember { mutableStateOf(MyDay(5, 11, 2020)) }
    val myCalendar : List<List<MyDay>> = ChipDayFactory.datesCreator()
    val (calendar,setCalendar) = remember{ mutableStateOf(myCalendar)}

    val mySetDay = { myDay: MyDay  ->
        if (date.month != myDay.month)
            setCalendar(ChipDayFactory.datesCreator(myDay))
        setDate(myDay)
    }
    Column(
        modifier = modifier
    ) {

        Row(
            modifier = modifier.weight(1.2f),
            horizontalArrangement = Arrangement.Center
        ) {
            CalendarWindow(date = date, setDate = mySetDay, calendar = calendar)
        }

        //todo Eliminar este Divider es solo una referencia
        Divider(thickness = 2.dp,  color = MaterialTheme.colors.primary)

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
fun CalendarWindow(date: MyDay, setDate: (MyDay) -> Unit ,
                   colorBackground : Color = MaterialTheme.colors.secondary,
                   calendar : List<List<MyDay>> ) {

    Surface(
        modifier = Modifier.fillMaxHeight().fillMaxHeight(),
        color = colorBackground,
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {
            Surface( color = ContentColorAmbient.current.copy(alpha = 0.15f),
                modifier = Modifier.padding(6.dp)
            ) {
                val myModifier = Modifier.padding(horizontal = 15.dp)
                    .weight(1f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 9.dp),
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
                        modifier = myModifier,
                        maxLines = 1
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
            }

            Divider(thickness = 1.dp)
            LazyColumnFor(items = calendar,modifier = Modifier.padding(6.dp)) {


            //Column(modifier = Modifier.padding(top = 15.dp)) {

                //for(week in chunkedCalendar){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(vertical = 9.dp)
                    ) {
                        for (days in it) {

                            Box(modifier = Modifier.weight(1f)) {
                                DayChip(date, setDate, days, colorBackground = colorBackground)
                            }
                        }
                    }
                //}


            }
        }
    }
}

@Composable
private fun DayChip(
    date: MyDay,
    setDate: (MyDay) -> Unit,
    chipDay: MyDay,
    text:String = chipDay.day.toString(),
    modifier:Modifier = Modifier
        .padding(10.dp),
    textAlign: TextAlign = TextAlign.Center,
    style: TextStyle = MaterialTheme.typography.h5,
    fontSize: TextUnit = TextUnit.Sp(18),
    colorNotMonth: Color = Color.Gray,
    colorSelected: Color = MaterialTheme.colors.primary,
    colorNotSelected: Color = MaterialTheme.colors.onSurface,
    colorBackground : Color = MaterialTheme.colors.surface
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(25))
            .preferredWidth(45.dp)
            .preferredHeight(45.dp)
            .clickable(
                onClick = { setDate(chipDay) },
                indication = null
            ),
        shape = RoundedCornerShape(50),
        border = if (date == chipDay) BorderStroke(2.dp, MaterialTheme.colors.primary)
                else null,
        elevation = if (date == chipDay) 1.dp
                else 0.dp,
        color = colorBackground

    ) {
        Text(
            text = text,
            modifier = modifier,
            textAlign = textAlign,
            style = style,
            fontSize = fontSize,
            color = when {
                chipDay.month != date.month -> colorNotMonth
                chipDay == date -> colorSelected
                else -> colorNotSelected
            }
        )
    }

}

@Preview("CalendarWindow")
@Composable
fun CalendarWindowPreview() {
    val (date, setDate) = remember { mutableStateOf(MyDay(5, 11, 2020)) }
    CalendarWindow(date, setDate,calendar = ChipDayFactory.datesCreator())
}

@Preview("Calendar")
@Composable
fun CalendarPreview() {
    NightTimeTheme{

        CalendarPageView()
    }
}