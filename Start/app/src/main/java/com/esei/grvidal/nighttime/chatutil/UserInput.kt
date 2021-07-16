/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esei.grvidal.nighttime.chatutil

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focusObserver
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.ui.compositedOnSurface

enum class InputSelector {
    NONE,
    EMOJI
}

@Preview
@Composable
fun UserInputPreview() {
    UserInput(onMessageSent = {}, scrollState = rememberScrollState())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    onMessageSent: (String) -> Unit,
    scrollState: ScrollState
) {
    var currentInputSelector by savedInstanceState { InputSelector.NONE }
    val dismissKeyboard = { currentInputSelector = InputSelector.NONE }


    var textState by remember { mutableStateOf(TextFieldValue()) }

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }

    Column {
        Divider()
        Row {
            UserInputText(
                modifier = Modifier.preferredWidth(200.dp).weight(1f),
                textFieldValue = textState,
                onTextChanged = { textState = it },
                // Only show the keyboard if there's no input selector and text field has focus
                keyboardShown = currentInputSelector == InputSelector.NONE && textFieldFocusState,
                // Close extended selector if text field receives focus
                onTextFieldFocused = { focused ->
                    if (focused) {
                        currentInputSelector = InputSelector.NONE
                        scrollState.smoothScrollTo(0f)
                    }
                    textFieldFocusState = focused
                },
                focusState = textFieldFocusState,
                textStyle = MaterialTheme.typography.body2
            )
            UserInputSelector(
                onSelectorChange = { currentInputSelector = it },
                sendMessageEnabled = textState.text.isNotBlank(),
                onMessageSent = {
                    onMessageSent(textState.text)
                    // Reset text field and close keyboard
                    textState = TextFieldValue()
                    // Move scroll to bottom
                    scrollState.smoothScrollTo(0f)
                    dismissKeyboard()
                },
                currentInputSelector = currentInputSelector
            )
        }

        SelectorExpanded(
            onTextAdded = { textState = textState.addText(it) },
            currentSelector = currentInputSelector
        )
    }
}

private fun TextFieldValue.addText(newString: String): TextFieldValue {
    val newText = this.text.replaceRange(
        this.selection.start,
        this.selection.end,
        newString
    )
    val newSelection = TextRange(
        start = newText.length,
        end = newText.length
    )

    return this.copy(text = newText, selection = newSelection)
}

@OptIn(ExperimentalFocus::class)
@Composable
private fun SelectorExpanded(
    currentSelector: InputSelector,
    onTextAdded: (String) -> Unit
) {
    if (currentSelector == InputSelector.NONE) return

    // Request focus to force the TextField to lose it
    val focusRequester = FocusRequester()
    // If the selector is shown, always request focus to trigger a TextField.onFocusChange.
    onCommit {
        if (currentSelector == InputSelector.EMOJI) {
            focusRequester.requestFocus()
        }
    }
    val selectorExpandedColor = getSelectorExpandedColor()

    Surface(color = selectorExpandedColor, elevation = 3.dp) {
        EmojiSelector(onTextAdded, focusRequester)
    }
}

@Composable
fun getSelectorExpandedColor(): Color {
    return MaterialTheme.colors.compositedOnSurface(alpha = 0.04f)
}

