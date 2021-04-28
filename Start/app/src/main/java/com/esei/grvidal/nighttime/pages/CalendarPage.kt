package com.esei.grvidal.nighttime.pages

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.CustomDialog
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.UsersSnapListDialog
import com.esei.grvidal.nighttime.data.*
import com.esei.grvidal.nighttime.ui.NightTimeTheme
import java.time.LocalDate
import java.util.*


private const val TAG = "CalendarPage"

/**
 * Show the Calendar page. A calendar with selectable dates, information of the selected date
 * and the list of the dates selected by the user
 *
 */
@Composable
fun CalendarPage(calendarVM: CalendarViewModel) {

    //Remembered state of a boolean that express if the dialog with the friendly users must be shown
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    //If Friendly users Card is touched a dialog with their names should be shown
    if (showDialog)
        UserListOnDate(
            userList = calendarVM.userFriends,
            numberOfFriends = calendarVM.dateInformation.friends,
            loadMoreFriends = calendarVM::getFriendsOnSelectedDate,
            closeDialog = { setShowDialog(false) }
        )



    CalendarScreen(
        total = calendarVM.dateInformation.total,
        friends = calendarVM.dateInformation.friends,
        events = calendarVM.dateInformation.events,
        selectedDate = calendarVM.selectedDate,
        setDate = calendarVM::setDate,
        calendar = calendarVM.calendar,
        userSelectedDates = calendarVM.userDays,
        addUserSelectedDate = calendarVM::addDateToUserList,
        removeUserSelectedDate = calendarVM::removeDateFromUserList,
        userFriendListButton = { setShowDialog(true) }
    )

}

@Composable
private fun UserListOnDate(
    userList: List<UserSnapImage>,
    numberOfFriends: Int,
    loadMoreFriends: () -> Unit,
    closeDialog: () -> Unit
) {

    val state = rememberLazyListState()

    Log.d(
        TAG,
        "UserListOnDate: size: ${userList.size}  state : ${state.firstVisibleItemIndex} "
    )


    /**
     * [LazyListState.firstVisibleItemIndex] points at the number of items already scrolled
     *
     * So if userList is not empty then we check if the remaining users in userList are 15 or less
     * (Full screen of the app),
     * if so, more users from API are fetched
     */
    if (numberOfFriends > 0 &&
        userList.size < numberOfFriends &&
        // total - cursor ( la posicion actual) >= 12  ->( los objetos restantes son 12 ( 9 mostrados en pantalla, 3 restantes por abajo ))
        ( userList.size - state.firstVisibleItemIndex <= 12 ||
                userList.isEmpty() )
    ) {

        loadMoreFriends()
    }



    CustomDialog(onClose = closeDialog) {
        UsersSnapListDialog(
            userList = userList,
            modifier = Modifier.preferredHeight(600.dp),
            listState = state
        )
    }
}

