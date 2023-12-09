package com.akshayashokcode.notepad.feature_note.presentation.util

sealed class Screen(val route:String){
    data object NotesScreen:Screen("notes_screen")
    data object AddEditNoteScreen:Screen("add_edit_note_screen")
}
