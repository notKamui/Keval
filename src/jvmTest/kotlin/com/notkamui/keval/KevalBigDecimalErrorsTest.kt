package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertFailsWith

class KevalBigDecimalErrorsTest {

    @Test
    fun emptyExpressionThrows() {
        assertFailsWith<KevalInvalidExpressionException> { "".kevalBigDecimal() }
        assertFailsWith<KevalInvalidExpressionException> { "   ".kevalBigDecimal() }
        assertFailsWith<KevalInvalidExpressionException> { "()".kevalBigDecimal() }
    }

    @Test
    fun mismatchedParenthesesThrow() {
        assertFailsWith<KevalInvalidExpressionException> { "(1 + 2".kevalBigDecimal() }
        assertFailsWith<KevalInvalidExpressionException> { "1 + 2)".kevalBigDecimal() }
        assertFailsWith<KevalInvalidExpressionException> { "(3+1)) - 2".kevalBigDecimal() }
    }

    @Test
    fun invalidSymbolThrows() {
        assertFailsWith<KevalInvalidSymbolException> { "1 + a".kevalBigDecimal() }
    }

    @Test
    fun unexpectedTokenThrows() {
        assertFailsWith<KevalInvalidExpressionException> { "1 +".kevalBigDecimal() }
        assertFailsWith<KevalInvalidExpressionException> { "(1 +".kevalBigDecimal() }
    }

    @Test
    fun unknownFunctionThrows() {
        assertFailsWith<KevalInvalidSymbolException> { "sin(1)".kevalBigDecimal() }
    }

    @Test
    fun wrongFunctionArityThrows() {
        assertFailsWith<KevalInvalidExpressionException> { "abs(1, 2)".kevalBigDecimal() }
        assertFailsWith<KevalInvalidExpressionException> { "gt(1)".kevalBigDecimal() }
    }

    @Test
    fun invalidCommaOutsideFunctionThrows() {
        assertFailsWith<KevalInvalidSymbolException> { "1, 2".kevalBigDecimal() }
    }
}
