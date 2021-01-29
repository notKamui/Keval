package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ShuntingYardTest {
    @Test
    fun syTest() {
        assertEquals(8.0, "3 + 5 * (2-1)".toAbstractSyntaxTree().eval())
    }

    @Test
    fun kevalTest() {
        assertEquals(50.0, "(2 + 3)(4 + 6)".keval())
        assertEquals(50.0, Keval.eval("(2 + 3)(4 + 6)"))
        assertFailsWith<KevalInvalidExpressionException> {
            "((3+1) - 2".keval()
        }

        println("(3+1) / 0".keval())
    }
}
