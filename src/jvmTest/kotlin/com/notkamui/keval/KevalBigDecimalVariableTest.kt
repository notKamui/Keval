package com.notkamui.keval

import java.math.BigDecimal
import java.math.MathContext
import kotlin.test.Test

class KevalBigDecimalVariableTest {

    @Test
    fun variablesWithBindings() {
        assertDecimalEquals(
            "15",
            Keval.create(KevalNumbers.BigDecimal) { includeDefault() }
                .eval("price * qty", mapOf("price" to BigDecimal("3"), "qty" to BigDecimal("5")))
        )
    }

    @Test
    fun customMathContext() {
        val lowPrecision = KevalNumberBigDecimal.withContext(MathContext(4))
        val result = Keval.create(lowPrecision) { includeDefault() }.eval("1 / 3")
        assertDecimalEquals("0.3333", result)
    }
}
