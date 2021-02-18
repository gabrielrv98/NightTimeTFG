package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavHostController
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.City
import com.esei.grvidal.nighttime.ui.NightTimeTheme
import com.esei.grvidal.nighttime.NavigationScreens
import com.esei.grvidal.nighttime.data.BarViewModel
import com.esei.grvidal.nighttime.data.MyDate
import com.esei.grvidal.nighttime.navigateWithId


class BarDAO {
    val bares: List<Bar> = listOf(
        Bar(0, "Lazaros", "Un pub para gente pija").apply {
            schedule = listOf(false, false, false, true, false, true, true)
        },
        Bar(1, "Lokal", "Un lokal para escuchar rock").apply {
            schedule = listOf(true, true, false, true, false, false, true)
        },
        Bar(2, "Urbe", "Las mejores aspiradoras").apply {
            schedule = listOf(false, true, false, false, false, true, false)
            events = listOf(
                EventData(MyDate(26, 11, 2020), "Copas gratis"),
                EventData(MyDate(6, 12, 2020), "Todo a mitad de precio"),
                EventData(MyDate(12, 12, 2020), "Un euro rebajado"),
                EventData(MyDate(14, 12, 2020), "Mas caro de lo normal"),
                EventData(MyDate(21, 12, 2020), "Dj personalizado")
            )
        },
        Bar(
            3,
            "Patio Andaluz",
            "Otro gran local pero con una descripcion algo larga de mas que se acortara"
        ).apply {
            schedule = listOf(false, true, false, false, true, true, false)
        },
        Bar(4, "Mil petalos", "Chicas siempre listas para darlo todo").apply {
            schedule = listOf(true, true, true, true, true, true, true)
            events = listOf(
                EventData(MyDate(26, 11, 2020), "Copas gratis"),
                EventData(MyDate(6, 12, 2020), "Todo a mitad de precio"),
                EventData(MyDate(12, 12, 2020), "Un euro rebajado"),
                EventData(MyDate(14, 12, 2020), "Mas caro de lo normal"),
                EventData(MyDate(21, 12, 2020), "Dj personalizado")
            )
        }
    )
}

data class EventData(val date: MyDate, val description: String)

data class Bar(val id: Int, val name: String, val description: String) {
    var events: List<EventData>? = null
    var multimedia: List<Any>? = listOf(
        Icons.Outlined.PartyMode,
        Icons.Outlined.LocalDining,
        Icons.Outlined.Photo,
        Icons.Outlined.Photo,
        Icons.Outlined.Dehaze,
        Icons.Outlined.Photo,
        Icons.Outlined.PartyMode,
        Icons.Outlined.CompareArrows
    )
    lateinit var schedule: List<Boolean>
    var time: String = "21:00 - 06:00"
    var address: String = "Rúa Pizarro, 8, 32005 Ourense"
}

/**
 * StateFull composable that manage the main composition of the BarPAge view
 *
 * @param cityId selected city
 * @param navController navigator with the queue of destinies and it will be used to navigate or go back
 */
@Composable
fun BarPage(cityId: City, navController: NavHostController, barData : BarViewModel) {

    if (barData.loggedUser.value == null)
        Text(
            text="es null"
        )
    val barList = BarDAO().bares
    //val barList = BarDAO().getBares(cityId.id)//Futuro llamamiento

    TitleColumn(title = stringResource(id = R.string.baresZona) + " " + cityId.name) {
        BarList(barList) {
            val bar = it as Bar
            BarChip(
                name = bar.name,
                description = bar.description,
                time = bar.time,
                schedule = bar.schedule,
                onBarClick = {
                    navController.navigateWithId(
                        NavigationScreens.BarDetails.route, bar.id
                    )
                })
        }
    }
}

/**
 * Composable that describes the structure of this View, a title with a body
 *
 * @param modifier Modifier with the relative padding
 * @param title to show with a border
 * @param content to show below the title
 */
@Composable
fun TitleColumn(
    modifier :Modifier = Modifier.padding(top = 24.dp),
    title: String,
    content: @Composable () -> Unit = {}
) {
    Column {
        Header(
            modifier = modifier,
            text = title
        )
        content()

    }
}

/**
 * Composable that describes the list of the Bars and formats each row
 *
 * @param barList list of bars
 * @param content content of the rows
 */
@Composable
fun BarList(
    barList: List<Any>,
    content: @Composable (Any) -> Unit = {}
) {
    LazyColumnFor(
        items = barList,
        modifier = Modifier.fillMaxSize()
            .padding(top = 12.dp)
            .padding(horizontal = 24.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 3.dp)
        ) {
            content(it)
        }
        Divider(startIndent = 30.dp, modifier = Modifier.padding(vertical = 3.dp))

    }
}

