package com.notkamui.keval

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class KevalBigDecimalArithmeticTest {

    @Test
    fun addition() {
        assertDecimalEquals("10", eval("3 + 7"))
        assertDecimalEquals("0.3", eval("0.1 + 0.2"))
    }

    @Test
    fun subtraction() {
        assertDecimalEquals("4", eval("10 - 6"))
        assertDecimalEquals("-3", eval("2 - 5"))
    }

    @Test
    fun multiplication() {
        assertDecimalEquals("42", eval("6 * 7"))
        assertDecimalEquals("0.06", eval("0.2 * 0.3"))
    }

    @Test
    fun division() {
        assertDecimalEquals("2.5", eval("5 / 2"))
        assertDecimalEquals("0.3333333333333333333333333333333333", eval("1 / 3"))
    }

    @Test
    fun modulo() {
        assertDecimalEquals("1", eval("10 % 3"))
        assertDecimalEquals("0", eval("9 % 3"))
    }

    @Test
    fun unaryPlusAndMinus() {
        assertDecimalEquals("5", eval("+5"))
        assertDecimalEquals("-5", eval("-5"))
        assertDecimalEquals("3", eval("5 + -2"))
    }

    @Test
    fun integerPower() {
        assertDecimalEquals("8", eval("2 ^ 3"))
        assertDecimalEquals("1", eval("5 ^ 0"))
        assertDecimalEquals("1024", eval("2 ^ 10"))
    }

    @Test
    fun powerRightAssociativity() {
        assertDecimalEquals("512", eval("2 ^ 3 ^ 2"))
    }

    @Test
    fun powerNegativeIntegerExponent() {
        assertDecimalEquals("0.25", eval("2 ^ -2"))
        assertDecimalEquals("0.5", eval("2 ^ -1"))
    }

    @Test
    fun zeroToNegativePowerThrows() {
        assertFailsWith<KevalInvalidArgumentException> { eval("0 ^ -1") }
    }

    @Test
    fun operatorPrecedence() {
        assertDecimalEquals("8", eval("3 + 5 * (2 - 1)"))
        assertDecimalEquals("8", eval("(3 + 5) * (2 - 1)"))
        assertDecimalEquals("7", eval("1 + 2 * 3"))
    }

    @Test
    fun implicitMultiplication() {
        assertDecimalEquals("50", eval("(2 + 3)(4 + 6)"))
        assertDecimalEquals("12", eval("3(2 + 2)"))
    }

    @Test
    fun implicitMultiplicationBetweenLiterals() {
        assertDecimalEquals("2", eval("1 2"))
    }

    @Test
    fun nestedParentheses() {
        assertDecimalEquals("1", eval("((((1))))"))
    }

    @Test
    fun largePrecisionLiterals() {
        assertDecimalEquals(
            "123456789012345678901234567890.123456789",
            eval("123456789012345678901234567890.123456789")
        )
    }

    @Test
    fun negativeLiteralAndExpression() {
        assertDecimalEquals("-15", eval("-3 * 5"))
        assertDecimalEquals("15", eval("-3 * -5"))
    }

    @Test
    fun divisionByZeroThrows() {
        assertFailsWith<KevalZeroDivisionException> { eval("1 / 0") }
        assertFailsWith<KevalZeroDivisionException> { eval("0 / 0") }
    }

    @Test
    fun moduloByZeroThrows() {
        assertFailsWith<KevalZeroDivisionException> { eval("1 % 0") }
    }

    @Test
    fun nonIntegerPowerThrows() {
        assertFailsWith<KevalInvalidArgumentException> { eval("2 ^ 0.5") }
        assertFailsWith<KevalInvalidArgumentException> { eval("2 ^ 1.5") }
    }

    @Test
    fun negFunction() {
        assertDecimalEquals("-7", eval("neg(7)"))
        assertDecimalEquals("7", eval("neg(-7)"))
    }

    @Test
    fun complexExpression() {
        assertDecimalEquals("14.5", eval("(sum(1, 2, 3) * 2) + (10 / 4)"))
    }

    private fun eval(expr: String): BigDecimal = expr.evalDecimal()
}