@Composable
private fun CalendarScreen(
    total: Int,
    friends: Int,
    events: List<EventData>,
    selectedDate: MyDate,
    setDate: (MyDate) -> Unit,
    calendar: List<List<MyDate>>,
    userSelectedDates: List<MyDate>,
    addUserSelectedDate: (MyDate) -> Unit,
    removeUserSelectedDate: (MyDate) -> Unit,
    userFriendListButton: () -> Unit
) {
    val colorBackground = MaterialTheme.colors.background //.copy(alpha = 0.2f) not working,
    CalendarPageView(
        calendar = {
            CalendarWindow(
                //Name of the month shown on the top
                monthName = monthName(selectedDate.month),
                colorBackground = colorBackground,
                previousMonthClick = { setDate(selectedDate.previousMonth) },
                nextMonthClick = { setDate(selectedDate.nextMonth) }
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

                                DayChip(
                                    isNextUserDate = userSelectedDates.contains(days),
                                    date = selectedDate,
                                    setDate = setDate,
                                    chipDate = days,
                                    colorBackground = colorBackground
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomInfo = {

            DayInformation(
                formattedDay = selectedDate.toStringFormatted(),
                totalPeople = total.toString(),
                friends = friends.toString(),
                showFriends = userFriendListButton,
                OnChooseDateClick = {
                    if (!userSelectedDates.contains(selectedDate))
                        addUserSelectedDate(selectedDate)
                    else removeUserSelectedDate(selectedDate)
                },
                selectDateEnable = !selectedDate.isBefore(),
                buttonText = if (!selectedDate.isBefore()) {
                    if (userSelectedDates.contains(selectedDate))
                        stringResource(id = R.string.deseleccionar)
                    else stringResource(id = R.string.elegirDia)
                } else stringResource(id = R.string.diaPasado),
                events = {

                    events.forEach { event ->
                        Event(event.barName, event.description)
                    }
                }
            )
        }
    )
}

/**
 * Function that returns true if the date that calls the function is before the actual date
 */
fun MyDate.isBefore(): Boolean {
    return LocalDate.of(this.year, this.month, this.day).isBefore(LocalDate.now())
}

/**
 * Function that returns the name of the month throug a number that represents the month
 *
 * @param month number of the month
 */
@Composable
fun monthName(month: Int): String {
    return when (month) {
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
}

/**
 * Stateless composable that describes the structure of the Calendar View
 * it shows a Composable on the top half [calendar] and some information on the bottom half [bottomInfo]
 *
 *
 * @param calendar
 * @param bottomInfo
 * @param modifierParam
 */
@Composable
fun CalendarPageView(
    calendar: @Composable () -> Unit = {},
    bottomInfo: @Composable () -> Unit = {},
    modifierParam: Modifier = Modifier
) {
    val modifier = modifierParam.fillMaxWidth().fillMaxHeight()
    Column {
        //Top of the screen
        Row(
            modifier = modifier.weight(1.5f),
            horizontalArrangement = Arrangement.Center
        ) {
            //val color = MaterialTheme.colors.background.copy(alpha = 0.2f)
            //ContentColorAmbient.current.copy(alpha = 0.02f)
            calendar()
        }
        //Bottom of the screen
        Row(
            modifier = modifier.weight(1f)
        ) {

            bottomInfo()
        }
    }
}


/**
 * Top View of the calendar
 *
 * @param monthName name of the month to show as title
 * @param previousMonthClick action to do when left arrow is clicked
 * @param nextMonthClick action to do when right arrow is clicked
 * @param contentDay Composable with the days to show on the calendar
 * @param colorBackground is the color of the background
 */
@Composable
fun CalendarWindow(
    monthName: String,
    previousMonthClick: () -> Unit,
    nextMonthClick: () -> Unit,
    colorBackground: Color = MaterialTheme.colors.background,
    contentDay: @Composable () -> Unit = {}
) {

    Surface(
        modifier = Modifier.fillMaxHeight().fillMaxHeight()
            .padding(bottom = 6.dp),
        color = colorBackground,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp)
                .animateContentSize() // automatically animate size when it changes
        ) {

            //Header of the Calendar
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Button previous month
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = previousMonthClick
                ) {
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
                    onClick = nextMonthClick
                ) {
                    Icon(asset = Icons.Default.ArrowForward)
                }
            }

            //Header with the name of the days
            Row {
                Surface(
                    color = AmbientContentColor.current.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 0.dp, bottom = 6.dp, start = 6.dp, end = 6.dp)
                        .clip(RoundedCornerShape(15))
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
                            text = stringResource(id = R.string.lunes_abbreviation),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.martes_abbreviation),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.miercoles_abbreviation),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.jueves_abbreviation),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.viernes_abbreviation),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.sabado_abbreviation),
                            modifier = myModifier
                        )
                        CenteredText(
                            text = stringResource(id = R.string.domingo_abbreviation),
                            modifier = myModifier
                        )
                    }
                }

            }

            Divider(thickness = 1.dp, color = AmbientContentColor.current.copy(alpha = 0.15f))

            val (value, setValue) = remember { mutableStateOf(false) }
            val sensibility = 35 // old 25

            //Calendar indeed
            Box(
                modifier = Modifier

                    .dragGestureFilter(
                        dragObserver = object : DragObserver {

                            override fun onStart(downPosition: Offset) {
                                setValue(true)
                                Log.d(
                                    TAG,
                                    "gesture onStart: offset { x = ${downPosition.x} , y = ${downPosition.y}}"
                                )
                            }

                            override fun onDrag(dragDistance: Offset): Offset {
                                Log.d(
                                    TAG,
                                    "gesture onDrag: offset { x = ${dragDistance.x} , y = ${dragDistance.y}}"
                                )

                                var (x, _) = dragDistance
                                if (value) {
                                    when {
                                        x > sensibility -> {
                                            Log.d(TAG, "gesture previous month")
                                            x = 0f
                                            previousMonthClick()
                                            setValue(false)
                                        }
                                        x < -sensibility -> {
                                            x = 0f
                                            Log.d(TAG, "gesture next month")
                                            nextMonthClick()
                                            setValue(false)
                                        }
                                    }
                                }
                                return Offset(x, 0f)
                            }
                        }

                    )
            ) {

                ScrollableColumn(
                    modifier = Modifier.padding(top = 0.dp, start = 6.dp, end = 6.dp)
                        .padding(bottom = 0.dp)
                ) {

                    contentDay()

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
    textStyle: TextStyle = AmbientTextStyle.current
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
 * if its selected a circle around it will appear, and if the day isn't in the selected month the
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
    isNextUserDate: Boolean = false,
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
        border = when {
            date == chipDate -> BorderStroke(2.dp, MaterialTheme.colors.primary)
            isNextUserDate -> BorderStroke(
                2.dp,
                MaterialTheme.colors.secondary
            )
            else -> null
        },
        elevation = 0.dp,

        color = if (isNextUserDate) MaterialTheme.colors.secondary else colorBackground

    ) {

        Text(
            text = text,
            modifier = textModifier,
            textAlign = textAlign,
            style = style,
            fontSize = fontSize,
            color = when {
                chipDate == date -> colorSelected
                isNextUserDate -> MaterialTheme.colors.onSecondary
                chipDate.month != date.month -> colorNotMonth
                else -> colorNotSelected
            }
        )


    }

}

