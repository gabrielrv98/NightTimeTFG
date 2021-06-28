package com.esei.grvidal.nighttime.pages.bar_pages

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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
import androidx.navigation.NavHostController
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.viewmodels.*
import com.esei.grvidal.nighttime.ui.NightTimeTheme
import com.esei.grvidal.nighttime.navigateWithId
import com.esei.grvidal.nighttime.network.BarDTO
import com.esei.grvidal.nighttime.scaffold.*

private const val TAG = "BarPage"


/**
 * StateFull composable that manage the main composition of the BarPAge view
 *
 * @param navController navigator with the queue of destinies and it will be used to navigate or go back
 * @param barVM ViewModel for Bar page
 */
@Composable
fun BarPage(
    navController: NavHostController,
    barVM: BarViewModel,
    city: City
) {
    SideEffect {
        barVM.city = city
    }


    TitleColumn(title = stringResource(id = R.string.baresZona) + " " + barVM.city.name) {

        val state = rememberLazyListState()

        Log.d(TAG, "BarPage: size: ${barVM.barList.size}  state : ${state.firstVisibleItemIndex} ")
        /**
         * [LazyListState.firstVisibleItemIndex] points at the number of items already scrolled
         *
         * So if barList is not empty then we check if the remaining bars in barList are 8 or less
         * (Full screen of the app),
         * if so, more than from API is fetched
         */
        if (barVM.barList.isNotEmpty() && barVM.barList.size - state.firstVisibleItemIndex <= 8) {
            Log.d(TAG, "BarPage: more pages")
            barVM.loadBarsOnCity()
        }

        BarList(
            barVM.barList,
            state = state
        ) {
            val bar = it as BarDTO
            BarChip(
                name = bar.name,
                description = bar.description,
                schedule = bar.schedule,
                onBarClick = {
                    Log.d(TAG, "BarPage: Navigating to barDetails id ${bar.id}")
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
    modifier: Modifier = Modifier.padding(top = 24.dp),
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
 * @param list list of bars
 * @param content content of the rows
 */
@Composable
fun BarList(
    list: List<Any>,
    state: LazyListState,
    content: @Composable (Any) -> Unit = {}
) {
    LazyColumnFor(
        items = list,
        modifier = Modifier.fillMaxSize()
            .padding(top = 12.dp)
            .padding(horizontal = 24.dp),
        state = state
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
    style: TextStyle = MaterialTheme.typography.h6,
    icon: (@Composable (Modifier) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
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

        icon?.let {
            it(Modifier.align(Alignment.CenterEnd))
        }
    }
}

/**
 * Composable that describes the information that will be shown about each bar
 *
 * @param onBarClick action to be done when a bar is pressed
 * @param name name of the bar
 * @param description Description of the bar
 * @param schedule schedule of what days it is open or not
 */
@Composable
fun BarChip(
    name: String,
    schedule: List<String>,
    description: String = "",
    onBarClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.padding(8.dp)
            .wrapContentHeight()
            .clickable(onClick = onBarClick)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 6.dp),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                text = name
            )

            Spacer(Modifier.width(55.dp))
            WeekScheduleIcon(schedule = schedule)

        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.body2,
                text = makeLongShort(description, 80)
            )
        }
    }
}

/**
 * Composable that manages the Schedule with all days
 * @param schedule List of 7 booleans, each one represents if that day it's open
 */
@Composable
fun WeekScheduleIcon(schedule: List<String>) {
    Row {
        DaySchedule(
            day = stringResource(id = R.string.lunes_abbreviation),
            schedule[0].isNotEmpty()
        )
        DaySchedule(
            day = stringResource(id = R.string.martes_abbreviation),
            schedule[1].isNotEmpty()
        )
        DaySchedule(
            day = stringResource(id = R.string.miercoles_abbreviation),
            schedule[2].isNotEmpty()
        )
        DaySchedule(
            day = stringResource(id = R.string.jueves_abbreviation),
            schedule[3].isNotEmpty()
        )
        DaySchedule(
            day = stringResource(id = R.string.viernes_abbreviation),
            schedule[4].isNotEmpty()
        )
        DaySchedule(
            day = stringResource(id = R.string.sabado_abbreviation),
            schedule[5].isNotEmpty()
        )
        DaySchedule(
            day = stringResource(id = R.string.domingo_abbreviation),
            schedule[6].isNotEmpty()
        )
    }
}

/**
 * Composable that shows the daily schedule
 */
@Composable
fun DailySchedule(day: String, schedule: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = day)
        Text(schedule)
    }
    Divider(thickness = 1.dp, color = AmbientContentColor.current.copy(alpha = 0.15f))
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
 * @param day Letter with the represented day
 * @param enable boolean to show if its enabled
 * @param border border around the letter
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

@Preview("BarPagePreview")
@Composable
fun BarPreview() {
    NightTimeTheme {
        val barList = listOf(
            BarDTO(
                id = 3,
                name = "Night",
                owner = "NightOwner",
                address = "Rua cabeza de manzaneda",
                description = "Ven y pasatelo como si volvieses a tener 15",
                mondaySchedule = null,
                tuesdaySchedule = "11:00-20:30",
                wednesdaySchedule = "12:00-22:00",
                thursdaySchedule = null,
                fridaySchedule = "11:00-20:30",
                saturdaySchedule = "14:40-21:20",
                sundaySchedule = "09:30-21:30"
            ),

            BarDTO(
                id = 4,
                name = "Studio 34",
                owner = "Studio Owner Santiago",
                address = "Rua Concordia",
                description = "Un lugar libre para gente libre",
                mondaySchedule = "12:00-22:00",
                tuesdaySchedule = "11:00-20:30",
                wednesdaySchedule = null,
                thursdaySchedule = "14:40-21:20",
                fridaySchedule = "11:00-20:30",
                saturdaySchedule = null,
                sundaySchedule = "09:30-21:30"
            )
        )

        TitleColumn(title = stringResource(id = R.string.baresZona) + " Ourense") {
            val state = rememberLazyListState()
            BarList(barList, state) {
                val bar = it as BarDTO
                BarChip(
                    name = bar.name,
                    description = bar.description,
                    schedule = bar.schedule,
                    onBarClick = {}
                )
            }
        }
    }
}