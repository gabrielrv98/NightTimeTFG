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
import com.esei.grvidal.nighttime.data.MyDate
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
            MyDate(
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

    // Edited set that if the month changes, the calendar will update
    val mySetDay = { myDate: MyDate ->
        if (date.month != myDate.month)
            setCalendar(ChipDayFactory.datesCreator(myDate))
        setDate(myDate)
    }
    val userList = //todo this is hardcoded
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
            User(name = "Filo")
        )

    //If Friendly users Card is touched a dialog with their names should be shown
    if (showDialog)

        CustomDialog(onClose = {setShowDialog(false)} ) {
            FriendlyUsersDialog(itemsUser = userList, modifier = Modifier.preferredHeight(600.dp))
        }

    Column {
        //Top of the screen
        Row(
            modifier = modifier.weight(1.5f),
            horizontalArrangement = Arrangement.Center
        ) {
            //val color = MaterialTheme.colors.background.copy(alpha = 0.2f)
            //ContentColorAmbient.current.copy(alpha = 0.02f)
            CalendarWindow(
                date = date,
                setDate = mySetDay,
                calendar = calendar,
                colorBackground = MaterialTheme.colors.background //.copy(alpha = 0.2f)       //doesn't really work
            )
        }

        //Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)

        //Bottom of the screen
        Row(
            modifier = modifier.weight(1f)
        ) {

            DayInformation(
                genteTotal = "27", amigos = "12",
                showFriends = { setShowDialog(true) }, date = date
            )
        }

    }

}

/**
 * Dialog that shows the friends who are coming out the selected date
 *
 * @param modifier custom modifier
 * @param itemsUser list with the users to show
 */
@Composable
fun FriendlyUsersDialog(
    itemsUser :List<User>,
    modifier :Modifier = Modifier
) {
    //List with the users
    LazyColumnFor(
        items = itemsUser,
        modifier = modifier
    ) {
        //Each user
        Row(
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            //Image
            Surface(
                modifier = Modifier.preferredSize(20.dp),
                shape = CircleShape,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            ) {
                // Image goes here
            }

            //Name
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = (it.name + " - Apellidos"))
            }
        }

    }

}

/**
 * Top View of the calendar
 *
 * @param date is the selected date
 * @param setDate is the setter of the selected date
 * @param colorBackground is the color of the background
 * @param calendar List of list (weeks)  of days
 */
@Composable
fun CalendarWindow(
    date: MyDate, setDate: (MyDate) -> Unit,
    colorBackground: Color = MaterialTheme.colors.background,
    calendar: List<List<MyDate>>
) {
    //Name of the month shown on the top
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
        modifier = Modifier.fillMaxHeight().fillMaxHeight()
            .padding(bottom = 6.dp),
        color = colorBackground,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {

            //Header of the Calendar
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Button previous month
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = { setDate(date.previousMonth) }) {
                    Icon(asset = Icons.Default.ArrowBack)
                }
                //Name of the month
                Text(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.h6,
                    text = monthName.toUpperCase(Locale.getDefault()),
                    textAlign = TextAlign.Center
                )
                //Button next month
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = { setDate(date.nextMonth) }) {
                    Icon(asset = Icons.Default.ArrowForward)
                }
            }

            //Header with the name of the days
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

            Divider(thickness = 1.dp, color = ContentColorAmbient.current.copy(alpha = 0.15f))


            /*
            If you swipe up when it's not needed (or "possible") you will get
            IlegalStateException entered drag with non-zero pending scroll: -101.1
LazyColumnFor(
    items = calendar,
    modifier = Modifier.padding(top = 0.dp, start = 6.dp, end = 6.dp)
        .padding(bottom = 0.dp)
) {
*/
            //Calendar indeed
            ScrollableColumn(
                modifier = Modifier.padding(top = 0.dp, start = 6.dp, end = 6.dp)
                    .padding(bottom = 0.dp)
            ) {

                for (week in calendar) {
                    //Week Row
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        for (days in week) {

                            //Each day in the week
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

/**
 * Shows a text in the middle of a box
 *
 * @param text is the Text to shown
 * @param modifier is the modifier, the default option is the default modifier
 * @param textAlign is the Align of the text, default option is center
 */
@Composable
fun CenteredText(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = currentTextStyle()
) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = textAlign,
        style = textStyle
    )
}

/**
 * This fun will show a Day with the right padding and a centered text with the number of the day,
 * if its selected a circle arround it will appear, and if the day isn't in the selected month the
 * number will show gray
 *
 * @param date is the selected date
 * @param setDate is the setter for the selected date
 * @param chipDate is the date of the chip
 * @param text is the text of the chip, default is the day of chip
 * @param textModifier is the modifier of the text, default has 10.dp of padding
 * @param textAlign is the Align of the text, default is center
 * @param style is the style of the text, default is h5 of the selected Theme
 * @param fontSize is the size of the font of the text, default is 18
 * @param colorNotMonth is the color of the text if the chipDate is from a month different from the
 * selected date
 * @param colorNotSelected is the color of the chip if it's not selected but has the same month as
 * the selected date
 * @param colorBackground is the color of the background of the calendar
 */
@Composable
private fun DayChip(
    date: MyDate,
    setDate: (MyDate) -> Unit,
    chipDate: MyDate,
    text: String = chipDate.day.toString(),
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
                onClick = { setDate(chipDate) },
                indication = null
            ),
        shape = RoundedCornerShape(50),
        border = if (date == chipDate) BorderStroke(2.dp, MaterialTheme.colors.primary)
        else null,
        elevation = 0.dp,
        color = colorBackground

    ) {
        Text(
            text = text,
            modifier = textModifier,
            textAlign = textAlign,
            style = style,
            fontSize = fontSize,
            color = when {
                chipDate.month != date.month -> colorNotMonth
                chipDate == date -> colorSelected
                else -> colorNotSelected
            }
        )
    }

}

