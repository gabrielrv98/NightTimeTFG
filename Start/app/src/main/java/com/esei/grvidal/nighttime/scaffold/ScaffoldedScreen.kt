package com.esei.grvidal.nighttime.scaffold

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.CustomDialog
import com.esei.grvidal.nighttime.data.City

/**
 * Screen with the Scaffolded format, with the topBar with the button to change the city
 * and the bottomBar with the navigation buttons
 *
 * @param modifier Modifier of the Screen
 * @param bottomBar  content to be shown on the bottom of the screen
 * @param topBar content to be shown on the top of the screen
 * @param content content to be shown on the center of the screen
 */
@Composable
fun ScreenScaffolded(
    modifier : Modifier = Modifier.padding(bottom = 57.dp),//TODO Bottom padding of the size of the bottomBar
    bottomBar: @Composable () -> Unit = { },
    topBar: @Composable () -> Unit = { },
    content: @Composable () -> Unit,

    ) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = bottomBar
    ) {

        Column(
            modifier = modifier
        ) {
            content()
        }


    }
}

/**
 * City Dialog stateful function, it will set the parameters to show CityDialog
 *
 * @param cityDialog Boolean to show or hide cityDialog
 * @param items List of items to be Shown
 * @param setCityDialog
 */
@Composable
fun CityDialogConstructor(
    cityDialog: Boolean,
    items: List<City> ,
    setCityDialog: (Boolean) -> Unit,
    setCityId: (Long,String) -> Unit
) {
    if (cityDialog) {
        CustomDialog(onClose = { setCityDialog(false) }) {
            CityDialog(
                items = items,
                onItemClick = { city ->
                    setCityId(city.id,city.name)
                    setCityDialog(false)
                }
            )
        }
    }
}


/**
 * Dialog with the cities that can be selected
 *
 * @param items list of the cities
 * @param onItemClick Unit to do when an item is selected
 */
@Composable
fun CityDialog(
    items: List<City>,
    onItemClick: (City) -> Unit
) {
    LazyColumnFor(items = items) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
        ) {
            Surface(
                modifier = Modifier
                    .preferredWidth(120.dp)
                    .clickable(onClick = { onItemClick(it) } ),
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