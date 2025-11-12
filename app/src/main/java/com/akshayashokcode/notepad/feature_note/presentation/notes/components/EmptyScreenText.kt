package com.akshayashokcode.notepad.feature_note.presentation.notes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun EmptyScreenText(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(
            text = "No notes",
            style = TextStyle(
                color = Color.Gray,
                fontStyle = MaterialTheme.typography.headlineSmall.fontStyle,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                fontFamily = FontFamily.SansSerif
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Tap the Add button to create a note",
            style = TextStyle(
                color = Color.Gray,
                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                fontFamily = FontFamily.Default
            )
        )
    }


}