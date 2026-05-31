package com.akshayashokcode.notepad.feature_note.presentation.add_edit_note.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun TransparentHintTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    expandToFillHeight: Boolean = false,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    testTag: String = "",
    onFocusChange: (FocusState) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }

    // Sync external text changes without resetting cursor position
    LaunchedEffect(text) {
        if (text != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = text)
        }
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onValueChange(newValue.text)
            },
            singleLine = singleLine,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = capitalization),
            modifier = Modifier
                .testTag(testTag)
                .then(if (expandToFillHeight) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
                .onFocusChanged { onFocusChange(it) }
        )
        if (isHintVisible) {
            Text(text = hint, style = textStyle, color = textStyle.color.copy(alpha = 0.45f))
        }
    }
}
