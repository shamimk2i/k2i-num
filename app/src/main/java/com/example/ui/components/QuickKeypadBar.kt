package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NumberSystem
import com.example.ui.theme.NumBorder
import com.example.ui.theme.NumBorderActive
import com.example.ui.theme.NumLime
import com.example.ui.theme.NumSurface
import com.example.ui.theme.NumSurfaceElevated

@Composable
fun QuickKeypadBar(
    activeSource: NumberSystem,
    isVisible: Boolean,
    onKeyPressed: (Char) -> Unit,
    onBackspace: () -> Unit,
    onToggleSign: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(tween(200)) + fadeIn(tween(150)),
        exit = shrinkVertically(tween(150)) + fadeOut(tween(100)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quick_keypad_bar")
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(NumSurfaceElevated)
                .border(1.dp, NumBorderActive, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Keypad Title & Active Mode Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRECISION KEYPAD • ${activeSource.displayName}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NumLime,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "RADIX ${activeSource.base}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            when (activeSource) {
                NumberSystem.BINARY -> {
                    // Binary: 0, 1, ±, ⌫, CLR
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KeypadButton(
                            label = "0",
                            modifier = Modifier.weight(1.5f),
                            isAccent = true,
                            onClick = { onKeyPressed('0') }
                        )
                        KeypadButton(
                            label = "1",
                            modifier = Modifier.weight(1.5f),
                            isAccent = true,
                            onClick = { onKeyPressed('1') }
                        )
                        KeypadButton(
                            label = "±",
                            modifier = Modifier.weight(1f),
                            onClick = onToggleSign
                        )
                        KeypadIconButton(
                            modifier = Modifier.weight(1f),
                            onClick = onBackspace
                        )
                        KeypadButton(
                            label = "CLR",
                            modifier = Modifier.weight(1f),
                            onClick = onClear
                        )
                    }
                }

                NumberSystem.OCTAL -> {
                    // Octal: row 1: 0-3, row 2: 4-7, ±, ⌫, CLR
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf('0', '1', '2', '3').forEach { ch ->
                                KeypadButton(
                                    label = ch.toString(),
                                    modifier = Modifier.weight(1f),
                                    onClick = { onKeyPressed(ch) }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf('4', '5', '6', '7').forEach { ch ->
                                KeypadButton(
                                    label = ch.toString(),
                                    modifier = Modifier.weight(1f),
                                    onClick = { onKeyPressed(ch) }
                                )
                            }
                            KeypadButton(
                                label = "±",
                                modifier = Modifier.weight(1f),
                                onClick = onToggleSign
                            )
                            KeypadIconButton(
                                modifier = Modifier.weight(1f),
                                onClick = onBackspace
                            )
                            KeypadButton(
                                label = "CLR",
                                modifier = Modifier.weight(1f),
                                onClick = onClear
                            )
                        }
                    }
                }

                NumberSystem.DECIMAL -> {
                    // Decimal: row 1: 1-5, row 2: 6-9, 0, ±, ⌫, CLR
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf('1', '2', '3', '4', '5').forEach { ch ->
                                KeypadButton(
                                    label = ch.toString(),
                                    modifier = Modifier.weight(1f),
                                    onClick = { onKeyPressed(ch) }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf('6', '7', '8', '9', '0').forEach { ch ->
                                KeypadButton(
                                    label = ch.toString(),
                                    modifier = Modifier.weight(1f),
                                    onClick = { onKeyPressed(ch) }
                                )
                            }
                            KeypadButton(
                                label = "±",
                                modifier = Modifier.weight(1f),
                                onClick = onToggleSign
                            )
                            KeypadIconButton(
                                modifier = Modifier.weight(1f),
                                onClick = onBackspace
                            )
                            KeypadButton(
                                label = "CLR",
                                modifier = Modifier.weight(1f),
                                onClick = onClear
                            )
                        }
                    }
                }

                NumberSystem.HEXADECIMAL -> {
                    // Hexadecimal: 0-9 and A-F
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        // Hex letters A through F + controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            listOf('A', 'B', 'C', 'D', 'E', 'F').forEach { ch ->
                                KeypadButton(
                                    label = ch.toString(),
                                    modifier = Modifier.weight(1f),
                                    isAccent = true,
                                    onClick = { onKeyPressed(ch) }
                                )
                            }
                            KeypadIconButton(
                                modifier = Modifier.weight(1f),
                                onClick = onBackspace
                            )
                            KeypadButton(
                                label = "CLR",
                                modifier = Modifier.weight(1f),
                                onClick = onClear
                            )
                        }
                        // Digits 0 to 9 + sign
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9').forEach { ch ->
                                KeypadButton(
                                    label = ch.toString(),
                                    modifier = Modifier.weight(1f),
                                    onClick = { onKeyPressed(ch) }
                                )
                            }
                            KeypadButton(
                                label = "±",
                                modifier = Modifier.weight(1f),
                                onClick = onToggleSign
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    label: String,
    modifier: Modifier = Modifier,
    isAccent: Boolean = false,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isAccent) NumSurface else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (isAccent) NumLime.copy(alpha = 0.5f) else NumBorder,
                RoundedCornerShape(6.dp)
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = if (label.length > 2) 11.sp else 16.sp,
                color = if (isAccent) NumLime else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun KeypadIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, NumBorder, RoundedCornerShape(6.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "Backspace",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.height(16.dp)
        )
    }
}
