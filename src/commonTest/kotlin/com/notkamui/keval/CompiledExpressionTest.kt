package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class CompiledExpressionTest {

    private val keval = Keval.create(KevalNumbers.real) { includeDefault() }

    @Test
    fun compileOnceEvalManyTimes() {
        val compiled = keval.compile("2 + 3 * 4")
        assertEquals(14.0, compiled.eval())
        assertEquals(14.0, compiled.eval())
    }

    @Test
    fun compiledExpressionWithBindings() {
        val compiled = keval.compile("base * rate")
        assertEquals(50.0, compiled.eval(mapOf("base" to 10.0, "rate" to 5.0)))
        assertEquals(20.0, compiled.eval(mapOf("base" to 4.0, "rate" to 5.0)))
    }

    @Test
    fun compileIsDistinctFromEvalEachTime() {
        val compiled = keval.compile("1 + 1")
        val direct = keval.eval("1 + 1")
        assertEquals(direct, compiled.eval())
        assertNotSame(compiled, keval.compile("1 + 1"))
    }

    @Test
    fun compiledEvalOrNullAndResult() {
        val compiled = keval.compile("missing + 1")
        assertEquals(null, compiled.evalOrNull())
        assertEquals(true, compiled.evalResult().isFailure)
    }
}
