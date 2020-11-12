package com.esei.grvidal.nighttime

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.data.City
import com.esei.grvidal.nighttime.data.CityDao
import java.util.*


@Composable
fun ScreenScaffolded(
    icon: NavButtonsIcon,
    setIcon: (NavButtonsIcon) -> Unit,
    content: @Composable (City) -> Unit
) {
    val (cityDialog, setCityDialog) = remember { mutableStateOf(false) }
    val (cityId, setCityId) = remember {
        mutableStateOf(CityDao().getAllCities()[0])
    }//todo cambiar

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "NightTime")
                },
                actions = {
                    Surface(
                        modifier = Modifier//.padding(end = (50).dp),
                            .clip(RoundedCornerShape(25))
                            .clickable(onClick = { setCityDialog(true) }),
                        color = MaterialTheme.colors.primary
                    ) {
                        Row{
                            Text(
                                modifier = Modifier.padding(6.dp),
                                text = cityId.name.toUpperCase(Locale.getDefault()),
                                maxLines = 1
                            )
                            Icon(
                                modifier = Modifier.padding(6.dp),
                                asset = Icons.Default.Search
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            bottomBar(icon, setIcon)
        }
    ) {

        if (cityDialog) {
            CustomDialog(onClose = { setCityDialog(false) }) {
                CityDialog(
                    items = CityDao().getAllCities(),
                    editCity = {city ->
                        setCityId(city)
                        setCityDialog(false)}
                )
            }
        }

        val fillMaxModifier = Modifier.fillMaxSize()
        Surface(
            modifier = fillMaxModifier.padding(bottom = 57.dp),//TODO Bottom padding of the size of the bottomBar
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = fillMaxModifier
            ) {
                content(cityId)
            }

        }


    }
}


@Composable
fun CityDialog(
    items: List<City>,
    editCity: (City) -> Unit
) {
    LazyColumnFor(items = items) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
        ) {
            Surface(
                modifier = Modifier
                    .clickable(onClick = { editCity(it) } ),
                color = MaterialTheme.colors.background
            ) {
                Text(
                    text = it.name
                )
            }
        }
    }
}