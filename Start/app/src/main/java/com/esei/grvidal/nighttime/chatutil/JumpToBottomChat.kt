package com.esei.grvidal.nighttime.chatutil


import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.R

private val bottomOffset = DpPropKey("Bottom Offset")

private val definition = transitionDefinition<Visibility> {
    state(Visibility.GONE) {
        this[bottomOffset] = (-32).dp
    }
    state(Visibility.VISIBLE) {
        this[bottomOffset] = 32.dp
    }
}

private enum class Visibility {
    VISIBLE,
    GONE
}

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
fun JumpToBottom(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Show Jump to Bottom button
    val transition = transition(
        definition = definition,
        toState = if (enabled) Visibility.VISIBLE else Visibility.GONE
    )
    if (transition[bottomOffset] > 0.dp) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    asset = Icons.Filled.ArrowDownward,
                    modifier = Modifier.preferredHeight(18.dp)
                )
            },
            text = {
                Text(text = stringResource(id = R.string.jump_to_bottom))
            },
            onClick = onClicked,
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.primary,
            modifier = modifier
                .offset(x = 0.dp, y = -transition[bottomOffset])
                .preferredHeight(36.dp)
        )
    }
}

@Preview
@Composable
fun JumpToBottomPreview() {
    JumpToBottom(enabled = true, onClicked = {})
}
