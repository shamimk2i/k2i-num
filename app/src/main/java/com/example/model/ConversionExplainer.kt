package com.example.model

import java.math.BigInteger

data class ExplanationDetail(
    val title: String,
    val subtitle: String,
    val method: String,
    val steps: List<String>,
    val note: String? = null,
    val resultSummary: String
)

object ConversionExplainer {

    private val MAX_STEPS = 20

    fun explain(source: NumberSystem, target: NumberSystem, value: BigInteger): ExplanationDetail {
        val isNegative = value.signum() < 0
        val absVal = value.abs()
        val signPrefix = if (isNegative) "-" else ""

        if (source == target) {
            return ExplanationDetail(
                title = "${source.displayName} → ${target.displayName}",
                subtitle = "Identical Base",
                method = "Identity",
                steps = listOf("The source and target number systems are the same (Base ${source.base})."),
                resultSummary = "$signPrefix${absVal.toString(source.base).uppercase()}"
            )
        }

        if (absVal == BigInteger.ZERO) {
            return ExplanationDetail(
                title = "${source.displayName} → ${target.displayName}",
                subtitle = "Zero Value",
                method = "Zero property",
                steps = listOf("Zero is represented as '0' in all number systems."),
                resultSummary = "0"
            )
        }

        // 1. Decimal to Base N (Division Remainder Method)
        if (source == NumberSystem.DECIMAL) {
            return explainDecimalToBase(target, absVal, isNegative)
        }

        // 2. Base N to Decimal (Positional Weight / Expansion Method)
        if (target == NumberSystem.DECIMAL) {
            return explainBaseToDecimal(source, absVal, isNegative)
        }

        // 3. Binary <-> Octal (3-bit grouping)
        if (source == NumberSystem.BINARY && target == NumberSystem.OCTAL) {
            return explainBinaryToOctal(absVal, isNegative)
        }
        if (source == NumberSystem.OCTAL && target == NumberSystem.BINARY) {
            return explainOctalToBinary(absVal, isNegative)
        }

        // 4. Binary <-> Hexadecimal (4-bit grouping)
        if (source == NumberSystem.BINARY && target == NumberSystem.HEXADECIMAL) {
            return explainBinaryToHex(absVal, isNegative)
        }
        if (source == NumberSystem.HEXADECIMAL && target == NumberSystem.BINARY) {
            return explainHexToBinary(absVal, isNegative)
        }

        // 5. Octal <-> Hexadecimal (via Binary 3-bit / 4-bit bridge)
        return explainOctalHexBridge(source, target, absVal, isNegative)
    }

    private fun explainDecimalToBase(target: NumberSystem, absVal: BigInteger, isNegative: Boolean): ExplanationDetail {
        val baseInt = target.base
        val baseBig = BigInteger.valueOf(baseInt.toLong())
        var current = absVal
        val steps = mutableListOf<String>()
        val remainders = mutableListOf<String>()

        var stepCount = 0
        while (current > BigInteger.ZERO && stepCount < MAX_STEPS) {
            val divRem = current.divideAndRemainder(baseBig)
            val quotient = divRem[0]
            val remainderInt = divRem[1].toInt()
            val remChar = if (remainderInt >= 10) {
                "${('A' + (remainderInt - 10))} (${remainderInt})"
            } else {
                remainderInt.toString()
            }
            val remRaw = if (remainderInt >= 10) ('A' + (remainderInt - 10)).toString() else remainderInt.toString()

            steps.add("$current ÷ $baseInt = $quotient   rem $remChar")
            remainders.add(remRaw)
            current = quotient
            stepCount++
        }

        val truncatedNote = if (current > BigInteger.ZERO) {
            "Steps truncated for display; complete value shown below."
        } else {
            "Read the remainders from bottom to top: ${remainders.reversed().joinToString("")}"
        }

        val resultStr = (if (isNegative) "-" else "") + absVal.toString(target.base).uppercase()

        return ExplanationDetail(
            title = "DECIMAL (${absVal}) → ${target.displayName}",
            subtitle = "Successive Division Algorithm (÷ $baseInt)",
            method = "Division by ${target.base}",
            steps = steps,
            note = truncatedNote,
            resultSummary = "$resultStr (Base $baseInt)"
        )
    }

