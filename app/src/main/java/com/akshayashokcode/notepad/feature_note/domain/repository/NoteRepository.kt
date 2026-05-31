package com.akshayashokcode.notepad.feature_note.domain.repository

import com.akshayashokcode.notepad.feature_note.domain.model.Note
import com.akshayashokcode.notepad.feature_note.domain.util.NoteOrder
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(
        noteOrder: NoteOrder,
        searchQuery: String = "",
        colorFilter: Int? = null
    ): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Int): Note?
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
}
