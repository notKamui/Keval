package com.notkamui.keval

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun hypotenuse(x: Double, y: Double): Double = sqrt(x*x + y*y)

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
        val k = Keval {
            includeDefault()
            function {
                name = "first"
                arity = 3
                implementation = { args ->
                    args[0]
                }
            }
            function {
                name = "second"
                arity = 3
                implementation = { args ->
                    args[1]
                }
            }
            function {
                name = "third"
                arity = 3
                implementation = { args ->
                    args[2]
                }
            }
        }
        assertEquals(1.0, k.eval("first(1, 2, 3)"), "first(1, 2, 3)")
        assertEquals(2.0, k.eval("second(1, 2, 3)"), "second(1, 2, 3)")
        assertEquals(3.0, k.eval("third(1, 2, 3)"), "third(1, 2, 3)")
    }

    @Test
    fun checkCoherence() {
        val k = Keval {
            includeDefault()
            function {
                name = "if"
                arity = 3
                implementation = { args -> if (args[0] != .0) args[1] else args[2] }
            }
        }

        assertEquals(4.0, k.eval("if((1*1), 4, 5)"), "if((1*1), 4, 5)")
        assertEquals(4.0, k.eval("if(1*1, 4, 5)"), "if(1*1, 4, 5)")
    }

    @Test
    fun checkRepeatingParentheses() {
        val k = Keval {
            includeDefault()
            function {
                name = "f"
                arity = 1
                implementation = { args -> args[0] }
            }
        }
        assertEquals(1.0, k.eval("f(((1)))"), "f(((1)))")
    }
}
