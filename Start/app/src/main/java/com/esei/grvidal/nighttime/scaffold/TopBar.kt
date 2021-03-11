package com.esei.grvidal.nighttime.scaffold

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
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.R
import java.util.*

@Composable
fun TopBarConstructor(
    buttonText : String,
    title : String = stringResource(id = R.string.app_name),
    icon: VectorAsset? = null,
    action : () -> Unit = {}
){
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            CityButton(buttonText, icon ,action)
        }
    )
}

@Composable
fun CityButton(
    nameCity: String,
    icon: VectorAsset?,
    setCityDialog: () -> Unit
){
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(25))
            .clickable(onClick = setCityDialog),
        color = MaterialTheme.colors.primary
    ) {
        Row {
            Text(
                modifier = Modifier.padding(6.dp),
                text = nameCity.toUpperCase(Locale.getDefault()),
                maxLines = 1
            )

            icon?.let{
                Icon(
                    modifier = Modifier.padding(6.dp),
                    asset = it
                )
            }

        }
    }
}