package com.notkamui.keval

import java.math.BigDecimal
import kotlin.test.assertEquals

internal fun assertDecimalEquals(expected: String, actual: BigDecimal) {
    assertEquals(
        0,
        BigDecimal(expected).compareTo(actual),
        "expected $expected but was $actual"
    )
}

internal fun String.evalDecimal(): BigDecimal = this.kevalBigDecimal()
