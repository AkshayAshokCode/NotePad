package com.akshayashokcode.notepad.feature_note.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.akshayashokcode.notepad.feature_note.data.data_source.NoteDao
import com.akshayashokcode.notepad.feature_note.domain.model.Note
import com.akshayashokcode.notepad.feature_note.domain.repository.NoteRepository
import com.akshayashokcode.notepad.feature_note.domain.util.NoteOrder
import com.akshayashokcode.notepad.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository {

    override fun getNotes(
        noteOrder: NoteOrder,
        searchQuery: String,
        colorFilter: Int?
    ): Flow<List<Note>> {
        val column = when (noteOrder) {
            is NoteOrder.Title -> "title"
            is NoteOrder.Date -> "timeStamp"
            is NoteOrder.Color -> "color"
        }
        val direction = when (noteOrder.orderType) {
            is OrderType.Ascending -> "ASC"
            is OrderType.Descending -> "DESC"
        }

        val conditions = mutableListOf("isArchived = 0")
        val args = mutableListOf<Any>()

        if (searchQuery.isNotBlank()) {
            conditions.add("(title LIKE ? OR content LIKE ?)")
            val pattern = "%$searchQuery%"
            args.add(pattern)
            args.add(pattern)
        }
        if (colorFilter != null) {
            conditions.add("color = ?")
            args.add(colorFilter)
        }

        val sql = "SELECT * FROM note WHERE ${conditions.joinToString(" AND ")} " +
                "ORDER BY isPinned DESC, $column $direction"
        return dao.getNotes(SimpleSQLiteQuery(sql, args.toTypedArray()))
    }

    override fun getArchivedNotes(): Flow<List<Note>> = dao.getArchivedNotes()

    override suspend fun getNoteById(id: Int): Note? = dao.getNoteById(id)

    override suspend fun insertNote(note: Note) = dao.insertNote(note)

    override suspend fun deleteNote(note: Note) = dao.deleteNote(note)
}
