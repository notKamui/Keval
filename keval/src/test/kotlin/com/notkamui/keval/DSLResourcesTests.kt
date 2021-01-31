package com.notkamui.keval

import com.notkamui.keval.framework.Resources
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class DSLResourcesTests {
    @Test
    fun builtInOperators() {
        val operators = Resources().loadBuiltInOperators()
        assertEquals("+-*/%^()".toSet(), operators.keys)
    }

    @Test
    fun testGetAllResources() {
        val operators = Resources().loadAllResources()
        assertEquals("+-*/%^();".toSet(), operators.keys)
    }

    @Test
    fun testSpecificResouces() {
        val operators = Resources().loadResources("com.notkamui.keval.testOperators")
        assertEquals(";".toSet(), operators.keys)
    }
}

class DLSTest {
    @Test
    fun checkSimpleDLS() {
        val kvl = Keval {
            +loadResources("com.notkamui.keval.testOperators")
        }

        assertEquals(
                5.0,
                kvl.eval("3;4")
        )

        assertEquals(
                10.0,
                kvl.eval("((3;4)*1.2);8")
        )

        assertFailsWith(KevalInvalidOperatorException::class) {
            kvl.eval("6+4")
        }

        assertFailsWith(KevalInvalidOperatorException::class) {
            kvl.eval("6;(4-1)")
        }
    }

    @Test
    fun checkCombinedDSL() {
        val kvl = Keval {
            +loadResources("com.notkamui.keval.testOperators")
            +loadBuiltInOperators()
        }

        assertEquals(
                4.1,
                kvl.eval("3;4-0.9")
        )

        assertEquals(
                8.85663593,
                (kvl.eval("((3;4)-1.2);8")*10.0.pow(8)).toInt()/10.0.pow(8)
        )
    }
}