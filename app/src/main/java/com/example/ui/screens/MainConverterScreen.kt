package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NumberSystem
import com.example.ui.components.AppHeader
import com.example.ui.components.HowItConvertsCard
import com.example.ui.components.NumberPanelCard
import com.example.ui.components.QuickKeypadBar
import com.example.ui.theme.NumLime
import com.example.viewmodel.ConverterViewModel

@Composable
fun MainConverterScreen(
    viewModel: ConverterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            QuickKeypadBar(
                activeSource = uiState.activeSource,
                isVisible = uiState.showQuickKeypad,
                onKeyPressed = { viewModel.onKeypadKeyPressed(it) },
                onBackspace = { viewModel.onKeypadBackspace() },
                onToggleSign = { viewModel.toggleSign() },
                onClear = { viewModel.clearActiveSource() },
                modifier = Modifier.navigationBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Header Branding & Toolbar
                item {
                    AppHeader(
                        activeSource = uiState.activeSource,
                        isDarkTheme = uiState.isDarkTheme,
                        showQuickKeypad = uiState.showQuickKeypad,
                        onCycleSource = { viewModel.cycleSource() },
                        onToggleKeypad = { viewModel.toggleQuickKeypad() },
                        onToggleTheme = { viewModel.toggleTheme() },
                        onClearAll = { viewModel.clearAll() }
                    )
                }

                // Four Number-System Panels: Responsive 2x2 or Stacked layout
                item {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val isWide = maxWidth >= 480.dp

                        if (isWide) {
                            // 2x2 Grid for wider devices / tablets / landscape
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    NumberPanelCard(
                                        system = NumberSystem.BINARY,
                                        value = uiState.binaryInput,
                                        isActiveSource = uiState.activeSource == NumberSystem.BINARY,
                                        hasError = uiState.errorSystem == NumberSystem.BINARY,
                                        errorMessage = if (uiState.errorSystem == NumberSystem.BINARY) uiState.errorMessage else null,
                                        isCopied = uiState.copiedSystem == NumberSystem.BINARY,
                                        bitInfo = if (uiState.bitLength > 0) "${uiState.bitLength} BITS" else null,
                                        onValueChange = { viewModel.onInputChanged(NumberSystem.BINARY, it) },
                                        onSelectAsSource = { viewModel.setActiveSource(NumberSystem.BINARY) },
                                        onCopy = { viewModel.onCopied(NumberSystem.BINARY) },
                                        onClear = { viewModel.onInputChanged(NumberSystem.BINARY, "") },
                                        modifier = Modifier.weight(1f)
                                    )

                                    NumberPanelCard(
                                        system = NumberSystem.OCTAL,
                                        value = uiState.octalInput,
                                        isActiveSource = uiState.activeSource == NumberSystem.OCTAL,
                                        hasError = uiState.errorSystem == NumberSystem.OCTAL,
                                        errorMessage = if (uiState.errorSystem == NumberSystem.OCTAL) uiState.errorMessage else null,
                                        isCopied = uiState.copiedSystem == NumberSystem.OCTAL,
                                        bitInfo = if (uiState.bitLength > 0) "${uiState.byteCount} BYTES" else null,
                                        onValueChange = { viewModel.onInputChanged(NumberSystem.OCTAL, it) },
                                        onSelectAsSource = { viewModel.setActiveSource(NumberSystem.OCTAL) },
                                        onCopy = { viewModel.onCopied(NumberSystem.OCTAL) },
                                        onClear = { viewModel.onInputChanged(NumberSystem.OCTAL, "") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    NumberPanelCard(
                                        system = NumberSystem.DECIMAL,
                                        value = uiState.decimalInput,
                                        isActiveSource = uiState.activeSource == NumberSystem.DECIMAL,
                                        hasError = uiState.errorSystem == NumberSystem.DECIMAL,
                                        errorMessage = if (uiState.errorSystem == NumberSystem.DECIMAL) uiState.errorMessage else null,
                                        isCopied = uiState.copiedSystem == NumberSystem.DECIMAL,
                                        bitInfo = if (uiState.bitLength > 0) "${uiState.bitLength} BITS" else null,
                                        onValueChange = { viewModel.onInputChanged(NumberSystem.DECIMAL, it) },
                                        onSelectAsSource = { viewModel.setActiveSource(NumberSystem.DECIMAL) },
                                        onCopy = { viewModel.onCopied(NumberSystem.DECIMAL) },
                                        onClear = { viewModel.onInputChanged(NumberSystem.DECIMAL, "") },
                                        modifier = Modifier.weight(1f)
                                    )

                                    NumberPanelCard(
                                        system = NumberSystem.HEXADECIMAL,
                                        value = uiState.hexInput,
                                        isActiveSource = uiState.activeSource == NumberSystem.HEXADECIMAL,
                                        hasError = uiState.errorSystem == NumberSystem.HEXADECIMAL,
                                        errorMessage = if (uiState.errorSystem == NumberSystem.HEXADECIMAL) uiState.errorMessage else null,
                                        isCopied = uiState.copiedSystem == NumberSystem.HEXADECIMAL,
                                        bitInfo = if (uiState.bitLength > 0) "${uiState.byteCount} BYTES" else null,
                                        onValueChange = { viewModel.onInputChanged(NumberSystem.HEXADECIMAL, it) },
                                        onSelectAsSource = { viewModel.setActiveSource(NumberSystem.HEXADECIMAL) },
                                        onCopy = { viewModel.onCopied(NumberSystem.HEXADECIMAL) },
                                        onClear = { viewModel.onInputChanged(NumberSystem.HEXADECIMAL, "") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        } else {
                            // Comfortable Vertical Stack on standard phone screens so each input is generous and easy to tap
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                NumberPanelCard(
                                    system = NumberSystem.BINARY,
                                    value = uiState.binaryInput,
                                    isActiveSource = uiState.activeSource == NumberSystem.BINARY,
                                    hasError = uiState.errorSystem == NumberSystem.BINARY,
                                    errorMessage = if (uiState.errorSystem == NumberSystem.BINARY) uiState.errorMessage else null,
                                    isCopied = uiState.copiedSystem == NumberSystem.BINARY,
                                    bitInfo = if (uiState.bitLength > 0) "${uiState.bitLength} BITS" else null,
                                    onValueChange = { viewModel.onInputChanged(NumberSystem.BINARY, it) },
                                    onSelectAsSource = { viewModel.setActiveSource(NumberSystem.BINARY) },
                                    onCopy = { viewModel.onCopied(NumberSystem.BINARY) },
                                    onClear = { viewModel.onInputChanged(NumberSystem.BINARY, "") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NumberPanelCard(
                                    system = NumberSystem.OCTAL,
                                    value = uiState.octalInput,
                                    isActiveSource = uiState.activeSource == NumberSystem.OCTAL,
                                    hasError = uiState.errorSystem == NumberSystem.OCTAL,
                                    errorMessage = if (uiState.errorSystem == NumberSystem.OCTAL) uiState.errorMessage else null,
                                    isCopied = uiState.copiedSystem == NumberSystem.OCTAL,
                                    bitInfo = if (uiState.bitLength > 0) "${uiState.byteCount} BYTES" else null,
                                    onValueChange = { viewModel.onInputChanged(NumberSystem.OCTAL, it) },
                                    onSelectAsSource = { viewModel.setActiveSource(NumberSystem.OCTAL) },
                                    onCopy = { viewModel.onCopied(NumberSystem.OCTAL) },
                                    onClear = { viewModel.onInputChanged(NumberSystem.OCTAL, "") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NumberPanelCard(
                                    system = NumberSystem.DECIMAL,
                                    value = uiState.decimalInput,
                                    isActiveSource = uiState.activeSource == NumberSystem.DECIMAL,
                                    hasError = uiState.errorSystem == NumberSystem.DECIMAL,
                                    errorMessage = if (uiState.errorSystem == NumberSystem.DECIMAL) uiState.errorMessage else null,
                                    isCopied = uiState.copiedSystem == NumberSystem.DECIMAL,
                                    bitInfo = if (uiState.bitLength > 0) "${uiState.bitLength} BITS" else null,
                                    onValueChange = { viewModel.onInputChanged(NumberSystem.DECIMAL, it) },
                                    onSelectAsSource = { viewModel.setActiveSource(NumberSystem.DECIMAL) },
                                    onCopy = { viewModel.onCopied(NumberSystem.DECIMAL) },
                                    onClear = { viewModel.onInputChanged(NumberSystem.DECIMAL, "") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NumberPanelCard(
                                    system = NumberSystem.HEXADECIMAL,
                                    value = uiState.hexInput,
                                    isActiveSource = uiState.activeSource == NumberSystem.HEXADECIMAL,
                                    hasError = uiState.errorSystem == NumberSystem.HEXADECIMAL,
                                    errorMessage = if (uiState.errorSystem == NumberSystem.HEXADECIMAL) uiState.errorMessage else null,
                                    isCopied = uiState.copiedSystem == NumberSystem.HEXADECIMAL,
                                    bitInfo = if (uiState.bitLength > 0) "${uiState.byteCount} BYTES" else null,
                                    onValueChange = { viewModel.onInputChanged(NumberSystem.HEXADECIMAL, it) },
                                    onSelectAsSource = { viewModel.setActiveSource(NumberSystem.HEXADECIMAL) },
                                    onCopy = { viewModel.onCopied(NumberSystem.HEXADECIMAL) },
                                    onClear = { viewModel.onInputChanged(NumberSystem.HEXADECIMAL, "") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Educational Explainer: "HOW IT CONVERTS"
                item {
                    HowItConvertsCard(
                        activeSource = uiState.activeSource,
                        selectedTarget = uiState.explainerTarget,
                        explanation = uiState.currentExplanation,
                        isExpanded = uiState.isExplainerExpanded,
                        onToggleExpand = { viewModel.toggleExplainer() },
                        onSelectTarget = { viewModel.setExplainerTarget(it) }
                    )
                }

                // Subtle Technical Workstation Footer
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NUM K2I • ZERO NONSENSE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )

                        if (uiState.currentBigInteger != null) {
                            Text(
                                text = "RAW: ${uiState.currentBigInteger}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = NumLime.copy(alpha = 0.8f)
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
