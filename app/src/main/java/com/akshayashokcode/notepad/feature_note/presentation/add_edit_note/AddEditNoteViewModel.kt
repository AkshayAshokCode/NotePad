package com.akshayashokcode.notepad.feature_note.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.notepad.feature_note.domain.model.Note
import com.akshayashokcode.notepad.feature_note.domain.use_case.NoteUseCases
import com.akshayashokcode.notepad.ui.theme.NoteColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
            text = savedStateHandle.get<String>(KEY_TITLE) ?: "",
            hint = "Enter title...",
            isHintVisible = savedStateHandle.get<String>(KEY_TITLE).isNullOrBlank()
        )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
            text = savedStateHandle.get<String>(KEY_CONTENT) ?: "",
            hint = "Enter some content",
            isHintVisible = savedStateHandle.get<String>(KEY_CONTENT).isNullOrBlank()
        )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteColor = mutableStateOf<Int>(
        savedStateHandle.get<Int>(KEY_COLOR) ?: NoteColors.all.random().toArgb()
    )
    val noteColor: State<Int> = _noteColor

    private val _isPinned = mutableStateOf(
        savedStateHandle.get<Boolean>(KEY_PINNED) ?: false
    )
    val isPinned: State<Boolean> = _isPinned

    private val _noteTimestamp = mutableStateOf<Long?>(null)
    val noteTimestamp: State<Long?> = _noteTimestamp

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: Int? = null
    private var originalNote: Note? = null

    init {
        // Pre-fill content when app opened via share intent from another app
        savedStateHandle.get<String>("sharedContent")?.takeIf { it.isNotBlank() }?.let { shared ->
            if (savedStateHandle.get<String>(KEY_CONTENT) == null) {
                _noteContent.value = noteContent.value.copy(text = shared, isHintVisible = false)
                savedStateHandle[KEY_CONTENT] = shared
            }
        }

        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if (noteId != -1) {
                currentNoteId = noteId
                viewModelScope.launch {
                    noteUseCases.getNote(noteId)?.also { note ->
                        originalNote = note
                        _noteTimestamp.value = note.timeStamp
                        if (savedStateHandle.get<String>(KEY_TITLE) == null) {
                            _noteTitle.value = noteTitle.value.copy(
                                text = note.title,
                                isHintVisible = false
                            )
                        }
                        if (savedStateHandle.get<String>(KEY_CONTENT) == null) {
                            _noteContent.value = noteContent.value.copy(
                                text = note.content,
                                isHintVisible = false
                            )
                        }
                        if (savedStateHandle.get<Int>(KEY_COLOR) == null) {
                            _noteColor.value = note.color
                        }
                        if (savedStateHandle.get<Boolean>(KEY_PINNED) == null) {
                            _isPinned.value = note.isPinned
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(text = event.value)
                savedStateHandle[KEY_TITLE] = event.value
            }
            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteTitle.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value = noteContent.value.copy(text = event.value)
                savedStateHandle[KEY_CONTENT] = event.value
            }
            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value = noteContent.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteContent.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
                savedStateHandle[KEY_COLOR] = event.color
            }
            is AddEditNoteEvent.TogglePin -> {
                _isPinned.value = !isPinned.value
                savedStateHandle[KEY_PINNED] = _isPinned.value
            }
            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        val title = noteTitle.value.text.trim().ifEmpty { "Untitled Note" }
                        val content = noteContent.value.text.trim()
                        val orig = originalNote
                        val hasChanges = orig == null ||
                            title != orig.title ||
                            content != orig.content ||
                            noteColor.value != orig.color ||
                            isPinned.value != orig.isPinned
                        val timestamp = if (hasChanges) System.currentTimeMillis() else orig!!.timeStamp
                        noteUseCases.addNote(
                            Note(
                                title = title,
                                content = content,
                                timeStamp = timestamp,
                                color = noteColor.value,
                                isPinned = isPinned.value,
                                id = currentNoteId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch (e: Note.InvalidNoteException) {
                        _eventFlow.emit(UiEvent.ShowSnackBar(e.message ?: "Couldn't save note"))
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }

    companion object {
        private const val KEY_TITLE = "edit_title"
        private const val KEY_CONTENT = "edit_content"
        private const val KEY_COLOR = "edit_color"
        private const val KEY_PINNED = "edit_pinned"
    }
}
