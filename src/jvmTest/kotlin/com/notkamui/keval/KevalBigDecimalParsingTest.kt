package com.notkamui.keval

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KevalBigDecimalParsingTest {

    @Test
    fun scientificNotationIntegerExponent() {
        assertDecimalEquals("10000000000", "1e10".evalDecimal())
        assertDecimalEquals("1000", "1e3".evalDecimal())
    }

    @Test
    fun scientificNotationDecimal() {
        assertDecimalEquals("0.0015", "1.5e-3".evalDecimal())
        assertDecimalEquals("1500", "1.5e3".evalDecimal())
    }

    @Test
    fun scientificNotationWithExplicitSign() {
        assertDecimalEquals("100", "1e+2".evalDecimal())
        assertDecimalEquals("0.01", "1e-2".evalDecimal())
    }

    @Test
    fun scientificNotationInExpression() {
        assertDecimalEquals("20000000000", "1e10 + 1e10".evalDecimal())
        assertDecimalEquals("2", "1e0 + 1".evalDecimal())
    }

    @Test
    fun isValidLiteralAcceptsNumericForms() {
        assertTrue(KevalNumberBigDecimal.isValidLiteral("42"))
        assertTrue(KevalNumberBigDecimal.isValidLiteral("3.14"))
        assertTrue(KevalNumberBigDecimal.isValidLiteral("1e10"))
        assertTrue(KevalNumberBigDecimal.isValidLiteral("-2.5"))
    }

    @Test
    fun isValidLiteralRejectsNonNumeric() {
        assertFalse(KevalNumberBigDecimal.isValidLiteral("abc"))
        assertFalse(KevalNumberBigDecimal.isValidLiteral(""))
        assertFalse(KevalNumberBigDecimal.isValidLiteral("1..2"))
    }

    @Test
    fun parseLiteralMatchesBigDecimalConstructor() {
        assertEquals(0, BigDecimal("-123.456").compareTo(KevalNumberBigDecimal.parseLiteral("-123.456")))
    }

    @Test
    fun tokenizePreservesScientificLiteral() {
        val tokens = "1e10 + 2e-3".tokenize(
            KevalNumberBigDecimal,
            KevalNumberBigDecimal.defaultResources()
        )
        assertEquals(listOf("1e10", "+", "2e-3"), tokens)
    }

    @Test
    fun tokenizeImplicitMultiplication() {
        val tokens = "(2+3)(4+1)".tokenize(
            KevalNumberBigDecimal,
            KevalNumberBigDecimal.defaultResources()
        )
        assertEquals(listOf("(", "2", "+", "3", ")", "*", "(", "4", "+", "1", ")"), tokens)
    }
}
