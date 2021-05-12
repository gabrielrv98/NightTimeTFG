package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun ErrorPage(message: String) {

    Box(
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.Center
    ) {
        Text(text = message)
    }
}