package com.example

import com.example.model.ConversionEngine
import com.example.model.ConversionExplainer
import com.example.model.NumberSystem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigInteger

class ConversionEngineTest {

    @Test
    fun testMandatoryCasesFromSpecification() {
        // 1. Decimal 0 -> 0, 0, 0, 0
        val dec0 = ConversionEngine.convert(NumberSystem.DECIMAL, "0")
        assertTrue(dec0.isValid)
        assertEquals("0", dec0.binaryValue)
        assertEquals("0", dec0.octalValue)
        assertEquals("0", dec0.decimalValue)
        assertEquals("0", dec0.hexValue)

        // 2. Decimal 10 -> 1010, 12, 10, A
        val dec10 = ConversionEngine.convert(NumberSystem.DECIMAL, "10")
        assertTrue(dec10.isValid)
        assertEquals("1010", dec10.binaryValue)
        assertEquals("12", dec10.octalValue)
        assertEquals("10", dec10.decimalValue)
        assertEquals("A", dec10.hexValue)

        // 3. Decimal 45 -> 101101, 55, 45, 2D
        val dec45 = ConversionEngine.convert(NumberSystem.DECIMAL, "45")
        assertTrue(dec45.isValid)
        assertEquals("101101", dec45.binaryValue)
        assertEquals("55", dec45.octalValue)
        assertEquals("45", dec45.decimalValue)
        assertEquals("2D", dec45.hexValue)

        // 4. Decimal 255 -> 11111111, 377, 255, FF
        val dec255 = ConversionEngine.convert(NumberSystem.DECIMAL, "255")
        assertTrue(dec255.isValid)
        assertEquals("11111111", dec255.binaryValue)
        assertEquals("377", dec255.octalValue)
        assertEquals("255", dec255.decimalValue)
        assertEquals("FF", dec255.hexValue)

        // 5. Binary 101101 -> 101101, 55, 45, 2D
        val bin101101 = ConversionEngine.convert(NumberSystem.BINARY, "101101")
        assertTrue(bin101101.isValid)
        assertEquals("101101", bin101101.binaryValue)
        assertEquals("55", bin101101.octalValue)
        assertEquals("45", bin101101.decimalValue)
        assertEquals("2D", bin101101.hexValue)

        // 6. Octal 77 -> 111111, 77, 63, 3F
        val oct77 = ConversionEngine.convert(NumberSystem.OCTAL, "77")
        assertTrue(oct77.isValid)
        assertEquals("111111", oct77.binaryValue)
        assertEquals("77", oct77.octalValue)
        assertEquals("63", oct77.decimalValue)
        assertEquals("3F", oct77.hexValue)

        // 7. Hexadecimal FF -> 11111111, 377, 255, FF
        val hexFF = ConversionEngine.convert(NumberSystem.HEXADECIMAL, "FF")
        assertTrue(hexFF.isValid)
        assertEquals("11111111", hexFF.binaryValue)
        assertEquals("377", hexFF.octalValue)
        assertEquals("255", hexFF.decimalValue)
        assertEquals("FF", hexFF.hexValue)

        // 8. Hexadecimal 2D -> 101101, 55, 45, 2D
        val hex2D = ConversionEngine.convert(NumberSystem.HEXADECIMAL, "2D")
        assertTrue(hex2D.isValid)
        assertEquals("101101", hex2D.binaryValue)
        assertEquals("55", hex2D.octalValue)
        assertEquals("45", hex2D.decimalValue)
        assertEquals("2D", hex2D.hexValue)
    }

    @Test
    fun testEmptyInput() {
        val empty = ConversionEngine.convert(NumberSystem.DECIMAL, "")
        assertTrue(empty.isValid)
        assertEquals("", empty.binaryValue)
        assertEquals("", empty.octalValue)
        assertEquals("", empty.decimalValue)
        assertEquals("", empty.hexValue)
    }

