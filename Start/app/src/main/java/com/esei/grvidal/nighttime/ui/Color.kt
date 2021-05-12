package com.esei.grvidal.nighttime.ui

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

val purple200 = Color(0xFFBB86FC)
val purple500 = Color(0xFF6200EE)
val purple700 = Color(0xFF3700B3)
val teal200 = Color(0xFF03DAC5)
val darkWhite = Color (0xFFF4FFFF)
val grayWhite = Color(0xFFF7F7F7)
val grayBlue = Color(0xFFB9E1E1) //primary variant

/**
 * Return the fully opaque color that results from compositing [Colors.onSurface] on top [Colors.surface] with the
 * given [alpha]. Useful for situations where semi-transparent colors are undesirable.
 */
@Composable
fun Colors.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(surface)
}
