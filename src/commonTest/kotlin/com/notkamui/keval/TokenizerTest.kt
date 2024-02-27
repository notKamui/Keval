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
        val operators = KevalDSL.DEFAULT_RESOURCES
        val tokens = "((34+8)/3)+3.3*(5+2)%2^6".tokenize(operators)
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
                "6"
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
        val k = Keval {
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
