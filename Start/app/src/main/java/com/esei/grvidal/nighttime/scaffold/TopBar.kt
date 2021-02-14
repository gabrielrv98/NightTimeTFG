package com.esei.grvidal.nighttime

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun TopBarConstructor(
    title : String = stringResource(id = R.string.app_name),
    setCityDialog : (Boolean) -> Unit,
    nameCity : String
){
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            CityButton(setCityDialog,nameCity)
        }
    )
}

@Composable
fun CityButton(setCityDialog: (Boolean) -> Unit, nameCity : String ){
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(25))
            .clickable(onClick = { setCityDialog(true) }),
        color = MaterialTheme.colors.primary
    ) {
        Row {
            Text(
                modifier = Modifier.padding(6.dp),
                text = nameCity.toUpperCase(Locale.getDefault()),
                maxLines = 1
            )
            Icon(
                modifier = Modifier.padding(6.dp),
                asset = Icons.Default.Search
            )
        }
    }
}