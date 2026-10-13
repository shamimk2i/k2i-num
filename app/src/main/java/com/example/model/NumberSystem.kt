package com.example.model

enum class NumberSystem(
    val id: String,
    val displayName: String,
    val base: Int,
    val baseLabel: String,
    val prefix: String,
    val allowedCharDescription: String
) {
    BINARY(
        id = "BIN",
        displayName = "BINARY",
        base = 2,
        baseLabel = "BASE 02",
        prefix = "0b",
        allowedCharDescription = "0, 1"
    ),
    OCTAL(
        id = "OCT",
        displayName = "OCTAL",
        base = 8,
        baseLabel = "BASE 08",
        prefix = "0o",
        allowedCharDescription = "0 to 7"
    ),
    DECIMAL(
        id = "DEC",
        displayName = "DECIMAL",
        base = 10,
        baseLabel = "BASE 10",
        prefix = "0d",
        allowedCharDescription = "0 to 9"
    ),
    HEXADECIMAL(
        id = "HEX",
        displayName = "HEXADECIMAL",
        base = 16,
        baseLabel = "BASE 16",
        prefix = "0x",
        allowedCharDescription = "0 to 9, A to F"
    );

    fun isValidChar(c: Char): Boolean {
        return when (this) {
            BINARY -> c == '0' || c == '1'
            OCTAL -> c in '0'..'7'
            DECIMAL -> c in '0'..'9'
            HEXADECIMAL -> c in '0'..'9' || c in 'A'..'F' || c in 'a'..'f'
        }
    }

    fun nextSystem(): NumberSystem {
        val entries = entries
        val nextIdx = (ordinal + 1) % entries.size
        return entries[nextIdx]
    }
}
