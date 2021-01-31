package com.notkamui.keval

import com.notkamui.keval.resources.KevalResources
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * DSL Tests
 */
class DSLKevalResourcesTests {
    /**
     * Checking built in operators
     */
    @Test
    fun builtInOperators() {
        val operators = KevalResources().loadBuiltInOperators()
        assertEquals("+-*/%^()".toSet(), operators.keys)
    }

    /**
     * Checking with "all resources"
     */
    @Test
    fun testGetAllResources() {
        val operators = KevalResources().loadAllResources()
        assertEquals("+-*/%^();".toSet(), operators.keys)
    }

    /**
     * Checking with specific resources
     */
    @Test
    fun testSpecificResources() {
        val operators = KevalResources().loadResources("com.notkamui.keval.testOperators")
        assertEquals(";".toSet(), operators.keys)
    }
}

/**
 * DSL tests
 */
class DLSTest {
    /**
     * Checking with a simple DSL
     */
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

    /**
     * Checking with combined resources
     */
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
            (kvl.eval("((3;4)-1.2);8") * 10.0.pow(8)).toInt() / 10.0.pow(8)
        )
    }
}
