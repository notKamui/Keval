package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests on the Shunting-yard algorithm
 */
class ShuntingYardTest {

    /**
     * Tests SYA
     */
    @Test
    fun syTest() {
        val operators = Resources.defaultOperators
        assertEquals(8.0, "3 + 5 * (2-1)".toAbstractSyntaxTree(operators).eval())
    }

    /**
     * Tests Keval
     */
    @Test
    fun kevalTest() {
        assertEquals(50.0, "(2 + 3)(4 + 6)".keval())
        assertEquals(50.0, Keval.eval("(2 + 3)(4 + 6)"))
        assertFailsWith<KevalInvalidExpressionException> {
            "(3+1)) - 2".keval()
        }
        assertFailsWith<KevalZeroDivisionException> {
            "(3+1) / 0".keval()
        }
    }
}
