package com.akshayashokcode.notepad.feature_note.presentation.notes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akshayashokcode.notepad.feature_note.domain.util.NoteOrder
import com.akshayashokcode.notepad.feature_note.domain.util.OrderType

@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    onOrderChange: (NoteOrder) -> Unit
) {
    val isAscending = noteOrder.orderType is OrderType.Ascending

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            "Title" to (noteOrder is NoteOrder.Title),
            "Date"  to (noteOrder is NoteOrder.Date),
            "Color" to (noteOrder is NoteOrder.Color)
        ).forEach { (label, selected) ->
            FilterChip(
                selected = selected,
                onClick = {
                    val newOrder = when (label) {
                        "Title" -> NoteOrder.Title(
                            if (selected) noteOrder.orderType.toggle() else noteOrder.orderType
                        )
                        "Date"  -> NoteOrder.Date(
                            if (selected) noteOrder.orderType.toggle() else noteOrder.orderType
                        )
                        else    -> NoteOrder.Color(
                            if (selected) noteOrder.orderType.toggle() else noteOrder.orderType
                        )
                    }
                    onOrderChange(newOrder)
                },
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    selectedBorderColor = MaterialTheme.colorScheme.primary
                ),
                label = { Text(label) },
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = if (isAscending) Icons.Default.KeyboardArrowUp
                                          else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isAscending) "Ascending" else "Descending",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null
            )
        }
    }
}

private fun OrderType.toggle(): OrderType =
    if (this is OrderType.Ascending) OrderType.Descending else OrderType.Ascending
