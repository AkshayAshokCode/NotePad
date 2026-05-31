package com.akshayashokcode.notepad.feature_note.data.repository

import com.akshayashokcode.notepad.feature_note.domain.model.Note
import com.akshayashokcode.notepad.feature_note.domain.repository.NoteRepository
import com.akshayashokcode.notepad.feature_note.domain.util.NoteOrder
import com.akshayashokcode.notepad.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeNoteRepository : NoteRepository {

    private val notes = mutableListOf<Note>()

    override fun getNotes(noteOrder: NoteOrder, searchQuery: String, colorFilter: Int?): Flow<List<Note>> {
        return flow {
            val filtered = notes
                .filter { !it.isArchived }
                .let { list ->
                    if (searchQuery.isBlank()) list
                    else list.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.content.contains(searchQuery, ignoreCase = true)
                    }
                }
                .let { list -> if (colorFilter != null) list.filter { it.color == colorFilter } else list }

            val sorted = when (noteOrder.orderType) {
                is OrderType.Ascending -> when (noteOrder) {
                    is NoteOrder.Title -> filtered.sortedBy { it.title.lowercase() }
                    is NoteOrder.Date -> filtered.sortedBy { it.timeStamp }
                    is NoteOrder.Color -> filtered.sortedBy { it.color }
                }
                is OrderType.Descending -> when (noteOrder) {
                    is NoteOrder.Title -> filtered.sortedByDescending { it.title.lowercase() }
                    is NoteOrder.Date -> filtered.sortedByDescending { it.timeStamp }
                    is NoteOrder.Color -> filtered.sortedByDescending { it.color }
                }
            }
            emit(sorted.sortedByDescending { it.isPinned })
        }
    }

    override fun getArchivedNotes(): Flow<List<Note>> {
        return flow { emit(notes.filter { it.isArchived }) }
    }

    override suspend fun getNoteById(id: Int): Note? = notes.find { it.id == id }

    override suspend fun insertNote(note: Note) {
        val existingIndex = notes.indexOfFirst { it.id == note.id }
        if (existingIndex >= 0) notes[existingIndex] = note else notes.add(note)
    }

    override suspend fun deleteNote(note: Note) { notes.remove(note) }
}
