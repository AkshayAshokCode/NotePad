package com.akshayashokcode.notepad.feature_note.data.data_source

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.akshayashokcode.notepad.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @RawQuery(observedEntities = [Note::class])
    fun getNotes(query: SupportSQLiteQuery): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE isArchived = 1 ORDER BY timeStamp DESC")
    fun getArchivedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}
