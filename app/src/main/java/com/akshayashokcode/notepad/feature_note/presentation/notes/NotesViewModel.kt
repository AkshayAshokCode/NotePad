package com.akshayashokcode.notepad.feature_note.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.notepad.feature_note.domain.model.Note
import com.akshayashokcode.notepad.feature_note.domain.use_case.NoteUseCases
import com.akshayashokcode.notepad.feature_note.domain.util.NoteOrder
import com.akshayashokcode.notepad.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private var recentlyDeletedNote: Note? = null
    private var recentlyArchivedNote: Note? = null
    private var getNotesJob: Job? = null

    init {
        refreshNotes()
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) return
                _state.value = state.value.copy(noteOrder = event.noteOrder)
                refreshNotes()
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
                if (state.value.isOrderSectionVisible) {
                    _state.value = state.value.copy(isOrderSectionVisible = false)
                }
            }
            is NotesEvent.ArchiveNote -> {
                viewModelScope.launch {
                    noteUseCases.archiveNote(event.note)
                    recentlyArchivedNote = event.note
                }
                if (state.value.isOrderSectionVisible) {
                    _state.value = state.value.copy(isOrderSectionVisible = false)
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NotesEvent.UndoArchive -> {
                viewModelScope.launch {
                    noteUseCases.unarchiveNote(recentlyArchivedNote ?: return@launch)
                    recentlyArchivedNote = null
                }
            }
            is NotesEvent.SearchQuery -> {
                _state.value = state.value.copy(searchQuery = event.query)
                refreshNotes()
            }
            is NotesEvent.FilterColor -> {
                val newFilter = if (state.value.colorFilter == event.color) null else event.color
                _state.value = state.value.copy(colorFilter = newFilter)
                refreshNotes()
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
            is NotesEvent.CloseOrderSection -> {
                _state.value = state.value.copy(isOrderSectionVisible = false)
            }
            is NotesEvent.TogglePin -> {
                viewModelScope.launch {
                    noteUseCases.addNote(event.note.copy(isPinned = !event.note.isPinned))
                }
            }
        }
    }

    private fun refreshNotes() {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(
            noteOrder = state.value.noteOrder,
            searchQuery = state.value.searchQuery,
            colorFilter = state.value.colorFilter
        ).onEach { notes ->
            _state.value = state.value.copy(notes = notes)
        }.launchIn(viewModelScope)
    }
}
