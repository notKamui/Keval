package com.notkamui.keval

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class KevalBigDecimalFunctionsTest {

    @Test
    fun abs() {
        assertDecimalEquals("7", eval("abs(-7)"))
        assertDecimalEquals("7", eval("abs(7)"))
        assertDecimalEquals("0", eval("abs(0)"))
    }

    @Test
    fun sign() {
        assertDecimalEquals("-1", eval("sign(-42)"))
        assertDecimalEquals("0", eval("sign(0)"))
        assertDecimalEquals("1", eval("sign(42)"))
    }

    @Test
    fun min() {
        assertDecimalEquals("1", eval("min(3, 1, 2)"))
        assertDecimalEquals("-5", eval("min(-5, 0, 5)"))
        assertDecimalEquals("7", eval("min(7)"))
    }

    @Test
    fun max() {
        assertDecimalEquals("9", eval("max(3, 9, 2)"))
        assertDecimalEquals("5", eval("max(-5, 0, 5)"))
    }

    @Test
    fun sum() {
        assertDecimalEquals("6", eval("sum(1, 2, 3)"))
        assertDecimalEquals("0", eval("sum()"))
        assertDecimalEquals("100", eval("sum(100)"))
    }

    @Test
    fun avg() {
        assertDecimalEquals("2", eval("avg(1, 2, 3)"))
        assertDecimalEquals("5", eval("avg(5)"))
        assertDecimalEquals("2.5", eval("avg(1, 2, 3, 4)"))
    }

    @Test
    fun ceil() {
        assertDecimalEquals("3", eval("ceil(2.1)"))
        assertDecimalEquals("-2", eval("ceil(-2.9)"))
        assertDecimalEquals("3", eval("ceil(2.9)"))
    }

    @Test
    fun floor() {
        assertDecimalEquals("2", eval("floor(2.9)"))
        assertDecimalEquals("-3", eval("floor(-2.1)"))
    }

    @Test
    fun round() {
        assertDecimalEquals("3", eval("round(2.5)"))
        assertDecimalEquals("2", eval("round(2.4)"))
        assertDecimalEquals("-2", eval("round(-2.4)"))
        assertDecimalEquals("-3", eval("round(-2.5)"))
    }

    @Test
    fun trunc() {
        assertDecimalEquals("2", eval("trunc(2.9)"))
        assertDecimalEquals("-2", eval("trunc(-2.9)"))
        assertDecimalEquals("0", eval("trunc(0.99)"))
    }

    @Test
    fun nestedFunctions() {
        assertDecimalEquals("6", eval("sum(abs(-1), abs(-2), abs(-3))"))
        assertDecimalEquals("3", eval("max(min(1, 5), min(3, 9))"))
    }

    private fun eval(expr: String): BigDecimal = expr.evalDecimal()
}
