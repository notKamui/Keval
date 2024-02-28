package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests on the Shunting-yard algorithm
 */
class GrammarTest {

    /**
     * Tests SYA
     */
    @Test
    fun grammarTest() {
        val operators = KevalBuilder.DEFAULT_RESOURCES
        assertEquals(8.0, "3 + 5 * (2-1)".toAST(operators).eval())
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

    /**
     * Tests Keval on empty expression
     */
    @Test
    fun emptyFailTest() {
        assertFailsWith<KevalInvalidExpressionException> {
            "".keval()
        }
        assertFailsWith<KevalInvalidExpressionException> {
            "()".keval()
        }
    }
}
