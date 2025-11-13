package com.akshayashokcode.notepad.feature_note.presentation.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.akshayashokcode.notepad.core.util.TestTags
import com.akshayashokcode.notepad.feature_note.domain.model.Note
import com.akshayashokcode.notepad.feature_note.presentation.notes.components.EmptyScreenText
import com.akshayashokcode.notepad.feature_note.presentation.notes.components.NoteItem
import com.akshayashokcode.notepad.feature_note.presentation.notes.components.OrderSection
import com.akshayashokcode.notepad.feature_note.presentation.util.Screen
import com.akshayashokcode.notepad.ui.theme.customTypography
import kotlinx.coroutines.launch

enum class FabState { Idle, Exploded }

@ExperimentalAnimationApi
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val animVisibleState = remember { MutableTransitionState(false) }
        .apply { targetState = true }
    val notesAvailable = state.notes.isNotEmpty()

    val primaryColor = MaterialTheme.colorScheme.primary
    val explosionColor = remember { Note.noteColors.random() }
    val fabSize = remember { Animatable(170f) }
    val iconAlpha = remember { Animatable(1f) }
    var fabState by remember { mutableStateOf(FabState.Idle) }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visibleState = animVisibleState,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .offset(x = 10.dp)
                        .padding(
                            bottom = WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding()
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            if (fabState == FabState.Idle) {
                                fabState = FabState.Exploded
                                scope.launch {
                                    // Fade out icon first
                                    iconAlpha.animateTo(0f, tween(150))
                                    // Then explode background
                                    fabSize.animateTo(
                                        targetValue = 6000f,
                                        animationSpec = keyframes {
                                            durationMillis = 300
                                            170f at 0
                                            90f at 80
                                            6000f at 300
                                        }
                                    )

                                    // close order section when clicking FAB
                                    viewModel.onEvent(NotesEvent.CloseOrderSection)
                                    navController.navigate(
                                        Screen.AddEditNoteScreen.route +
                                                "?noteColor=${explosionColor.toArgb()}"
                                    )
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Exploding background
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val color =
                            if (fabState == FabState.Exploded) explosionColor else primaryColor
                        drawCircle(
                            color = color,
                            radius = fabSize.value / 2f,
                            center = center
                        )
                    }
                    // Static icon that fades out
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.alpha(iconAlpha.value)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    actionColor = MaterialTheme.colorScheme.surface
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notes",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        fontWeight = MaterialTheme.typography.headlineMedium.fontWeight,
                        fontFamily = customTypography.bodyLarge.fontFamily
                    )
                )
                if (notesAvailable) {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(NotesEvent.ToggleOrderSection)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort",
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = state.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                OrderSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .testTag(TestTags.ORDER_SECTION),
                    noteOrder = state.noteOrder,
                    onOrderChange = {
                        viewModel.onEvent(NotesEvent.Order(it))
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (notesAvailable) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.notes) { note ->
                        NoteItem(
                            note = note,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // close order section when clicking FAB
                                    viewModel.onEvent(NotesEvent.CloseOrderSection)
                                    navController.navigate(
                                        Screen.AddEditNoteScreen.route +
                                                "?noteId=${note.id}&noteColor=${note.color}"
                                    )
                                },
                            onDeleteClick = {
                                viewModel.onEvent(NotesEvent.DeleteNote(note))
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Note deleted",
                                        actionLabel = "Undo",
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.onEvent(NotesEvent.RestoreNote)
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (state.notes.last() == note) {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            } else {
                EmptyScreenText()
            }
        }
    }
}
