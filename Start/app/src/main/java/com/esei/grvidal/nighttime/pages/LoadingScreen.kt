package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esei.grvidal.nighttime.R

/**
 * check https://proandroiddev.com/rotating-pokeball-animation-with-jetpack-compose-e3e839782cba
 *
 * or check Canvas()
 */
@Composable
fun LoadingScreen() {


    Box(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
        alignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = stringResource(id = R.string.loading),
                style = MaterialTheme.typography.h4,
                fontSize = 28.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.size(15.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colors.primary
            )

            Spacer(modifier = Modifier.size(5.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(35.dp),
                color = MaterialTheme.colors.primary,
                strokeWidth = 3.dp

            )

            Spacer(modifier = Modifier.size(5.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colors.primary,
                strokeWidth = 2.dp
            )


        }
    }

}
