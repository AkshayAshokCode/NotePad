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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.akshayashokcode.notepad.core.util.TestTags
import com.akshayashokcode.notepad.feature_note.presentation.notes.components.EmptyScreenText
import com.akshayashokcode.notepad.feature_note.presentation.notes.components.NoteItem
import com.akshayashokcode.notepad.feature_note.presentation.notes.components.OrderSection
import com.akshayashokcode.notepad.feature_note.presentation.util.Screen
import com.akshayashokcode.notepad.ui.theme.NoteColors
import com.akshayashokcode.notepad.ui.theme.customTypography
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch

enum class FabState { Idle, Exploded }

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val animVisibleState = remember { MutableTransitionState(false) }
        .apply { targetState = true }
    val notesAvailable = state.notes.isNotEmpty()

    val primaryColor = MaterialTheme.colorScheme.primary
    val explosionColor = remember { NoteColors.all.random() }
    val fabSize = remember { Animatable(170f) }
    val iconAlpha = remember { Animatable(1f) }
    var fabState by remember { mutableStateOf(FabState.Idle) }
    var isSearchVisible by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visibleState = animVisibleState,
                enter = fadeIn(animationSpec = tween(500, easing = LinearOutSlowInEasing))
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
                        .semantics {
                            contentDescription = "Add new note"
                            role = Role.Button
                        }
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            if (fabState == FabState.Idle) {
                                fabState = FabState.Exploded
                                scope.launch {
                                    iconAlpha.animateTo(0f, tween(150))
                                    fabSize.animateTo(
                                        targetValue = 6000f,
                                        animationSpec = keyframes {
                                            durationMillis = 300
                                            170f at 0
                                            90f at 80
                                            6000f at 300
                                        }
                                    )
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
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            color = if (fabState == FabState.Exploded) explosionColor else primaryColor,
                            radius = fabSize.value / 2f,
                            center = center
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.alpha(iconAlpha.value)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isSearchVisible) {
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
                    Row {
                        IconButton(onClick = {
                            isSearchVisible = true
                            viewModel.onEvent(NotesEvent.CloseOrderSection)
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search notes")
                        }
                        IconButton(onClick = {
                            navController.navigate(Screen.ArchiveScreen.route)
                        }) {
                            Icon(Icons.Outlined.Inventory2, contentDescription = "View archive")
                        }
                        if (notesAvailable) {
                            IconButton(onClick = { viewModel.onEvent(NotesEvent.ToggleOrderSection) }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort notes")
                            }
                        }
                    }
                }
            }

            // Search bar
            AnimatedVisibility(visible = isSearchVisible) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onEvent(NotesEvent.SearchQuery(it)) },
                        placeholder = { Text("Search notes...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.onEvent(NotesEvent.SearchQuery("")) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { })
                    )
                    TextButton(onClick = {
                        viewModel.onEvent(NotesEvent.SearchQuery(""))
                        isSearchVisible = false
                    }) {
                        Text("Cancel")
                    }
                }
            }

            // Sort + color filter section
            AnimatedVisibility(
                visible = state.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Column {
                    OrderSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .testTag(TestTags.ORDER_SECTION),
                        noteOrder = state.noteOrder,
                        onOrderChange = { viewModel.onEvent(NotesEvent.Order(it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(NoteColors.all) { color ->
                            val colorInt = color.toArgb()
                            val isSelected = state.colorFilter == colorInt
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.onEvent(NotesEvent.FilterColor(colorInt)) }
                                    .semantics {
                                        contentDescription = "Filter by ${NoteColors.nameFor(color)}"
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                notesAvailable -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.notes,
                            key = { note -> note.id ?: note.hashCode() }
                        ) { note ->
                            var menuExpanded by remember { mutableStateOf(false) }

                            Box {
                                NoteItem(
                                    note = note,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                if (navController.currentBackStackEntry
                                                        ?.lifecycle?.currentState == Lifecycle.State.RESUMED
                                                ) {
                                                    viewModel.onEvent(NotesEvent.CloseOrderSection)
                                                    navController.navigate(
                                                        Screen.AddEditNoteScreen.route +
                                                                "?noteId=${note.id}&noteColor=${note.color}"
                                                    )
                                                }
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuExpanded = true
                                            }
                                        )
                                )

                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }
                                ) {
                                    val menuContentColor = MaterialTheme.colorScheme.onBackground
                                    val menuItemColors = MenuDefaults.itemColors(
                                        textColor = menuContentColor,
                                        leadingIconColor = menuContentColor
                                    )

                                    DropdownMenuItem(
                                        text = { Text(if (note.isPinned) "Unpin" else "Pin") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                                contentDescription = null
                                            )
                                        },
                                        colors = menuItemColors,
                                        onClick = {
                                            menuExpanded = false
                                            viewModel.onEvent(NotesEvent.TogglePin(note))
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Archive") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Archive, contentDescription = null)
                                        },
                                        colors = menuItemColors,
                                        onClick = {
                                            menuExpanded = false
                                            viewModel.onEvent(NotesEvent.ArchiveNote(note))
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Note archived",
                                                    actionLabel = "Undo",
                                                    withDismissAction = true,
                                                    duration = SnackbarDuration.Long
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.onEvent(NotesEvent.UndoArchive)
                                                }
                                            }
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Delete",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            viewModel.onEvent(NotesEvent.DeleteNote(note))
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Note deleted",
                                                    actionLabel = "Undo",
                                                    withDismissAction = true,
                                                    duration = SnackbarDuration.Long
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.onEvent(NotesEvent.RestoreNote)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                state.searchQuery.isNotBlank() || state.colorFilter != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No notes match your search",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
                else -> EmptyScreenText()
            }
        }
    }
}
