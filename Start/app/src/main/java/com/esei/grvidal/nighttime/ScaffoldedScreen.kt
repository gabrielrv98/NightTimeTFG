package com.esei.grvidal.nighttime

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.esei.grvidal.nighttime.data.City
import com.esei.grvidal.nighttime.data.CityDao
import java.util.*

/**
 * Screen with the Scaffolded format, with the topBar with the button to change the city
 * and the bottomBar with the navigation buttons
 *
 * @param navController Controller of the navigation
 * @param items list of the selectable navigation buttons
 * @param content content to be shown on the center of the screen
 */
@Composable
fun ScreenScaffolded(
    navController : NavHostController,
    items : List<BottomNavigationScreens>,
    content: @Composable (City) -> Unit
) {
    val (cityDialog, setCityDialog) = remember { mutableStateOf(false) }
    val (cityId, setCityId) = remember {
        mutableStateOf(CityDao().getAllCities()[0])
    }//todo cambiar, inicia siempre en ourense, deberia ser con sharedPreferences o algo asi

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "NightTime")
                },
                actions = {
                    Surface(
                        modifier = Modifier
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
        bottomBar = { bottomBarNavigation(navController,items) }
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


/**
 * Dialog with the cities that can be selected
 *
 * @param items list of the cities
 * @param editCity setter of the selected city
 */
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
                    .preferredWidth(120.dp)
                    .clickable(onClick = { editCity(it) } ),
                color = MaterialTheme.colors.background
            ) {
                Text(
                    modifier = Modifier,
                    text = it.name
                )
            }
        }
    }
}