package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests on the Tokenizer
 */
class TokenizerTest {

    /**
     * Tests for String.tokenize()
     */
    @Test
    fun parseString() {
        val operators = KevalBuilder.DEFAULT_RESOURCES.plus(
            listOf(
                "A_1a2b3c" to KevalConstant(1.2),
                "A_a1b2c3" to KevalConstant(2.3),
                "A1_1A_A1_AB_AB_12" to KevalConstant(3.4),
                "A__B" to KevalConstant(4.5),
                "A__" to KevalConstant(5.6),
                "_A" to KevalConstant(6.7),
                "__A" to KevalConstant(7.8),
            )
        )
        val tokens = "((34+8)/3)+3.3*(5+2)%2^6+A_1a2b3c^4.2+A_a1b2c3/4.2-A1_1A_A1_AB_AB_12%4.2+A__B-A__+_A-__A".tokenize(operators)
        assertEquals(
            listOf(
                "(",
                "(",
                "34",
                "+",
                "8",
                ")",
                "/",
                "3",
                ")",
                "+",
                "3.3",
                "*",
                "(",
                "5",
                "+",
                "2",
                ")",
                "%",
                "2",
                "^",
                "6",
                "+",
                "A_1a2b3c",
                "^",
                "4.2",
                "+",
                "A_a1b2c3",
                "/",
                "4.2",
                "-",
                "A1_1A_A1_AB_AB_12",
                "%",
                "4.2",
                "+",
                "A__B",
                "-",
                "A__",
                "+",
                "_A",
                "-",
                "__A",
            ),
            tokens
        )

        val tokens2 = "(3+4 ) (2-5) ".tokenize(operators) // check auto mul
        assertEquals(
            listOf("(", "3", "+", "4", ")", "*", "(", "2", "-", "5", ")"),
            tokens2
        )

        assertTrue {
            try {
                "(37+4)a+5".tokenize(operators)
                false
            } catch (e: KevalInvalidSymbolException) {
                e.invalidSymbol == "a" && e.position == 6 && e.expression == "(37+4)a+5"
            }
        }
    }

    @Test
    fun checkRepeatingParentheses() {
        val k = Keval.create {
            includeDefault()
            function {
                name = "f"
                arity = 1
                implementation = { args -> args[0] }
            }
        }

        val nodes = "f(((1)))".tokenize(k.resourcesView())
        assertEquals("f(((1)))", nodes.joinToString(separator = ""))
    }
}