/**
 * Is the information about the people who is coming out tonight, it will update if the selected date changes
 *
 * @param genteTotal total amount of people who is coming out
 * @param amigos total amount of people who is coming out AND you have them added as a friend
 * @param showFriends lambda expression that will update a boolean variable that will show a dialog with your friends
 * @param date Selected date
 */
@Composable
fun DayInformation(
    genteTotal: String = "?",
    amigos: String = "?",
    showFriends: () -> Unit,
    date: MyDate
) {
    //Formatted date string
    val formattedDay = StringBuilder(8)
        .append(date.day)
        .append("/")
        .append(date.month)
        .append("/")
        .append(date.year)
        .toString()


        Row(
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().weight(0.7f)
                    //.padding(vertical = 10.dp)
                    .padding(top = 6.dp)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                //Formatted text of the selected date
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CenteredText(
                            modifier = Modifier
                                .background(Color.White)
                                .border(
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colors.primary
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .fillMaxWidth(),
                            text = formattedDay,
                            textStyle = MaterialTheme.typography.h6
                        )

                        Button(
                            modifier = Modifier.padding(top = 12.dp),
                            onClick = {}
                        ) {
                            Text(text  = stringResource(id = R.string.elegirDia))
                        }
                    }
                }

                //Info about the people on the selected date
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    //Button to show a dialog with all the friends
                    Button(
                        onClick = showFriends,
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        InfoChip(
                            numberOfPeople = amigos,
                            peopleDescription = stringResource(id = R.string.amigos)
                        )
                    }

                    //A chip with the number of the total confirmed people
                    Surface(
                        modifier = Modifier.padding(6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
                        shape = RoundedCornerShape(15.dp),
                        elevation = 1.dp,
                        color = MaterialTheme.colors.surface
                    ) {

                        InfoChip(
                            modifier = Modifier.padding(12.dp),
                            numberOfPeople = genteTotal,
                            peopleDescription = stringResource(id = R.string.genteTotal)
                        )
                    }
                }
            }

//Cool vertical divider

            Box(
                modifier = Modifier
                    //.padding(horizontal = 3.dp)
                    .padding(top = 8.dp)
                    .fillMaxHeight()
                    .preferredWidth(1.dp)
                    .background(MaterialTheme.colors.primary)
            )

//Column with the Events on the selected date

            Surface(
                modifier = Modifier.weight(1.35f).fillMaxHeight(),
                color = MaterialTheme.colors.background
            ) {
                ScrollableColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(
                            top = 10.dp,
                            bottom = 0.dp
                        )
                        .padding(horizontal = 10.dp)
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

/**
 * Formatted Event
 *
 * @param barName name that will be shown as bold text
 * @param eventDescription description of the event
 */
@Composable
fun Event(
    barName: String,
    eventDescription: String
) {
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


/**
 * Chip with a bold number and a description under it, all surrounded with  a black border
 *
 * @param modifier custom modifier
 * @param numberOfPeople number of people who has confirmed
 * @param peopleDescription description of the people ( friends or all )
 * @param styleTitle custom style of the Title
 * @param styleDescription custom style of the description
 */
@Composable
private fun InfoChip(
    modifier: Modifier = Modifier,
    numberOfPeople: String = "",
    peopleDescription: String = "",
    styleTitle : TextStyle = MaterialTheme.typography.body1,
    styleDescription : TextStyle =  MaterialTheme.typography.body2
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = numberOfPeople,
            style = styleTitle,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = peopleDescription,
            style =styleDescription
        )
    }
}

@Preview("CalendarWindow")
@Composable
fun CalendarWindowPreview() {
    val (date, setDate) = remember {
        mutableStateOf(
            MyDate(
                LocalDate.now().dayOfMonth,
                LocalDate.now().month.value,
                LocalDate.now().year
            )
        )
    }
    CalendarWindow(date, setDate, calendar = ChipDayFactory.datesCreator())
}

@Preview("Page")
@Composable
fun CalendarPreview() {
    NightTimeTheme {

        CalendarPageView()
    }
}


@Preview("Dialog")
@Composable
fun DialogPreview() {
    val userList = //todo this is hardcoded
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
            User(name = "Filo")
        )
    NightTimeTheme {

        CustomDialog(onClose = {} ) {
            FriendlyUsersDialog(itemsUser = userList, modifier = Modifier)
        }
    }
}
