package com.akshayashokcode.notepad.feature_note.presentation.notes

import com.akshayashokcode.notepad.feature_note.domain.model.Note
import com.akshayashokcode.notepad.feature_note.domain.util.NoteOrder

sealed class NotesEvent {
    data class Order(val noteOrder: NoteOrder) : NotesEvent()
    data class DeleteNote(val note: Note) : NotesEvent()
    data class ArchiveNote(val note: Note) : NotesEvent()
    data class SearchQuery(val query: String) : NotesEvent()
    data class FilterColor(val color: Int?) : NotesEvent()
    data object RestoreNote : NotesEvent()
    data object UndoArchive : NotesEvent()
    data object ToggleOrderSection : NotesEvent()
    data object CloseOrderSection : NotesEvent()
    data class TogglePin(val note: Note) : NotesEvent()
    data object ToggleLayout : NotesEvent()
}