/**
 * Is the information about the people who is coming out tonight, it will update if the selected date changes
 *
 * @param formattedDay selected date in the format dd/MM/yyyyy
 * @param totalPeople total amount of people who is coming out
 * @param friends total amount of people who is coming out AND you have them added as a friend
 * @param showFriends lambda expression that will update a boolean variable that will show a dialog with your friends
 * @param selectDateEnable enable the button, should be false if the day is before actual date
 * @param buttonText text to show on the button
 * @param OnChooseDateClick action to do when the button is pressed
 * @param events Events to show on that day
 */
@Composable
fun DayInformation(
    formattedDay: String,
    totalPeople: String = "?",
    friends: String = "?",
    showFriends: () -> Unit,
    selectDateEnable: Boolean,
    buttonText: String,
    OnChooseDateClick: () -> Unit,
    events: @Composable () -> Unit,
) {


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
                        onClick = OnChooseDateClick,
                        enabled = selectDateEnable
                    ) {
                        Text(
                            text = buttonText
                        )
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
                        numberOfPeople = friends,
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
                        numberOfPeople = totalPeople,
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
                events()
            }

        }
    }

}

/**
 * Formatted Event
 *
 * @param title name that will be shown as bold text
 * @param description description of the event
 */
@Composable
fun Event(
    title: String,
    description: String
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
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description, modifier = Modifier.padding(top = 1.dp, start = 6.dp),
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
    styleTitle: TextStyle = MaterialTheme.typography.body1,
    styleDescription: TextStyle = MaterialTheme.typography.body2
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = if (numberOfPeople == "-1") "?"
            else numberOfPeople,
            style = styleTitle,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = peopleDescription,
            style = styleDescription
        )
    }
}

