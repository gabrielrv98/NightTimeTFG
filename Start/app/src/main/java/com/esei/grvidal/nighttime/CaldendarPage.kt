package com.esei.grvidal.nighttime

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import com.esei.grvidal.nighttime.data.Usuario
import com.esei.grvidal.nighttime.ui.NightTimeTheme
import java.time.LocalDate

/**
 * Show the Calendar page, with the calendar on the top and the information below it
 */
@Composable
fun CalendarPageView() {

    val modifier = Modifier.fillMaxWidth().fillMaxHeight()
    //remember date, it's used to show the selected date and move the calendar to the specified month
    val (date, setDate) = remember { mutableStateOf(MyDay(LocalDate.now().dayOfMonth, LocalDate.now().month.value, LocalDate.now().year)) }
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

    val submit = {setShowDialog(true)}

    Column {

        //If Friendly users Card is touched a dialog with their names should be shown
        if (showDialog)
            FriendlyUsersDialog(onClose = { setShowDialog(false) },
                //todo esto esta hardcodeado
                listOf(
                    Usuario(name="Nuria"),
                    Usuario(name="Miguel"),
                    Usuario(name="Maria"),
                    Usuario(name="Marcos"),
                    Usuario(name="Laura"),
                    Usuario(name="Sara"),
                    Usuario(name="Julio"),
                    Usuario(name="Juan"),
                    Usuario(name="Pedro"),
                    Usuario(name="Salva"),
                    Usuario(name="Gabriel"),
                    Usuario(name="Jose"),
                    Usuario(name="Emma"),
                    Usuario(name="Santi"),
                    Usuario(name="Filo"),
                    Usuario(name="Nuria"),
                    Usuario(name="Miguel"),
                    Usuario(name="Maria"),
                    Usuario(name="Marcos"),
                    Usuario(name="Laura"),
                    Usuario(name="Sara"),
                    Usuario(name="Julio"),
                    Usuario(name="Juan"),
                    Usuario(name="Pedro"),
                    Usuario(name="Salva"),
                    Usuario(name="Gabriel"),
                    Usuario(name="Jose"),
                    Usuario(name="Emma"),
                    Usuario(name="Santi"),
                    Usuario(name="Filo"),

                )
            )


        Row(
            modifier = modifier.weight(1.4f),
            horizontalArrangement = Arrangement.Center
        ) {
            CalendarWindow(date = date, setDate = mySetDay, calendar = calendar)
        }

        //todo Eliminar este Divider es solo una referencia
        Row {
            Surface(
                modifier = Modifier//.fillMaxWidth()
                    .preferredHeight(1.dp).weight(1f),
                color = MaterialTheme.colors.primary,
                elevation = 1.dp,
                content = {}
            )

        }


        Row(modifier = modifier.weight(1f).padding(bottom = 50.dp)) {

            DayInformation(showFriends = submit)
        }

    }

}

@Composable
fun FriendlyUsersDialog(
    onClose : () -> Unit,
    itemsUser : List<Usuario>
){
    Dialog(onDismissRequest = onClose){

        Surface(
            modifier = Modifier.clip(MaterialTheme.shapes.medium)
                .padding(24.dp),
            color = MaterialTheme.colors.background,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(3.dp, MaterialTheme.colors.primary)
        ){
            Column(
                modifier = Modifier.padding(12.dp)
            ){
                LazyColumnFor(items = itemsUser,
                modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp)
                    ){
                        Surface(
                            modifier = Modifier.preferredSize(20.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                        ) {
                            // Image goes here
                        }

                        Column(modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically)){
                            Text(text=(it.name + " - Apellidos"))
                        }
                    }

                }

                Button(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .align(Alignment.End),
                    onClick = onClose
                ) {
                    Text(text = "Cerrar")
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

    Surface(
        modifier = Modifier.fillMaxHeight().fillMaxHeight(),
        color = colorBackground,
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Surface(
                color = ContentColorAmbient.current.copy(alpha = 0.15f),
                modifier = Modifier.padding(6.dp)
            ) {
                val myModifier = Modifier.padding(horizontal = 15.dp)
                    .weight(1f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 9.dp).padding(top = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        stringResource(id = R.string.lunes),
                        modifier = myModifier
                    )
                    Text(
                        stringResource(id = R.string.martes),
                        modifier = myModifier
                    )
                    Text(
                        stringResource(id = R.string.miercoles),
                        modifier = myModifier
                    )
                    Text(
                        stringResource(id = R.string.jueves),
                        modifier = myModifier
                    )
                    Text(
                        stringResource(id = R.string.viernes),
                        modifier = myModifier
                    )
                    Text(
                        stringResource(id = R.string.sabado),
                        modifier = myModifier
                    )
                    Text(
                        stringResource(id = R.string.domingo),
                        modifier = myModifier
                    )
                }
            }

            Divider(thickness = 1.dp)

            LazyColumnFor(
                items = calendar,
                modifier = Modifier.padding(top = 0.dp, start = 6.dp, end = 6.dp)
                    .padding(bottom = 0.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(vertical = 9.dp)
                ) {
                    for (days in it) {

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
    showFriends: () -> Unit
) {

    Row(
        modifier = Modifier.padding(vertical = 12.dp)
            .padding(horizontal = 6.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxHeight().weight(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            InfoChip(info = amigos, text = "Amigos", onClick = showFriends)
            InfoChip(info = genteTotal, text = "Gente total")
        }


        //---------------------------------------------------------

        Column(
            modifier = Modifier.weight(1.4f)
                .fillMaxHeight()
                .padding(start = 6.dp),
            horizontalAlignment = Alignment.End
        ) {

            Surface(
                modifier = Modifier.fillMaxHeight(),
                border = BorderStroke(2.dp, MaterialTheme.colors.primary),
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(8.dp)
            ) {
                ScrollableColumn(modifier = Modifier.padding(horizontal = 12.dp, vertical =  6.dp)) {

                    Event("Lazaros","Copas a 3 euros")

                    Event("Lokal","Musica de los 90")

                    Event("Patio andaluz","Fiesta de la espuma")
                    Event("Luxus","Hoy cerrado por fiesta infantil, nos vemos gente")
                    Event("Urbe","Cocaina gratis")
                    Event("Cueva hentai","Nuria atada a la pared con el culo en pompa y el ano disponible")

                }

            }
        }
    }
}

@Composable
fun Event(barName: String, eventDescription: String){
    Surface(modifier = Modifier.fillMaxWidth()
        .padding( vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)
    ){
        Row(
            modifier = Modifier.padding(12.dp)
        ){
            Text(text = barName,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
            Text(text = eventDescription, modifier = Modifier.padding(top = 1.dp, start = 6.dp),
                style = MaterialTheme.typography.body2)
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
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
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
    val (date, setDate) = remember { mutableStateOf(MyDay(LocalDate.now().dayOfMonth, LocalDate.now().month.value, LocalDate.now().year)) }
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
        DayInformation(showFriends = {})
    }
}