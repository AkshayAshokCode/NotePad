package com.akshayashokcode.notepad.feature_note.domain.use_case

import com.akshayashokcode.notepad.feature_note.domain.model.Note
import com.akshayashokcode.notepad.feature_note.domain.repository.NoteRepository
import com.akshayashokcode.notepad.feature_note.domain.util.NoteOrder
import com.akshayashokcode.notepad.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow

class GetNotes(
    private val repository: NoteRepository
) {
    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
        searchQuery: String = "",
        colorFilter: Int? = null
    ): Flow<List<Note>> {
        return repository.getNotes(noteOrder, searchQuery, colorFilter)
    }
}
