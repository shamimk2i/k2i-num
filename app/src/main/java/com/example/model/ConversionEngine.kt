package com.example.model

import java.math.BigInteger

data class ConversionResult(
    val isValid: Boolean = true,
    val isPending: Boolean = false, // e.g. single '-' typed
    val errorMessage: String? = null,
    val binaryValue: String = "",
    val octalValue: String = "",
    val decimalValue: String = "",
    val hexValue: String = "",
    val rawValue: BigInteger? = null,
    val bitLength: Int = 0,
    val byteCount: Int = 0
)

object ConversionEngine {

    /**
     * Converts a raw input string from a specified source system into all 4 number systems.
     */
    fun convert(source: NumberSystem, rawInput: String): ConversionResult {
        val trimmed = rawInput.trim()

        if (trimmed.isEmpty()) {
            return ConversionResult(
                isValid = true,
                isPending = false,
                binaryValue = "",
                octalValue = "",
                decimalValue = "",
                hexValue = "",
                rawValue = null,
                bitLength = 0,
                byteCount = 0
            )
        }

        val isNegative = trimmed.startsWith("-")
        val content = if (isNegative) trimmed.substring(1) else trimmed

        if (content.isEmpty() && isNegative) {
            // User just typed "-"
            return ConversionResult(
                isValid = true,
                isPending = true,
                binaryValue = "-",
                octalValue = "-",
                decimalValue = "-",
                hexValue = "-",
                rawValue = null,
                bitLength = 0,
                byteCount = 0
            )
        }

        // Validate each character against the source system
        val invalidChar = content.find { !source.isValidChar(it) }
        if (invalidChar != null) {
            return ConversionResult(
                isValid = false,
                errorMessage = "Invalid digit '$invalidChar' for ${source.displayName} (${source.allowedCharDescription})"
            )
        }

        return try {
            val parsedBigInt = BigInteger(content, source.base)
            val signedBigInt = if (isNegative) parsedBigInt.negate() else parsedBigInt

            val signPrefix = if (signedBigInt.signum() < 0) "-" else ""
            val absBigInt = signedBigInt.abs()

            val bin = signPrefix + absBigInt.toString(2)
            val oct = signPrefix + absBigInt.toString(8)
            val dec = signedBigInt.toString(10)
            val hex = signPrefix + absBigInt.toString(16).uppercase()

            val bits = if (absBigInt == BigInteger.ZERO) 1 else absBigInt.bitLength()
            val bytes = if (bits == 0) 1 else (bits + 7) / 8

            ConversionResult(
                isValid = true,
                isPending = false,
                errorMessage = null,
                binaryValue = bin,
                octalValue = oct,
                decimalValue = dec,
                hexValue = hex,
                rawValue = signedBigInt,
                bitLength = bits,
                byteCount = bytes
            )
        } catch (e: Exception) {
            ConversionResult(
                isValid = false,
                errorMessage = "Invalid number format: ${e.message}"
            )
        }
    }

    /**
     * Sanitizes input for a given system, filtering out completely invalid characters
     * or converting hex to uppercase.
     */
    fun sanitizeInput(system: NumberSystem, input: String): String {
        val isNegative = input.startsWith("-")
        val rest = if (isNegative) input.substring(1) else input
        val filtered = rest.filter { system.isValidChar(it) }
        val result = if (isNegative) "-$filtered" else filtered
        return if (system == NumberSystem.HEXADECIMAL) result.uppercase() else result
    }

    /**
     * Validates whether a character is permissible for entry in this system (including '-').
     */
    fun isAllowedInInput(system: NumberSystem, char: Char, isFirstChar: Boolean): Boolean {
        if (char == '-' && isFirstChar) return true
        return system.isValidChar(char)
    }
}
