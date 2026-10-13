package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NumberSystem
import com.example.ui.theme.NumBorder
import com.example.ui.theme.NumBorderActive
import com.example.ui.theme.NumError
import com.example.ui.theme.NumErrorBg
import com.example.ui.theme.NumLime
import com.example.ui.theme.NumSurface
import com.example.ui.theme.NumSurfaceElevated

@Composable
fun NumberPanelCard(
    system: NumberSystem,
    value: String,
    isActiveSource: Boolean,
    hasError: Boolean,
    errorMessage: String?,
    isCopied: Boolean,
    bitInfo: String? = null,
    onValueChange: (String) -> Unit,
    onSelectAsSource: () -> Unit,
    onCopy: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current

    val borderColor by animateColorAsState(
        targetValue = when {
            hasError -> NumError
            isActiveSource -> NumLime
            else -> NumBorder
        },
        animationSpec = tween(200),
        label = "panel_border_color"
    )

    val cardBg = if (isActiveSource) NumSurfaceElevated else NumSurface

    Box(
        modifier = modifier
            .testTag("${system.id.lowercase()}_panel")
            .clip(RoundedCornerShape(10.dp))
            .background(cardBg)
            .border(if (isActiveSource) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isActiveSource) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectAsSource()
                }
            }
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: System Name, Base Label, Active Indicator, and Copy Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = system.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = if (isActiveSource) NumLime else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = system.baseLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    if (isActiveSource) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(NumLime.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(NumLime)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SOURCE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NumLime
                                )
                            )
                        }
                    }
                }

                // Copy Action and Feedback Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedVisibility(
                        visible = isCopied,
                        enter = fadeIn(tween(150)),
                        exit = fadeOut(tween(200))
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NumLime)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "COPIED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp,
                                    color = NumSurfaceElevated
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = {
                            if (value.isNotEmpty()) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                clipboardManager.setText(AnnotatedString(value))
                                onCopy()
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("${system.id.lowercase()}_copy_button")
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy ${system.displayName} value",
                            tint = if (isCopied) NumLime else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Input Field Container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isActiveSource) NumSurface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .border(
                        0.5.dp,
                        if (isActiveSource) NumBorderActive else NumBorder.copy(alpha = 0.5f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prefix Label
                Text(
                    text = system.prefix,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(end = 6.dp)
                )

                // Editable Number Input
                BasicTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (!isActiveSource) {
                            onSelectAsSource()
                        }
                        onValueChange(newText)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("${system.id.lowercase()}_input"),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (value.length > 20) 13.sp else if (value.length > 12) 15.sp else 18.sp,
                        color = if (hasError) NumError else MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(if (hasError) NumError else NumLime),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (system) {
                            NumberSystem.HEXADECIMAL -> KeyboardType.Ascii
                            NumberSystem.BINARY, NumberSystem.OCTAL, NumberSystem.DECIMAL -> KeyboardType.Ascii
                        },
                        capitalization = if (system == NumberSystem.HEXADECIMAL) {
                            KeyboardCapitalization.Characters
                        } else {
                            KeyboardCapitalization.None
                        },
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (value.isEmpty()) {
                                Text(
                                    text = "0",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Clear button inside input if active and not empty
                if (isActiveSource && value.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onClear()
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("${system.id.lowercase()}_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear input",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Bottom metadata / validation feedback
            Spacer(modifier = Modifier.height(6.dp))

            if (hasError && errorMessage != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(NumErrorBg)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = NumError
                        ),
                        maxLines = 1
                    )
                }
            } else if (bitInfo != null && value.isNotEmpty() && value != "-") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = bitInfo,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