@Composable
private fun UserInputSelector(
    onSelectorChange: (InputSelector) -> Unit,
    sendMessageEnabled: Boolean,
    onMessageSent: () -> Unit,
    currentInputSelector: InputSelector,
) {
    InputSelectorButton(
        onClick = { onSelectorChange(InputSelector.EMOJI) },
        icon = Icons.Outlined.Mood,
        selected = currentInputSelector == InputSelector.EMOJI
    )
    val border = if (!sendMessageEnabled) {
        BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
        )
    } else {
        null
    }

    val disabledContentColor =
        AmbientEmphasisLevels.current.disabled.applyEmphasis(MaterialTheme.colors.onSurface)

    val buttonColors = ButtonConstants.defaultButtonColors(
        disabledBackgroundColor = MaterialTheme.colors.surface,
        disabledContentColor = disabledContentColor
    )

    // Send button
    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .preferredHeight(36.dp)
            .padding(top = 6.dp),
        enabled = sendMessageEnabled,
        onClick = onMessageSent,
        colors = buttonColors,
        border = border,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            stringResource(R.string.send),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun InputSelectorButton(
    onClick: () -> Unit,
    icon: VectorAsset,
    selected: Boolean
) {
    IconButton(
        onClick = onClick
    ) {
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
            val tint = if (selected) MaterialTheme.colors.primary else AmbientContentColor.current
            Icon(
                icon,
                tint = tint,
                modifier = Modifier.padding(vertical = 12.dp)
                    .padding(start = 12.dp, end = 0.dp)
                    .preferredSize(20.dp)
            )
        }
    }
}


@OptIn(ExperimentalFocus::class)
@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean,
    textStyle: TextStyle
) {
    // Grab a reference to the keyboard controller whenever text input starts
    var keyboardController by remember { mutableStateOf<SoftwareKeyboardController?>(null) }

    // Show or hide the keyboard
    onCommit(keyboardController, keyboardShown) { // Guard side-effects against failed commits
        keyboardController?.let {
            if (keyboardShown) it.showSoftwareKeyboard() else it.hideSoftwareKeyboard()
        }
    }

    Row(
        modifier = modifier
            //.fillMaxWidth()
            .preferredHeight(48.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier.preferredHeight(48.dp).weight(1f).align(Alignment.Bottom)
        ) {
            var lastFocusState by remember { mutableStateOf(FocusState.Inactive) }
            BaseTextField(
                value = textFieldValue,
                onValueChange = { onTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .align(Alignment.CenterStart)
                    .focusObserver { state ->
                        if (lastFocusState != state) {
                            onTextFieldFocused(state == FocusState.Active)
                        }
                        lastFocusState = state
                    },
                keyboardType = keyboardType,
                imeAction = ImeAction.Send,
                onTextInputStarted = { controller -> keyboardController = controller },
                textStyle = textStyle
            )

            val disableContentColor =
                AmbientEmphasisLevels.current.disabled.applyEmphasis(MaterialTheme.colors.onSurface)
            if (textFieldValue.text.isEmpty() && !focusState) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp),
                    text = stringResource(id = R.string.TextFieldHint),
                    style = MaterialTheme.typography.body1.copy(color = disableContentColor)
                )
            }
        }
    }
}

@OptIn(ExperimentalFocus::class)
@Composable
fun EmojiSelector(
    onTextAdded: (String) -> Unit,
    focusRequester: FocusRequester
) {
    Column(
        modifier = Modifier
            .focusRequester(focusRequester) // Requests focus when the Emoji selector is displayed
            .focus() // Make the emoji selector focusable so it can steal focus from TextField
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            ExtendedSelectorInnerButton(
                text = "EMOJIS",
                //stringResource(id = R.string.emojis_label),
                selected = true,
                modifier = Modifier.weight(1f)
            )

        }
        ScrollableRow {
            EmojiTable(onTextAdded, modifier = Modifier.padding(8.dp))
        }
    }

}

@Composable
fun ExtendedSelectorInnerButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = ButtonConstants.defaultButtonColors(
        backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
        disabledBackgroundColor = getSelectorExpandedColor(), // Same as background
        contentColor = MaterialTheme.colors.onSurface,
        disabledContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.74f)
    )
    TextButton(
        onClick = {},

        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .preferredHeight(30.dp),
        shape = MaterialTheme.shapes.medium,
        enabled = selected,
        colors = colors,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle2
        )
    }
}

