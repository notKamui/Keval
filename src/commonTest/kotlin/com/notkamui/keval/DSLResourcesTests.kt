package com.notkamui.keval

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun hypotenuse(x: Double, y: Double): Double = sqrt(x.pow(2) + y.pow(2))

class DLSTest {
    @Test
    fun checkSimpleDLS() {
        val kvl = Keval {
            operator {
                symbol = ';'
                implementation = ::hypotenuse
                precedence = 3
                isLeftAssociative = true
            }
        }

        assertEquals(
            5.0,
            kvl.eval("3;4")
        )

        assertEquals(
            10.0,
            kvl.eval("((3;4)*1.2);8")
        )

        assertFailsWith(KevalInvalidSymbolException::class) {
            kvl.eval("6+4")
        }

        assertFailsWith(KevalInvalidSymbolException::class) {
            kvl.eval("6;(4-1)")
        }
    }

    @Test
    fun checkCombinedDSL() {
        val kvl = Keval {
            includeDefault()
            operator {
                symbol = ';'
                implementation = ::hypotenuse
                precedence = 3
                isLeftAssociative = true
            }
            function {
                name = "max"
                arity = 2
                implementation = { args ->
                    max(args[0], args[1])
                }
            }
            constant {
                name = "PHI"
                value = 1.618
            }
        }

        assertEquals(
            9.0,
            kvl.eval("max(4, 2) + 5")
        )

        assertEquals(
            5.0,
            kvl.eval("neg(5) + 10")
        )

        assertEquals(
            8.85663593,
            (kvl.eval("((3;4)-1.2);8") * 10.0.pow(8)).toInt() / 10.0.pow(8)
        )

        assertEquals(
            2.6179240000000004,
            kvl.eval("PHI^2")
        )
    }

    @Test
    fun conflictTests() {
        val kvl = Keval {
            function {
                name = "a"
                arity = 1
                implementation = { it[0] }
            }

            function {
                name = "ab_c"
                arity = 1
                implementation = { it[0] + 1 }
            }
        }

        assertEquals(
            2.0,
            kvl.eval("ab_c(1)")
        )
    }

    @Test
    fun checkWith() {
        val kvl = Keval()
            .withDefault()
            .withOperator(
                ';',
                3,
                isLeftAssociative = true,
                ::hypotenuse
            ).withFunction(
                "max",
                2
            ) { max(it[0], it[1]) }
            .withConstant("PHI", 1.618)

        assertEquals(
            9.0,
            kvl.eval("max(4, 2) + 5")
        )

        assertEquals(
            5.0,
            kvl.eval("neg(5) + 10")
        )

        assertEquals(
            8.85663593,
            (kvl.eval("((3;4)-1.2);8") * 10.0.pow(8)).toInt() / 10.0.pow(8)
        )

        assertEquals(
            2.6179240000000004,
            kvl.eval("PHI^2")
        )
    }

    @Test
    fun checkOrder() {
        var first = 1.0;
        val second = 2.0;
        val third = 3.0;
        val k = Keval {
            includeDefault()
            function {
                name = "test"
                arity = 3
                implementation = { args ->
                    assertEquals(args[0], first)
                    assertEquals(args[1], second)
                    assertEquals(args[2], third)
                    .0;
                }
            }
        }
        k.eval("test(1, 2, 3)")
    }
}
