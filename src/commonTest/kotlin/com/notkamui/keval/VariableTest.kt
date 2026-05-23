package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class VariableTest {

    private val keval = Keval.create(KevalNumbers.real) { includeDefault() }

    @Test
    fun evalWithBindings() {
        assertEquals(10.0, keval.eval("x + y", mapOf("x" to 3.0, "y" to 7.0)))
    }

    @Test
    fun unresolvedVariableThrows() {
        assertFailsWith<KevalUnresolvedVariableException> {
            keval.eval("x + 1")
        }
    }

    @Test
    fun missingBindingThrows() {
        assertFailsWith<KevalUnresolvedVariableException> {
            keval.eval("x + y", mapOf("x" to 1.0))
        }
    }

    @Test
    fun implicitMultiplicationWithVariable() {
        assertEquals(6.0, keval.eval("x(y + 1)", mapOf("x" to 2.0, "y" to 2.0)))
        assertEquals(6.0, keval.eval("2 x", mapOf("x" to 3.0)))
    }

    @Test
    fun compileCollectsVariables() {
        val compiled = keval.compile("x * y + z")
        assertEquals(setOf("x", "y", "z"), compiled.variables)
        assertEquals(20.0, compiled.eval(mapOf("x" to 2.0, "y" to 5.0, "z" to 10.0)))
    }

    @Test
    fun constantTakesPrecedenceOverVariableName() {
        val custom = Keval.create(KevalNumbers.real) {
            includeDefault()
            constant {
                name = "x"
                value = 99.0
            }
        }
        assertEquals(100.0, custom.eval("x + 1"))
    }

    @Test
    fun evalWithExtension() {
        assertEquals(5.0, "2 + 3".evalWith(KevalNumbers.real))
    }

    @Test
    fun compileWithExtension() {
        val compiled = "a * 2".compileWith(KevalNumbers.real)
        assertEquals(setOf("a"), compiled.variables)
        assertEquals(8.0, compiled.eval(mapOf("a" to 4.0)))
    }

    @Test
    fun evalOrNullReturnsNullOnError() {
        assertEquals(null, keval.evalOrNull("x + 1"))
        assertEquals(3.0, keval.evalOrNull("1 + 2"))
    }

    @Test
    fun evalResultCapturesException() {
        val result = keval.evalResult("x + 1")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is KevalUnresolvedVariableException)
    }
}
