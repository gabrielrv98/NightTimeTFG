package com.esei.grvidal.nighttime.pages

import android.view.animation.Animation
import android.widget.LinearLayout
import androidx.compose.animation.Transition
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
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


            Text(text = stringResource(id = R.string.loading), style = MaterialTheme.typography.h4, fontSize = 28.sp , color= Color.Gray)

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
/*
original Loading circle
            Card(
                modifier = Modifier
                    .preferredSize(48.dp),
                shape = CircleShape,
                elevation = 2.dp
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary
                )
            }

 */

@Composable
fun animation() {
    val children: @Composable() () -> Unit = {
        Image(
            imageResource(id = R.drawable.loading_img),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
