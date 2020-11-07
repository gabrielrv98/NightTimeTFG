package com.esei.grvidal.nighttime

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.data.ChipDayFactory
import com.esei.grvidal.nighttime.data.MyDay
import com.esei.grvidal.nighttime.data.User
import com.esei.grvidal.nighttime.ui.NightTimeTheme
import java.time.LocalDate
import java.util.*

/**
 * Show the Calendar page, with the calendar on the top and the information below it
 */
@Composable
fun CalendarPageView() {

    val modifier = Modifier.fillMaxWidth().fillMaxHeight()
    //remember date, it's used to show the selected date and move the calendar to the specified month
    val (date, setDate) = remember {
        mutableStateOf(
            MyDay(
                LocalDate.now().dayOfMonth,
                LocalDate.now().month.value,
                LocalDate.now().year
            )
        )
    }
    //Remembered state of the days that must be shown on the calendar
    val (calendar, setCalendar) = remember { mutableStateOf(ChipDayFactory.datesCreator()) }
    //Remembered state of a boolean that express if the dialog with the friendly users must be shown
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    //
    val mySetDay = { myDay: MyDay ->
        if (date.month != myDay.month)
            setCalendar(ChipDayFactory.datesCreator(myDay))
        setDate(myDay)
    }

    val submit = { setShowDialog(true) }

    //If Friendly users Card is touched a dialog with their names should be shown
    if (showDialog)
        FriendlyUsersDialog(
            onClose = { setShowDialog(false) },
            //todo this is hardcoded
            listOf(
                User(name = "Nuria"),
                User(name = "Miguel"),
                User(name = "Maria"),
                User(name = "Marcos"),
                User(name = "Laura"),
                User(name = "Sara"),
                User(name = "Julio"),
                User(name = "Juan"),
                User(name = "Pedro"),
                User(name = "Salva"),
                User(name = "Gabriel"),
                User(name = "Jose"),
                User(name = "Emma"),
                User(name = "Santi"),
                User(name = "Filo"),
                User(name = "Nuria"),
                User(name = "Miguel"),
                User(name = "Maria"),
                User(name = "Marcos"),
                User(name = "Laura"),
                User(name = "Sara"),
                User(name = "Julio"),
                User(name = "Juan"),
                User(name = "Pedro"),
                User(name = "Salva"),
                User(name = "Gabriel"),
                User(name = "Jose"),
                User(name = "Emma"),
                User(name = "Santi"),
                User(name = "Filo"),

                )
        )

    Column {
        Row(
            modifier = modifier.weight(1.4f),
            horizontalArrangement = Arrangement.Center
        ) {
            CalendarWindow(date = date, setDate = mySetDay, calendar = calendar)
        }

        //todo Eliminar este Divider es solo una referencia

        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)


/*
        Row {
            Surface(
                modifier = Modifier//.fillMaxWidth()
                    .preferredHeight(1.dp).weight(1f),
                color = MaterialTheme.colors.primary,
                elevation = 1.dp,
                content = {}
            )
        }
*/

        Row(modifier = modifier.weight(1f).padding(bottom = 50.dp)) {

            DayInformation(showFriends = submit, date = date)
        }

    }

}

@Composable
fun FriendlyUsersDialog(
    onClose: () -> Unit,
    itemsUser: List<User>
) {
    Dialog(onDismissRequest = onClose) {

        Surface(
            modifier = Modifier.clip(MaterialTheme.shapes.medium)
                .padding(24.dp),
            color = MaterialTheme.colors.background,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(3.dp, MaterialTheme.colors.primary)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                LazyColumnFor(
                    items = itemsUser,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.preferredSize(20.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                        ) {
                            // Image goes here
                        }

                        Column(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = (it.name + " - Apellidos"))
                        }
                    }

                }

                Button(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .align(Alignment.End),
                    onClick = onClose
                ) {
                    Text(text = stringResource(R.string.cerrar))
                }
            }


        }


    }
}