/**
 * Composable that describes the header of the View
 *
 * @param modifier modifier with the relative padding
 * @param text Title to be shown
 * @param border Custom border to the title
 * @param style Style of the title
 */
@Composable
fun Header(
    modifier: Modifier = Modifier,
    text: String,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.primary),
    style: TextStyle = MaterialTheme.typography.h6
) {

    Row(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .padding(6.dp)
                .border(
                    border = border,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(vertical = 6.dp, horizontal = 10.dp),
            text = text,
            style = style
        )
    }
}

/**
 * Composable that describes the information that will be shown about each bar
 *
 * @param onBarClick action to be done when a bar is pressed
 * @param name name of the bar
 * @param description Description of the bar
 * @param time Time when its open
 * @param schedule schedule of what days it is open or not
 */
@Composable
fun BarChip(
    onBarClick: () -> Unit = {},
    name: String,
    description: String = "",
    time: String,
    schedule: List<Boolean>
) {
    Column(
        modifier = Modifier.padding(8.dp)
            .clickable(onClick = onBarClick)
    ) {
        Row(
            modifier = Modifier.padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = 6.dp),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                text = name
            )
            Text(
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body2,
                text = makeLongShort(description, 25)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(5.dp))
            WeekSchedule(schedule = schedule)

            Text(
                modifier = Modifier.padding(start = 18.dp),
                color = Color.Gray,
                text = time,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

/**
 * Composable that manages the Schedule with all days
 * @param schedule List of 7 booleans, each one represents if that day it's open
 */
@Composable
fun WeekSchedule(schedule: List<Boolean>) {
    DaySchedule(day = stringResource(id = R.string.lunes), schedule[0])
    DaySchedule(day = stringResource(id = R.string.martes), schedule[1])
    DaySchedule(day = stringResource(id = R.string.miercoles), schedule[2])
    DaySchedule(day = stringResource(id = R.string.jueves), schedule[3])
    DaySchedule(day = stringResource(id = R.string.viernes), schedule[4])
    DaySchedule(day = stringResource(id = R.string.sabado), schedule[5])
    DaySchedule(day = stringResource(id = R.string.domingo), schedule[6])
}

/**
 * Function that takes a substring of length [maxLetter] from the string [text]
 *
 * @param text String of any length
 * @param maxLetter max Num of letters
 */
fun makeLongShort(text: String, maxLetter: Int): String {
    return if (text.length <= maxLetter) text
        else text.removeRange(maxLetter, text.length).plus(" ...")
}

/**
 * Composable that describes if a day of the week that starts with [day] it's open [enable] or not
 * the day will be in a box with the border [border] and with a shape [shape]
 *
 * @param day Letter with the representated day
 * @param enable boolean to show if its enabled
 * @param border border arround the letter
 * @param shape shape of the border
 */
@Composable
fun DaySchedule(
    day: String,
    enable: Boolean = false,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.primary),
    shape: Shape = RoundedCornerShape(0)
) {

    Box(
        modifier = Modifier
            .border(
                border = border,
                shape = shape
            )
            .preferredWidth(15.dp)
            .preferredHeight(15.dp)
            .clip(shape)
        //.background( if(enable) MaterialTheme.colors.background
        // else  Color.LightGray)

        ,
        alignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(0.dp),
            style = MaterialTheme.typography.overline,
            text = day,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp,
            color = if (enable) MaterialTheme.colors.onBackground
            else Color.LightGray
        )
    }
}

/**
 * Composable that describes if a day of the week that starts with [day] it's open [enable] or not
 * the day will be in a box with the border [border] and with a shape [shape]
 *
 * @param day Letter with the representated day
 * @param enable boolean to show if its enabled
 * @param border border arround the letter
 * @param shape shape of the border
 */
@Deprecated("rounded days", ReplaceWith("daySchedule"))
@Composable
fun daySchedule2(
    day: String,
    enable: Boolean = false,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.primary),
    shape: Shape = RoundedCornerShape(50)
) {

    Box(
        modifier = Modifier
            .padding(horizontal = 1.dp)
            .border(
                border = border,
                shape = shape
            )
            .preferredWidth(15.dp)
            .preferredHeight(15.dp)
            .clip(shape)
            .background(
                if (enable) Color.Green
                else MaterialTheme.colors.background
            ),
        alignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(0.dp),
            style = MaterialTheme.typography.overline,
            text = day,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Preview("BarDetail")
@Composable
fun BarPreview() {
    NightTimeTheme {
        val barList = BarDAO().bares
        //val barList = BarDAO().getBares(cityId.id)//Futuro llamamiento

        TitleColumn(title = stringResource(id = R.string.baresZona) + " Ourense") {
            BarList(barList) {
                val bar = it as Bar
                BarChip(
                    name = bar.name,
                    description = bar.description,
                    time = bar.time,
                    schedule = bar.schedule,
                    onBarClick = {}
                )
            }
        }
    }
}