@Composable
fun EmojiTable(
    onTextAdded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        repeat(4) { x ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(EMOJI_COLUMNS) { y ->
                    val emoji = emojis[x * EMOJI_COLUMNS + y]
                    Text(
                        modifier = Modifier
                            .clickable(onClick = { onTextAdded(emoji) })
                            .sizeIn(minWidth = 42.dp, minHeight = 42.dp)
                            .padding(8.dp),
                        text = emoji,
                        style = AmbientTextStyle.current.copy(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

private const val EMOJI_COLUMNS = 10

private val emojis = listOf(
    "\ud83d\ude00", // Grinning Face
    "\ud83d\ude01", // Grinning Face With Smiling Eyes
    "\ud83d\ude02", // Face With Tears of Joy
    "\ud83d\ude03", // Smiling Face With Open Mouth
    "\ud83d\ude04", // Smiling Face With Open Mouth and Smiling Eyes
    "\ud83d\ude05", // Smiling Face With Open Mouth and Cold Sweat
    "\ud83d\ude06", // Smiling Face With Open Mouth and Tightly-Closed Eyes
    "\ud83d\ude09", // Winking Face
    "\ud83d\ude0a", // Smiling Face With Smiling Eyes
    "\ud83d\ude0b", // Face Savouring Delicious Food
    "\ud83d\ude0e", // Smiling Face With Sunglasses
    "\ud83d\ude0d", // Smiling Face With Heart-Shaped Eyes
    "\ud83d\ude18", // Face Throwing a Kiss
    "\ud83d\ude17", // Kissing Face
    "\ud83d\ude19", // Kissing Face With Smiling Eyes
    "\ud83d\ude1a", // Kissing Face With Closed Eyes
    "\u263a", // White Smiling Face
    "\ud83d\ude42", // Slightly Smiling Face
    "\ud83e\udd17", // Hugging Face
    "\ud83d\ude07", // Smiling Face With Halo
    "\ud83e\udd13", // Nerd Face
    "\ud83e\udd14", // Thinking Face
    "\ud83d\ude10", // Neutral Face
    "\ud83d\ude11", // Expressionless Face
    "\ud83d\ude36", // Face Without Mouth
    "\ud83d\ude44", // Face With Rolling Eyes
    "\ud83d\ude0f", // Smirking Face
    "\ud83d\ude23", // Persevering Face
    "\ud83d\ude25", // Disappointed but Relieved Face
    "\ud83d\ude2e", // Face With Open Mouth
    "\ud83e\udd10", // Zipper-Mouth Face
    "\ud83d\ude2f", // Hushed Face
    "\ud83d\ude2a", // Sleepy Face
    "\ud83d\ude2b", // Tired Face
    "\ud83d\ude34", // Sleeping Face
    "\ud83d\ude0c", // Relieved Face
    "\ud83d\ude1b", // Face With Stuck-Out Tongue
    "\ud83d\ude1c", // Face With Stuck-Out Tongue and Winking Eye
    "\ud83d\ude1d", // Face With Stuck-Out Tongue and Tightly-Closed Eyes
    "\ud83d\ude12", // Unamused Face
    "\ud83d\ude13", // Face With Cold Sweat
    "\ud83d\ude14", // Pensive Face
    "\ud83d\ude15", // Confused Face
    "\ud83d\ude43", // Upside-Down Face
    "\ud83e\udd11", // Money-Mouth Face
    "\ud83d\ude32", // Astonished Face
    "\ud83d\ude37", // Face With Medical Mask
    "\ud83e\udd12", // Face With Thermometer
    "\ud83e\udd15", // Face With Head-Bandage
    "\u2639", // White Frowning Face
    "\ud83d\ude41", // Slightly Frowning Face
    "\ud83d\ude16", // Confounded Face
    "\ud83d\ude1e", // Disappointed Face
    "\ud83d\ude1f", // Worried Face
    "\ud83d\ude24", // Face With Look of Triumph
    "\ud83d\ude22", // Crying Face
    "\ud83d\ude2d", // Loudly Crying Face
    "\ud83d\ude26", // Frowning Face With Open Mouth
    "\ud83d\ude27", // Anguished Face
    "\ud83d\ude28", // Fearful Face
    "\ud83d\ude29", // Weary Face
    "\ud83d\ude2c", // Grimacing Face
    "\ud83d\ude30", // Face With Open Mouth and Cold Sweat
    "\ud83d\ude31", // Face Screaming in Fear
    "\ud83d\ude33", // Flushed Face
    "\ud83d\ude35", // Dizzy Face
    "\ud83d\ude21", // Pouting Face
    "\ud83d\ude20", // Angry Face
    "\ud83d\ude08", // Smiling Face With Horns
    "\ud83d\udc7f", // Imp
    "\ud83d\udc79", // Japanese Ogre
    "\ud83d\udc7a", // Japanese Goblin
    "\ud83d\udc80", // Skull
    "\ud83d\udc7b", // Ghost
    "\ud83d\udc7d", // Extraterrestrial Alien
    "\ud83e\udd16", // Robot Face
    "\ud83d\udca9", // Pile of Poo
    "\ud83d\ude3a", // Smiling Cat Face With Open Mouth
    "\ud83d\ude38", // Grinning Cat Face With Smiling Eyes
    "\ud83d\ude39", // Cat Face With Tears of Joy
    "\ud83d\ude3b", // Smiling Cat Face With Heart-Shaped Eyes
    "\ud83d\ude3c", // Cat Face With Wry Smile
    "\ud83d\ude3d", // Kissing Cat Face With Closed Eyes
    "\ud83d\ude40", // Weary Cat Face
    "\ud83d\ude3f", // Crying Cat Face
    "\ud83d\ude3e", // Pouting Cat Face
    "\ud83d\udc66", // Boy
    "\ud83d\udc67", // Girl
    "\ud83d\udc68", // Man
    "\ud83d\udc69", // Woman
    "\ud83d\udc74", // Older Man
    "\ud83d\udc75", // Older Woman
    "\ud83d\udc76", // Baby
    "\ud83d\udc71", // Person With Blond Hair
    "\ud83d\udc6e", // Police Officer
    "\ud83d\udc72", // Man With Gua Pi Mao
    "\ud83d\udc73", // Man With Turban
    "\ud83d\udc77", // Construction Worker
    "\u26d1", // Helmet With White Cross
    "\ud83d\udc78", // Princess
    "\ud83d\udc82", // Guardsman
    "\ud83d\udd75", // Sleuth or Spy
    "\ud83c\udf85", // Father Christmas
    "\ud83d\udc70", // Bride With Veil
    "\ud83d\udc7c", // Baby Angel
    "\ud83d\udc86", // Face Massage
    "\ud83d\udc87", // Haircut
    "\ud83d\ude4d", // Person Frowning
    "\ud83d\ude4e", // Person With Pouting Face
    "\ud83d\ude45", // Face With No Good Gesture
    "\ud83d\ude46", // Face With OK Gesture
    "\ud83d\udc81", // Information Desk Person
    "\ud83d\ude4b", // Happy Person Raising One Hand
    "\ud83d\ude47", // Person Bowing Deeply
    "\ud83d\ude4c", // Person Raising Both Hands in Celebration
    "\ud83d\ude4f", // Person With Folded Hands
    "\ud83d\udde3", // Speaking Head in Silhouette
    "\ud83d\udc64", // Bust in Silhouette
    "\ud83d\udc65", // Busts in Silhouette
    "\ud83d\udeb6", // Pedestrian
    "\ud83c\udfc3", // Runner
    "\ud83d\udc6f", // Woman With Bunny Ears
    "\ud83d\udc83", // Dancer
    "\ud83d\udd74", // Man in Business Suit Levitating
    "\ud83d\udc6b", // Man and Woman Holding Hands
    "\ud83d\udc6c", // Two Men Holding Hands
    "\ud83d\udc6d", // Two Women Holding Hands
    "\ud83d\udc8f" // Kiss
)