@Composable
fun CalendarWindow(
    date: MyDay, setDate: (MyDay) -> Unit,
    colorBackground: Color = MaterialTheme.colors.background,
    calendar: List<List<MyDay>>
) {
    val monthName = when (date.month) {
        1 -> stringResource(id = R.string.enero)
        2 -> stringResource(id = R.string.febrero)
        3 -> stringResource(id = R.string.marzo)
        4 -> stringResource(id = R.string.abril)
        5 -> stringResource(id = R.string.mayo)
        6 -> stringResource(id = R.string.junio)
        7 -> stringResource(id = R.string.julio)
        8 -> stringResource(id = R.string.agosto)
        9 -> stringResource(id = R.string.septiembre)
        10 -> stringResource(id = R.string.octubre)
        11 -> stringResource(id = R.string.noviembre)
        12 -> stringResource(id = R.string.diciembre)

        else -> stringResource(id = R.string.error)
    }

    Surface(
        modifier = Modifier.fillMaxHeight().fillMaxHeight(),
        color = colorBackground,
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = { setDate(date.previousMonth) }) {
                    Icon(asset = Icons.Default.ArrowBack)
                }
                Text(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.h6,
                    text = monthName.toUpperCase(Locale.ENGLISH),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = { setDate(date.nextMonth) }) {
                    Icon(asset = Icons.Default.ArrowForward)
                }
            }

            Row {
                Surface(
                    color = ContentColorAmbient.current.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 0.dp, bottom = 6.dp, start = 6.dp, end = 6.dp)
                ) {
                    val myModifier = Modifier.padding(horizontal = 15.dp)
                        .weight(1f)

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        CenteredText(
                            text = stringResource(id = R.string.lunes),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.martes),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.miercoles),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.jueves),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.viernes),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.sabado),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.domingo),
                            modifier = myModifier
                        )
                    }
                }

            }

            Divider(thickness = 1.dp, color = ContentColorAmbient.current.copy(alpha = 0.15f) )


            /*
            If you swipe up when it's not needed (or "possible") ypu'll get
            IlegalStateException entered drag with non-zero pending scroll: -101.1
LazyColumnFor(
    items = calendar,
    modifier = Modifier.padding(top = 0.dp, start = 6.dp, end = 6.dp)
        .padding(bottom = 0.dp)
) {
*/
            ScrollableColumn(
                modifier = Modifier.padding(top = 0.dp, start = 6.dp, end = 6.dp)
                    .padding(bottom = 0.dp)
            ) {

                for (week in calendar) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(vertical = 9.dp)
                    ) {
                        for (days in week) {

                            Box(
                                modifier = Modifier.weight(1f),
                                alignment = Alignment.Center
                            ) {
                                DayChip(date, setDate, days, colorBackground = colorBackground)
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun CenteredText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
private fun DayChip(
    date: MyDay,
    setDate: (MyDay) -> Unit,
    chipDay: MyDay,
    text: String = chipDay.day.toString(),
    textModifier: Modifier = Modifier.padding(10.dp),
    textAlign: TextAlign = TextAlign.Center,
    style: TextStyle = MaterialTheme.typography.h5,
    fontSize: TextUnit = TextUnit.Sp(18),
    colorNotMonth: Color = Color.Gray,
    colorSelected: Color = MaterialTheme.colors.primary,
    colorNotSelected: Color = MaterialTheme.colors.onSurface,
    colorBackground: Color = MaterialTheme.colors.background
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
            modifier = textModifier,
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

@Composable
fun DayInformation(
    genteTotal: String = "27",
    amigos: String = "12",
    showFriends: () -> Unit,
    date: MyDay
) {
    val formattedDay = StringBuilder(8)
        .append(date.day)
        .append("/")
        .append(date.month)
        .append("/")
        .append(date.year)
        .toString()

    Row(
        modifier = Modifier
            .padding(horizontal = 6.dp)
//            .padding(vertical = 12.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxHeight().weight(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp, top = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formattedDay,
                    style = MaterialTheme.typography.h6
                )
            }

            Column(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                InfoChip(info = amigos, text = "Amigos", onClick = showFriends)
                InfoChip(info = genteTotal, text = "Gente total")
            }


        }



        Box(
            modifier = Modifier
                .padding(start = 6.dp)
                .fillMaxHeight()
                .preferredWidth(1.dp)
                .background(MaterialTheme.colors.primary)
        )

        Column(
            modifier = Modifier.weight(1.4f)
                .fillMaxHeight()
                .padding(start = 6.dp),
            horizontalAlignment = Alignment.End
        ) {

            Surface(
                modifier = Modifier.fillMaxHeight(),
                //border = BorderStroke(2.dp, MaterialTheme.colors.primary),
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(8.dp)
            ) {
                ScrollableColumn(
                    modifier = Modifier.padding(
                        start = 10.dp,
                        end = 12.dp,
                        top = 10.dp,
                        bottom = 0.dp
                    )
                ) {

                    Event("Lazaros", "Copas a 3 euros")

                    Event("Lokal", "Musica de los 90")

                    Event("Patio andaluz", "Fiesta de la espuma")
                    Event("Luxus", "Hoy cerrado por fiesta infantil, nos vemos gente")
                    Event("Urbe", "Cocaina gratis")
                    Event(
                        "Dulce flor", "Ahora un 30% en nuevos productos y perfumes con un coste " +
                                "inferior a 2$"
                    )

                }

            }
        }
    }
}

@Composable
fun Event(barName: String, eventDescription: String) {
    Surface(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = barName,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = eventDescription, modifier = Modifier.padding(top = 1.dp, start = 6.dp),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun InfoChip(
    info: String = "?",
    text: String = "personas",
    onClick: (() -> Unit)? = null
) {

    var surfaceModifier = Modifier.padding(6.dp)

    if (onClick is () -> Unit) {
        surfaceModifier = surfaceModifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    }


    Surface(
        modifier = surfaceModifier,
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
        shape = RoundedCornerShape(15.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = info,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )

            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                modifier = Modifier
            )
        }


    }
}

@Preview("CalendarWindow")
@Composable
fun CalendarWindowPreview() {
    val (date, setDate) = remember {
        mutableStateOf(
            MyDay(
                LocalDate.now().dayOfMonth,
                LocalDate.now().month.value,
                LocalDate.now().year
            )
        )
    }
    CalendarWindow(date, setDate, calendar = ChipDayFactory.datesCreator())
}

@Preview("Calendar")
@Composable
fun CalendarPreview() {
    NightTimeTheme {

        CalendarPageView()
    }
}

@Preview("DayInfo")
@Composable
fun DayInfoPreview() {
    NightTimeTheme {
        DayInformation(showFriends = {}, date = MyDay(6, 11, 2020))
    }
}