package com.akshayashokcode.notepad.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    val title: String,
    val content: String,
    val timeStamp: Long,
    val color: Int,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    @PrimaryKey val id: Int? = null
) {
    class InvalidNoteException(message: String) : Exception(message)
}