    @Test
    fun testInvalidDigitsHandling() {
        // Binary with '2'
        val invalidBin = ConversionEngine.convert(NumberSystem.BINARY, "102")
        assertFalse(invalidBin.isValid)
        assertNotNull(invalidBin.errorMessage)
        assertTrue(invalidBin.errorMessage!!.contains("'2'"))

        // Octal with '8'
        val invalidOct = ConversionEngine.convert(NumberSystem.OCTAL, "78")
        assertFalse(invalidOct.isValid)
        assertTrue(invalidOct.errorMessage!!.contains("'8'"))

        // Decimal with 'A'
        val invalidDec = ConversionEngine.convert(NumberSystem.DECIMAL, "12A")
        assertFalse(invalidDec.isValid)
        assertTrue(invalidDec.errorMessage!!.contains("'A'"))

        // Hex with 'G'
        val invalidHex = ConversionEngine.convert(NumberSystem.HEXADECIMAL, "12G")
        assertFalse(invalidHex.isValid)
        assertTrue(invalidHex.errorMessage!!.contains("'G'"))
    }

    @Test
    fun testLeadingZerosNormalization() {
        val leadingZeroBin = ConversionEngine.convert(NumberSystem.BINARY, "00101")
        assertTrue(leadingZeroBin.isValid)
        assertEquals("5", leadingZeroBin.decimalValue)
        assertEquals("5", leadingZeroBin.octalValue)
        assertEquals("5", leadingZeroBin.hexValue)

        val multipleZeros = ConversionEngine.convert(NumberSystem.DECIMAL, "000")
        assertTrue(multipleZeros.isValid)
        assertEquals("0", multipleZeros.binaryValue)
        assertEquals("0", multipleZeros.decimalValue)
    }

    @Test
    fun testLowercaseHexadecimal() {
        val lowerHex = ConversionEngine.convert(NumberSystem.HEXADECIMAL, "ff")
        assertTrue(lowerHex.isValid)
        assertEquals("255", lowerHex.decimalValue)
        assertEquals("11111111", lowerHex.binaryValue)
        assertEquals("377", lowerHex.octalValue)
        assertEquals("FF", lowerHex.hexValue)
    }

    @Test
    fun testNegativeNumbersConsistently() {
        val negDec = ConversionEngine.convert(NumberSystem.DECIMAL, "-45")
        assertTrue(negDec.isValid)
        assertEquals("-101101", negDec.binaryValue)
        assertEquals("-55", negDec.octalValue)
        assertEquals("-45", negDec.decimalValue)
        assertEquals("-2D", negDec.hexValue)
    }

    @Test
    fun testLargeIntegersArbitraryPrecision() {
        // Large integer: 10^20
        val bigValStr = "100000000000000000000"
        val decBig = ConversionEngine.convert(NumberSystem.DECIMAL, bigValStr)
        assertTrue(decBig.isValid)
        val expectedBigInt = BigInteger(bigValStr)
        assertEquals(expectedBigInt.toString(2), decBig.binaryValue)
        assertEquals(expectedBigInt.toString(8), decBig.octalValue)
        assertEquals(expectedBigInt.toString(10), decBig.decimalValue)
        assertEquals(expectedBigInt.toString(16).uppercase(), decBig.hexValue)
    }

    @Test
    fun testExplanationGeneratorSteps() {
        // Decimal 45 to Binary division steps
        val explanation = ConversionExplainer.explain(
            source = NumberSystem.DECIMAL,
            target = NumberSystem.BINARY,
            value = BigInteger.valueOf(45)
        )
        assertNotNull(explanation)
        assertTrue(explanation.steps.isNotEmpty())
        assertTrue(explanation.steps.any { it.contains("45 ÷ 2 = 22") })
        assertTrue(explanation.steps.any { it.contains("1 ÷ 2 = 0") })
        assertEquals("101101 (Base 2)", explanation.resultSummary)

        // Binary to Octal 3-bit grouping
        val groupExplain = ConversionExplainer.explain(
            source = NumberSystem.BINARY,
            target = NumberSystem.OCTAL,
            value = BigInteger.valueOf(45)
        )
        assertTrue(groupExplain.steps.any { it.contains("Triplet") || it.contains("3-bit") })
    }
}