@Preview("CalendarWindow")
@Composable
fun CalendarWindowPreview() {

    val date = MyDate(20, 11, 2020)
    val colorBackground = MaterialTheme.colors.background //.copy(alpha = 0.2f) not working,
    CalendarPageView(
        calendar = {
            CalendarWindow(
                //Name of the month shown on the top
                monthName = monthName(date.month),
                colorBackground = colorBackground,
                previousMonthClick = { },
                nextMonthClick = { }
            ) {
                for (week in ChipDayFactory.datesCreator()) {
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

                                DayChip(
                                    isNextUserDate = MyDate(24, 11, 2020) == days,
                                    date = date,
                                    setDate = {},
                                    chipDate = days,
                                    colorBackground = colorBackground
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomInfo = {

            DayInformation(
                formattedDay = StringBuilder(8)
                    .append(date.day)
                    .append("/")
                    .append(date.month)
                    .append("/")
                    .append(date.year)
                    .toString(),
                totalPeople = "150",
                friends = "20",
                showFriends = { },
                OnChooseDateClick = { },
                buttonText = stringResource(id = R.string.elegirDia),
                events = {

                    Event("bar1", "description 1")
                    Event("bar2", "description 2")
                    Event("bar3", "description 3")
                    Event("bar4", "description 4")

                },
                selectDateEnable = true,
            )
        }
    )
}

@Preview("Page")
@Composable
fun CalendarPreview() {
    NightTimeTheme {

        //CalendarPageView(City(0,"Ourense"))
    }
}


@Preview("Dialog")
@Composable
fun DialogPreview() {
    val userList =
        listOf(
            UserSnapImage(1, "Nuria", "pinknut", false, null),
            UserSnapImage(1, "Miguel", "emikepick", false, null),
            UserSnapImage(1, "Maria", "dulceFlor", false, null),
            UserSnapImage(1, "Marcos", "Tigre", false, null),
            UserSnapImage(1, "Laura", "laux21", false, null),
            UserSnapImage(1, "Sara", "saraaldo", false, null),
            UserSnapImage(1, "Julio", "itsme", false, null),
            UserSnapImage(1, "Juan", "john32", false, null),
            UserSnapImage(1, "Pedro", "pcsantiago", false, null),
            UserSnapImage(1, "Salva", "Salvador", false, null),
            UserSnapImage(1, "Gabriel", "grvidal", false, null),
            UserSnapImage(1, "Jose", "JS", false, null),
            UserSnapImage(1, "Emma", "EmmaSant", false, null),
            UserSnapImage(1, "Santi", "santii810", false, null),
            UserSnapImage(1, "Filo", "erfilo", false, null),
            UserSnapImage(1, "Nuria", "pinknut", false, null),
            UserSnapImage(1, "Miguel", "emikepick", false, null),
            UserSnapImage(1, "Maria", "dulceFlor", false, null),
            UserSnapImage(1, "Marcos", "Tigre", false, null),
            UserSnapImage(1, "Laura", "laux21", false, null),
            UserSnapImage(1, "Sara", "saraaldo", false, null),
            UserSnapImage(1, "Julio", "itsme", false, null),
            UserSnapImage(1, "Juan", "john32", false, null),
            UserSnapImage(1, "Pedro", "pcsantiago", false, null),
            UserSnapImage(1, "Salva", "Salvador", false, null),
            UserSnapImage(1, "Gabriel", "grvidal", false, null),
        )
    NightTimeTheme {

        CustomDialog(onClose = {}) {
            UsersSnapListDialog(userList = userList, modifier = Modifier)
        }
    }
}