    private fun explainBaseToDecimal(source: NumberSystem, absVal: BigInteger, isNegative: Boolean): ExplanationDetail {
        val digits = absVal.toString(source.base).uppercase()
        val base = source.base
        val steps = mutableListOf<String>()
        val len = digits.length

        if (len > 16) {
            val formula = digits.take(6) + "... (length $len)"
            return ExplanationDetail(
                title = "${source.displayName} ($digits) → DECIMAL",
                subtitle = "Positional Expansion Method",
                method = "Polynomial sum: Σ (digit × $base^power)",
                steps = listOf(
                    "Input string has $len digits in base $base.",
                    "Evaluating sum from power ${len - 1} down to 0..."
                ),
                note = "Large integer: evaluated in full precision using BigInteger.",
                resultSummary = (if (isNegative) "-" else "") + absVal.toString(10)
            )
        }

        val terms = mutableListOf<String>()
        val evaluatedTerms = mutableListOf<String>()
        for (i in 0 until len) {
            val char = digits[i]
            val power = len - 1 - i
            val digitVal = if (char in 'A'..'F') (10 + (char - 'A')) else (char - '0')
            val weight = BigInteger.valueOf(base.toLong()).pow(power)
            val termVal = BigInteger.valueOf(digitVal.toLong()).multiply(weight)

            terms.add("($char × $base^$power)")
            evaluatedTerms.add(termVal.toString())
        }

        steps.add("Formula: " + terms.joinToString(" + "))
        steps.add("Weights: " + evaluatedTerms.joinToString(" + "))
        val decimalResult = absVal.toString(10)
        steps.add("Total sum = $decimalResult")

        return ExplanationDetail(
            title = "${source.displayName} ($digits) → DECIMAL",
            subtitle = "Positional Weight Expansion",
            method = "Sum of (digit × $base^position)",
            steps = steps,
            note = "Summing positional values yields the base-10 integer.",
            resultSummary = (if (isNegative) "-" else "") + decimalResult
        )
    }

    private fun explainBinaryToOctal(absVal: BigInteger, isNegative: Boolean): ExplanationDetail {
        val binStr = absVal.toString(2)
        val padLen = (3 - (binStr.length % 3)) % 3
        val paddedBin = "0".repeat(padLen) + binStr
        val groups = paddedBin.chunked(3)
        val octalDigits = groups.map { Integer.parseInt(it, 2).toString() }

        val steps = mutableListOf<String>()
        steps.add("Original binary: $binStr")
        if (padLen > 0) {
            steps.add("Padded with $padLen leading zero(s) for 3-bit alignment: $paddedBin")
        }
        steps.add("Group into 3-bit triplets from right to left:")
        groups.forEachIndexed { idx, group ->
            val digit = octalDigits[idx]
            steps.add("  Triplet [ $group ]₂ = $digit₈")
        }
        steps.add("Combine octal digits: ${octalDigits.joinToString("")}")

        val resultStr = (if (isNegative) "-" else "") + absVal.toString(8)
        return ExplanationDetail(
            title = "BINARY → OCTAL",
            subtitle = "3-Bit Grouping Method (HSC ICT Standard)",
            method = "Binary Triplet Grouping (2³ = 8)",
            steps = steps,
            note = "Every octal digit corresponds exactly to a 3-bit binary triplet.",
            resultSummary = "$resultStr (Base 8)"
        )
    }

    private fun explainOctalToBinary(absVal: BigInteger, isNegative: Boolean): ExplanationDetail {
        val octStr = absVal.toString(8)
        val steps = mutableListOf<String>()
        steps.add("Octal digits: $octStr")
        steps.add("Expand each octal digit into its 3-bit binary equivalent:")

        val binTriplets = octStr.map { ch ->
            val digit = ch - '0'
            val triplet = Integer.toBinaryString(digit).padStart(3, '0')
            steps.add("  Digit $ch₈ → $triplet₂")
            triplet
        }
        val combined = binTriplets.joinToString("")
        val normalized = combined.trimStart('0').ifEmpty { "0" }
        steps.add("Combined binary: $combined → $normalized₂")

        val resultStr = (if (isNegative) "-" else "") + absVal.toString(2)
        return ExplanationDetail(
            title = "OCTAL → BINARY",
            subtitle = "3-Bit Expansion Method",
            method = "Octal Digit Expansion (8 = 2³)",
            steps = steps,
            note = "Replace each octal digit with its 3-bit binary code.",
            resultSummary = "$resultStr (Base 2)"
        )
    }

    private fun explainBinaryToHex(absVal: BigInteger, isNegative: Boolean): ExplanationDetail {
        val binStr = absVal.toString(2)
        val padLen = (4 - (binStr.length % 4)) % 4
        val paddedBin = "0".repeat(padLen) + binStr
        val groups = paddedBin.chunked(4)
        val hexDigits = groups.map { Integer.parseInt(it, 2).toString(16).uppercase() }

        val steps = mutableListOf<String>()
        steps.add("Original binary: $binStr")
        if (padLen > 0) {
            steps.add("Padded with $padLen leading zero(s) for 4-bit alignment: $paddedBin")
        }
        steps.add("Group into 4-bit nibbles from right to left:")
        groups.forEachIndexed { idx, group ->
            val digit = hexDigits[idx]
            val decVal = Integer.parseInt(group, 2)
            val detail = if (decVal >= 10) " = $decVal₁₀ = $digit₁₆" else " = $digit₁₆"
            steps.add("  Nibble [ $group ]₂$detail")
        }
        steps.add("Combine hexadecimal digits: ${hexDigits.joinToString("")}")

        val resultStr = (if (isNegative) "-" else "") + absVal.toString(16).uppercase()
        return ExplanationDetail(
            title = "BINARY → HEXADECIMAL",
            subtitle = "4-Bit Grouping Method (HSC ICT Standard)",
            method = "Binary Nibble Grouping (2⁴ = 16)",
            steps = steps,
            note = "Every hexadecimal character corresponds exactly to a 4-bit nibble.",
            resultSummary = "$resultStr (Base 16)"
        )
    }

