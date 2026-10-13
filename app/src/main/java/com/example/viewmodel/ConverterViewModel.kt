package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ConversionEngine
import com.example.model.ConversionExplainer
import com.example.model.ConversionResult
import com.example.model.ExplanationDetail
import com.example.model.NumberSystem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger

data class ConverterUiState(
    val activeSource: NumberSystem = NumberSystem.DECIMAL,
    val binaryInput: String = "45",
    val octalInput: String = "55",
    val decimalInput: String = "45",
    val hexInput: String = "2D",
    val currentBigInteger: BigInteger? = BigInteger.valueOf(45),
    val bitLength: Int = 6,
    val byteCount: Int = 1,
    val isValid: Boolean = true,
    val errorMessage: String? = null,
    val errorSystem: NumberSystem? = null,
    val isExplainerExpanded: Boolean = false,
    val explainerTarget: NumberSystem = NumberSystem.BINARY,
    val currentExplanation: ExplanationDetail? = null,
    val isDarkTheme: Boolean = true,
    val showQuickKeypad: Boolean = true,
    val copiedSystem: NumberSystem? = null
) {
    fun getValueFor(system: NumberSystem): String {
        return when (system) {
            NumberSystem.BINARY -> binaryInput
            NumberSystem.OCTAL -> octalInput
            NumberSystem.DECIMAL -> decimalInput
            NumberSystem.HEXADECIMAL -> hexInput
        }
    }
}

class ConverterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    private var copyFeedbackJob: Job? = null

    init {
        // Initial calculation based on Decimal 45
        updateExplanation()
    }

    fun onInputChanged(source: NumberSystem, newRawValue: String) {
        val activeSource = source
        val normalizedInput = if (source == NumberSystem.HEXADECIMAL) {
            newRawValue.uppercase()
        } else {
            newRawValue
        }

        val result = ConversionEngine.convert(source, normalizedInput)

        if (result.isValid) {
            _uiState.update { state ->
                val updatedState = state.copy(
                    activeSource = activeSource,
                    binaryInput = if (source == NumberSystem.BINARY) normalizedInput else result.binaryValue,
                    octalInput = if (source == NumberSystem.OCTAL) normalizedInput else result.octalValue,
                    decimalInput = if (source == NumberSystem.DECIMAL) normalizedInput else result.decimalValue,
                    hexInput = if (source == NumberSystem.HEXADECIMAL) normalizedInput else result.hexValue,
                    currentBigInteger = result.rawValue,
                    bitLength = result.bitLength,
                    byteCount = result.byteCount,
                    isValid = true,
                    errorMessage = null,
                    errorSystem = null
                )
                updatedState
            }
            updateExplanation()
        } else {
            // Keep the user's typed value in that field so they see the exact character, and display the error
            _uiState.update { state ->
                when (source) {
                    NumberSystem.BINARY -> state.copy(
                        activeSource = activeSource,
                        binaryInput = normalizedInput,
                        isValid = false,
                        errorMessage = result.errorMessage,
                        errorSystem = source
                    )
                    NumberSystem.OCTAL -> state.copy(
                        activeSource = activeSource,
                        octalInput = normalizedInput,
                        isValid = false,
                        errorMessage = result.errorMessage,
                        errorSystem = source
                    )
                    NumberSystem.DECIMAL -> state.copy(
                        activeSource = activeSource,
                        decimalInput = normalizedInput,
                        isValid = false,
                        errorMessage = result.errorMessage,
                        errorSystem = source
                    )
                    NumberSystem.HEXADECIMAL -> state.copy(
                        activeSource = activeSource,
                        hexInput = normalizedInput,
                        isValid = false,
                        errorMessage = result.errorMessage,
                        errorSystem = source
                    )
                }
            }
        }
    }

    fun setActiveSource(source: NumberSystem) {
        if (_uiState.value.activeSource != source) {
            _uiState.update { it.copy(activeSource = source) }
            // If target explainer was set to this source, pick an alternate target
            if (_uiState.value.explainerTarget == source) {
                val alternate = NumberSystem.entries.firstOrNull { it != source } ?: NumberSystem.BINARY
                _uiState.update { it.copy(explainerTarget = alternate) }
            }
            updateExplanation()
        }
    }

    fun cycleSource() {
        val next = _uiState.value.activeSource.nextSystem()
        setActiveSource(next)
    }

    fun clearAll() {
        _uiState.update {
            it.copy(
                binaryInput = "",
                octalInput = "",
                decimalInput = "",
                hexInput = "",
                currentBigInteger = null,
                bitLength = 0,
                byteCount = 0,
                isValid = true,
                errorMessage = null,
                errorSystem = null,
                currentExplanation = null
            )
        }
    }

    fun clearActiveSource() {
        clearAll()
    }

    fun toggleSign() {
        val source = _uiState.value.activeSource
        val currentVal = _uiState.value.getValueFor(source)
        if (currentVal.isEmpty()) {
            onInputChanged(source, "-")
        } else if (currentVal.startsWith("-")) {
            onInputChanged(source, currentVal.substring(1))
        } else {
            onInputChanged(source, "-$currentVal")
        }
    }

    fun onKeypadKeyPressed(char: Char) {
        val source = _uiState.value.activeSource
        val currentVal = _uiState.value.getValueFor(source)
        onInputChanged(source, currentVal + char)
    }

    fun onKeypadBackspace() {
        val source = _uiState.value.activeSource
        val currentVal = _uiState.value.getValueFor(source)
        if (currentVal.isNotEmpty()) {
            onInputChanged(source, currentVal.dropLast(1))
        }
    }

    fun toggleExplainer() {
        _uiState.update { it.copy(isExplainerExpanded = !it.isExplainerExpanded) }
        if (_uiState.value.isExplainerExpanded) {
            updateExplanation()
        }
    }

    fun setExplainerTarget(target: NumberSystem) {
        _uiState.update { it.copy(explainerTarget = target) }
        updateExplanation()
    }

    fun toggleQuickKeypad() {
        _uiState.update { it.copy(showQuickKeypad = !it.showQuickKeypad) }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun onCopied(system: NumberSystem) {
        copyFeedbackJob?.cancel()
        _uiState.update { it.copy(copiedSystem = system) }
        copyFeedbackJob = viewModelScope.launch {
            delay(1600)
            _uiState.update { it.copy(copiedSystem = null) }
        }
    }

    private fun updateExplanation() {
        val state = _uiState.value
        val bigInt = state.currentBigInteger
        if (bigInt != null) {
            val explainer = ConversionExplainer.explain(
                source = state.activeSource,
                target = state.explainerTarget,
                value = bigInt
            )
            _uiState.update { it.copy(currentExplanation = explainer) }
        } else {
            _uiState.update { it.copy(currentExplanation = null) }
        }
    }
}
