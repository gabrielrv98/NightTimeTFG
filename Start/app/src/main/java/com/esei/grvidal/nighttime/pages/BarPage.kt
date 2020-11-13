package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
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
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.City
import com.esei.grvidal.nighttime.ui.NightTimeTheme
import androidx.navigation.compose.navigate
import com.esei.grvidal.nighttime.NavigationScreens


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
        }
    )
}

data class Bar(val id: Int, val name: String, val description: String) {
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
    var address : String = "Rúa Pizarro, 8, 32005 Ourense"
}

/**
 * Statefull composable with the logic of the Bar View
 */
@Composable
fun BarPage(cityId: City, navController: NavController) {
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
                    navController.navigate(
                        NavigationScreens.BarDetails.route + "/" + bar.id
                    )
                })
        }
    }
}

@Composable
fun TitleColumn(
    title: String,
    content: @Composable () -> Unit = {}
) {
    Column {
        Header(
            text = title
        )
        content()

    }
}

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

@Composable
fun Header(
    text: String,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.primary),
    style : TextStyle = MaterialTheme.typography.h6
) {

    Row(
        modifier = Modifier
            .padding(top = 24.dp, bottom = 12.dp)
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

fun makeLongShort(text: String, maxLetter: Int): String {
    return if (text.length <= maxLetter) text
    else
        text.removeRange(maxLetter, text.length).plus(" ...")
}


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

        TitleColumn(title = stringResource(id = R.string.baresZona) + " Ourense" ) {
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