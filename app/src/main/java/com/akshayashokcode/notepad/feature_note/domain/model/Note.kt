package com.akshayashokcode.notepad.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akshayashokcode.notepad.ui.theme.BabyBlue
import com.akshayashokcode.notepad.ui.theme.Cream
import com.akshayashokcode.notepad.ui.theme.Lavender
import com.akshayashokcode.notepad.ui.theme.LightGreen
import com.akshayashokcode.notepad.ui.theme.LightOrange
import com.akshayashokcode.notepad.ui.theme.LightPink
import com.akshayashokcode.notepad.ui.theme.LightPurple
import com.akshayashokcode.notepad.ui.theme.LightYellow
import com.akshayashokcode.notepad.ui.theme.MintGreen
import com.akshayashokcode.notepad.ui.theme.PaleGreen
import com.akshayashokcode.notepad.ui.theme.RedOrange
import com.akshayashokcode.notepad.ui.theme.RedPink
import com.akshayashokcode.notepad.ui.theme.Violet

@Entity
data class Note(
    val title: String,
    val content: String,
    val timeStamp: Long,
    val color: Int,
    @PrimaryKey val id: Int? = null
) {
    companion object {
        val noteColors = listOf(
            RedOrange,
            LightGreen,
            Violet,
            BabyBlue,
            RedPink,
            LightYellow,
            LightOrange,
            MintGreen,
            LightPurple,
            Lavender,
            LightPink,
            PaleGreen,
            Cream
        )
    }

    class InvalidNoteException(message: String) : Exception(message)
}
