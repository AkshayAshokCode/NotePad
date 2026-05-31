package com.akshayashokcode.notepad.feature_note.domain.use_case

data class NoteUseCases(
    val getNotes: GetNotes,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote,
    val archiveNote: ArchiveNote,
    val unarchiveNote: UnarchiveNote,
    val getArchivedNotes: GetArchivedNotes
)
