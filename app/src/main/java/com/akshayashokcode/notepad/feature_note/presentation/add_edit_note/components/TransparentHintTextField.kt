package com.akshayashokcode.notepad.feature_note.presentation.add_edit_note.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization

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
    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = onValueChange,
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
