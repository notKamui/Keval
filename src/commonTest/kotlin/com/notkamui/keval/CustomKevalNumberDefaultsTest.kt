package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Verifies that a custom [KevalNumber] can supply [KevalDefaults.defaultResources]
 * using only the public [kevalResources] DSL (issue #70).
 */
class CustomKevalNumberDefaultsTest {

    private object LongNumber : KevalNumber<Long> {
        override fun isValidLiteral(token: String): Boolean = token.toLongOrNull() != null

        override fun parseLiteral(token: String): Long = token.toLong()

        override fun multiply(a: Long, b: Long): Long = a * b

        override fun defaultResources(): Map<String, KevalOperator<Long>> = kevalResources {
            binaryOperator {
                symbol = '+'
                precedence = 2
                isLeftAssociative = true
                implementation = { a, b -> a + b }
            }
            unaryOperator {
                symbol = '+'
                isPrefix = true
                implementation = { it }
            }
            binaryOperator {
                symbol = '-'
                precedence = 2
                isLeftAssociative = true
                implementation = { a, b -> a - b }
            }
            unaryOperator {
                symbol = '-'
                isPrefix = true
                implementation = { -it }
            }
            binaryOperator {
                symbol = '/'
                precedence = 3
                isLeftAssociative = true
                implementation = { a, b ->
                    if (b == 0L) throw KevalZeroDivisionException()
                    a / b
                }
            }
            function {
                name = "abs"
                arity = 1
                implementation = { args -> if (args[0] < 0) -args[0] else args[0] }
            }
            constant {
                name = "ten"
                value = 10L
            }
        }
    }

    @Test
    fun dslDefaultsAreIncludedViaIncludeDefault() {
        val result = Keval.create(LongNumber) {
            includeDefault()
        }.eval("abs(-3) + ten")
        assertEquals(13L, result)
    }

    @Test
    fun dslDefaultsSupportPrefixAndBinaryPlusMinus() {
        val result = Keval.create(LongNumber) {
            includeDefault()
        }.eval("-5 + +2")
        assertEquals(-3L, result)
    }

    @Test
    fun multiplyStillProvidesStarWithoutDefaultEntry() {
        val result = Keval.create(LongNumber) {
            includeDefault()
        }.eval("3 * 4")
        assertEquals(12L, result)
        assertTrue("*" !in LongNumber.defaultResources())
    }

    @Test
    fun emptyBuilderWithoutIncludeDefaultRejectsUnknownSymbols() {
        assertFailsWith<KevalInvalidSymbolException> {
            Keval.create(LongNumber) {}.eval("1 + 1")
        }
    }
}
