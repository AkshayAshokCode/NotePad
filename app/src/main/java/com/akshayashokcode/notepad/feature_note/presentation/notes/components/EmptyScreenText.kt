package com.akshayashokcode.notepad.feature_note.presentation.notes.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
    fun EmptyScreenText(
    modifier:Modifier=Modifier
) {
    Column( modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(text ="No notes",
            style = TextStyle(color= Color.LightGray,fontStyle = MaterialTheme.typography.h6.fontStyle,
                fontSize = MaterialTheme.typography.h6.fontSize,fontWeight = MaterialTheme.typography.h6.fontWeight,
                fontFamily = FontFamily.SansSerif
            )
        )
        Text(text ="Tap the Add button to create a note",
            style = TextStyle(color=Color.Gray,fontStyle = MaterialTheme.typography.subtitle2.fontStyle,
                fontSize = MaterialTheme.typography.subtitle2.fontSize,fontWeight = MaterialTheme.typography.subtitle2.fontWeight,
                fontFamily = FontFamily.Default
            )
        )
    }


}