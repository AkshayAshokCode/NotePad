package com.akshayashokcode.notepad.feature_note.presentation.notes.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.akshayashokcode.notepad.core.util.TestTags
import com.akshayashokcode.notepad.feature_note.domain.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCornerSize: Dp = 14.dp,
    onDeleteClick: (() -> Unit)? = null,
    onRestoreClick: (() -> Unit)? = null
) {
    val textColor = Color(0xFF1C1C1C)
    val gradientLight = remember(note.color) {
        val c = Color(note.color)
        Color(c.red + (1f - c.red) * 0.10f, c.green + (1f - c.green) * 0.10f, c.blue + (1f - c.blue) * 0.10f, c.alpha)
    }
    val gradientDark = remember(note.color) {
        val c = Color(note.color)
        Color(c.red * 0.88f, c.green * 0.88f, c.blue * 0.88f, c.alpha)
    }

    val formattedDate = remember(note.timeStamp) {
        val now = System.currentTimeMillis()
        val diff = now - note.timeStamp
        when {
            diff < 24 * 60 * 60 * 1000L ->
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(note.timeStamp))
            diff < 7 * 24 * 60 * 60 * 1000L ->
                SimpleDateFormat("EEE, h:mm a", Locale.getDefault()).format(Date(note.timeStamp))
            else ->
                SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(note.timeStamp))
        }
    }

    val hasActions = onDeleteClick != null || onRestoreClick != null

    Box(modifier = modifier.testTag(TestTags.NOTE_ITEM)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val cut = cutCornerSize.toPx()
            val r = CornerRadius(cornerRadius.toPx())
            val base = Color(note.color)

            // Back face of the paper is always near-white, like real folded paper
            val foldFlap = Color(0xFFF5F5F5)

            // Darker shade for the crease line
            val crease = Color(
                red   = base.red   * 0.68f,
                green = base.green * 0.68f,
                blue  = base.blue  * 0.68f,
                alpha = base.alpha
            )

            // ── 1. Main card body ──────────────────────────────────────────
            val cardPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width - cut, 0f)
                lineTo(size.width, cut)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            clipPath(cardPath) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(gradientLight, gradientDark),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    ),
                    size = size,
                    cornerRadius = r
                )
            }

            // ── 2. Shadow strip where the flap lifts away from the card ───
            val inset = cut * 0.20f
            val shadowPath = Path().apply {
                moveTo(size.width - cut, 0f)
                lineTo(size.width, cut)
                lineTo(size.width - inset, cut)
                lineTo(size.width - cut, inset)
                close()
            }
            drawPath(path = shadowPath, color = Color.Black.copy(alpha = 0.10f))

            // ── 3. Fold flap triangle (back face of the paper) ────────────
            val foldPath = Path().apply {
                moveTo(size.width - cut, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, cut)
                close()
            }
            drawPath(path = foldPath, color = foldFlap)

            // ── 4. Crease line ─────────────────────────────────────────────
            drawLine(
                color = crease,
                start = Offset(size.width - cut, 0f),
                end   = Offset(size.width, cut),
                strokeWidth = 1.5.dp.toPx()
            )
        }

        if (note.isPinned) {
            Icon(
                imageVector = Icons.Filled.PushPin,
                contentDescription = "Pinned",
                tint = textColor.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .size(14.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (note.isPinned) 28.dp else 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = if (hasActions) 40.dp else 12.dp
                )
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.85f),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.55f),
                maxLines = 1
            )
        }

        if (hasActions) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 2.dp, bottom = 2.dp)
            ) {
                if (onRestoreClick != null) {
                    IconButton(onClick = onRestoreClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Filled.Unarchive,
                            contentDescription = "Restore note",
                            tint = textColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                if (onDeleteClick != null) {
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete note",
                            tint = textColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
