package com.notkamui.keval

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class KevalBigDecimalLogicalTest {

    private fun eval(expr: String): BigDecimal = expr.kevalBigDecimal()
    private val t = BigDecimal.ONE
    private val f = BigDecimal.ZERO

    @Test
    fun boolAndNot() {
        assertEquals(t, eval("bool(1)"))
        assertEquals(f, eval("bool(0)"))
        assertEquals(t, eval("not(0)"))
        assertEquals(f, eval("not(1)"))
    }

    @Test
    fun truthinessUsesZeroComparison() {
        assertEquals(t, eval("bool(0.0001)"))
        assertEquals(f, eval("bool(0.0)"))
    }

    @Test
    fun eq() {
        assertEquals(t, eval("eq(1, 1, 1)"))
        assertEquals(f, eval("eq(1, 2)"))
        assertEquals(t, eval("eq(1.0, 1.00)"))
    }

    @Test
    fun ne() {
        assertEquals(t, eval("ne(1, 2, 3)"))
        assertEquals(f, eval("ne(1, 1)"))
        assertEquals(f, eval("ne(1.0, 1.00)"))
    }

    @Test
    fun comparisons() {
        assertEquals(t, eval("gt(3, 2)"))
        assertEquals(f, eval("gt(2, 3)"))
        assertEquals(t, eval("lt(1, 2)"))
        assertEquals(t, eval("ge(5, 5)"))
        assertEquals(t, eval("ge(6, 5)"))
        assertEquals(f, eval("ge(4, 5)"))
        assertEquals(t, eval("le(5, 5)"))
        assertEquals(t, eval("le(4, 5)"))
        assertEquals(f, eval("le(6, 5)"))
    }

    @Test
    fun andOr() {
        assertEquals(t, eval("and(1, 1, 1)"))
        assertEquals(f, eval("and(1, 0, 1)"))
        assertEquals(t, eval("or(0, 0, 1)"))
        assertEquals(f, eval("or(0, 0, 0)"))
    }

    @Test
    fun nandNor() {
        assertEquals(f, eval("nand(1, 1)"))
        assertEquals(t, eval("nand(1, 0)"))
        assertEquals(f, eval("nor(1, 0)"))
        assertEquals(t, eval("nor(0, 0)"))
    }

    @Test
    fun xorXnor() {
        assertEquals(f, eval("xor(1, 1)"))
        assertEquals(t, eval("xor(1, 0)"))
        assertEquals(t, eval("xnor(1, 1)"))
        assertEquals(f, eval("xnor(1, 0)"))
    }

    @Test
    fun implyNimply() {
        assertEquals(t, eval("imply(0, 0)"))
        assertEquals(f, eval("imply(1, 0)"))
        assertEquals(t, eval("nimply(1, 0)"))
        assertEquals(f, eval("nimply(0, 1)"))
    }

    @Test
    fun comprehensiveLogicalExpression() {
        val expr = """
            and(
                not(lt(5, 3)),
                or(
                    gt(4, 2),
                    xor(
                        eq(1, 1, 1),
                        ne(1, 2, 3)
                    )
                ),
                nand(
                    ge(5, 5),
                    not(le(3, 4))
                ),
                nor(
                    imply(1, 0),
                    nimply(1, 1)
                ),
                xnor(1, 1)
            )
        """.trimIndent()
        assertEquals(t, eval(expr))
    }
}