    private fun explainHexToBinary(absVal: BigInteger, isNegative: Boolean): ExplanationDetail {
        val hexStr = absVal.toString(16).uppercase()
        val steps = mutableListOf<String>()
        steps.add("Hexadecimal digits: $hexStr")
        steps.add("Expand each hexadecimal character into its 4-bit binary equivalent:")

        val binNibbles = hexStr.map { ch ->
            val digit = if (ch in 'A'..'F') 10 + (ch - 'A') else (ch - '0')
            val nibble = Integer.toBinaryString(digit).padStart(4, '0')
            steps.add("  Digit $ch₁₆ ($digit₁₀) → $nibble₂")
            nibble
        }
        val combined = binNibbles.joinToString("")
        val normalized = combined.trimStart('0').ifEmpty { "0" }
        steps.add("Combined binary: $combined → $normalized₂")

        val resultStr = (if (isNegative) "-" else "") + absVal.toString(2)
        return ExplanationDetail(
            title = "HEXADECIMAL → BINARY",
            subtitle = "4-Bit Expansion Method",
            method = "Hex Digit Expansion (16 = 2⁴)",
            steps = steps,
            note = "Replace each hexadecimal digit with its 4-bit binary representation.",
            resultSummary = "$resultStr (Base 2)"
        )
    }

    private fun explainOctalHexBridge(
        source: NumberSystem,
        target: NumberSystem,
        absVal: BigInteger,
        isNegative: Boolean
    ): ExplanationDetail {
        val bin = absVal.toString(2)
        val steps = mutableListOf<String>()

        if (source == NumberSystem.OCTAL && target == NumberSystem.HEXADECIMAL) {
            val octStr = absVal.toString(8)
            val hexStr = absVal.toString(16).uppercase()
            steps.add("Step 1: Convert Octal ($octStr) to Binary via 3-bit expansion:")
            octStr.forEach {
                val d = it - '0'
                steps.add("  $it₈ → ${Integer.toBinaryString(d).padStart(3, '0')}₂")
            }
            steps.add("Intermediate binary: $bin₂")
            steps.add("Step 2: Convert Binary to Hexadecimal via 4-bit grouping:")
            val padLen = (4 - (bin.length % 4)) % 4
            val paddedBin = "0".repeat(padLen) + bin
            paddedBin.chunked(4).forEach {
                val dec = Integer.parseInt(it, 2)
                steps.add("  [ $it ]₂ → ${dec.toString(16).uppercase()}₁₆")
            }
            steps.add("Resulting Hex: $hexStr")

            return ExplanationDetail(
                title = "OCTAL → HEXADECIMAL",
                subtitle = "Binary Bridge Method (HSC ICT Shortcut)",
                method = "Octal (3-bit) → Binary → Hex (4-bit)",
                steps = steps,
                note = "Converting via Binary is faster than converting through Decimal.",
                resultSummary = (if (isNegative) "-" else "") + hexStr
            )
        } else {
            // Hex to Octal
            val hexStr = absVal.toString(16).uppercase()
            val octStr = absVal.toString(8)
            steps.add("Step 1: Convert Hex ($hexStr) to Binary via 4-bit expansion:")
            hexStr.forEach {
                val d = if (it in 'A'..'F') 10 + (it - 'A') else (it - '0')
                steps.add("  $it₁₆ → ${Integer.toBinaryString(d).padStart(4, '0')}₂")
            }
            steps.add("Intermediate binary: $bin₂")
            steps.add("Step 2: Convert Binary to Octal via 3-bit grouping:")
            val padLen = (3 - (bin.length % 3)) % 3
            val paddedBin = "0".repeat(padLen) + bin
            paddedBin.chunked(3).forEach {
                val dec = Integer.parseInt(it, 2)
                steps.add("  [ $it ]₂ → $dec₈")
            }
            steps.add("Resulting Octal: $octStr")

            return ExplanationDetail(
                title = "HEXADECIMAL → OCTAL",
                subtitle = "Binary Bridge Method (HSC ICT Shortcut)",
                method = "Hex (4-bit) → Binary → Octal (3-bit)",
                steps = steps,
                note = "Converting via Binary is faster than converting through Decimal.",
                resultSummary = (if (isNegative) "-" else "") + octStr
            )
        }
    }
}
