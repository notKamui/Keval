package com.notkamui.keval

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KevalBigDecimalBuilderTest {

    @Test
    fun kevalNumbersBigDecimalEntryPoint() {
        assertEquals(KevalNumberBigDecimal, KevalNumbers.BigDecimal)
    }

    @Test
    fun kevalNumberEvalExtension() {
        assertDecimalEquals("4", KevalNumbers.BigDecimal.eval("2 + 2"))
    }

    @Test
    fun createWithoutDefaultsRequiresCustomOps() {
        assertFailsWith<KevalInvalidExpressionException> {
            Keval.create(KevalNumbers.BigDecimal) {}.eval("1 + 1")
        }
    }

    @Test
    fun customBinaryOperator() {
        val kvl = Keval.create(KevalNumbers.BigDecimal) {
            includeDefault()
            binaryOperator {
                symbol = ';'
                precedence = 3
                isLeftAssociative = true
                implementation = { a, b -> a.multiply(a).add(b.multiply(b)) }
            }
        }
        assertDecimalEquals("25", kvl.eval("3;4"))
    }

    @Test
    fun customUnaryOperator() {
        val kvl = Keval.create(KevalNumbers.BigDecimal) {
            includeDefault()
            unaryOperator {
                symbol = '&'
                isPrefix = true
                implementation = { it.negate() }
            }
        }
        assertEquals(BigDecimal("-5"), kvl.eval("&5"))
    }

    @Test
    fun customFunction() {
        val kvl = Keval.create(KevalNumbers.BigDecimal) {
            includeDefault()
            function {
                name = "double"
                arity = 1
                implementation = { it[0].multiply(BigDecimal.TWO) }
            }
        }
        assertEquals(BigDecimal("42"), kvl.eval("double(21)"))
    }

    @Test
    fun customConstant() {
        val kvl = Keval.create(KevalNumbers.BigDecimal) {
            includeDefault()
            constant {
                name = "TEN"
                value = BigDecimal.TEN
            }
        }
        assertDecimalEquals("15", kvl.eval("TEN + 5"))
    }

    @Test
    fun withMethodsChain() {
        val kvl = Keval.create(KevalNumbers.BigDecimal) { includeDefault() }
            .withConstant("PHI", BigDecimal("1.618"))
            .withFunction("twice", 1) { it[0].multiply(BigDecimal.TWO) }

        assertDecimalEquals("3.236", kvl.eval("twice(PHI)"))
    }

    @Test
    fun kevalBigDecimalWithGenerator() {
        assertDecimalEquals("99", "x + 1".kevalBigDecimal {
            includeDefault()
            constant {
                name = "x"
                value = BigDecimal("98")
            }
        })
    }

    @Test
    fun excludedDoubleOnlyFunctionsNotInDefaults() {
        val defaults = KevalNumberBigDecimal.defaultResources()
        listOf("sin", "cos", "tan", "sqrt", "ln", "exp", "rand", "!", "PI", "e", "median", "percentile")
            .forEach { name ->
                assertFalse(name in defaults, "$name should not be in BigDecimal defaults")
            }
    }

    @Test
    fun includedFunctionsAreInDefaults() {
        val defaults = KevalNumberBigDecimal.defaultResources()
        listOf(
            "+", "-", "*", "/", "%", "^",
            "neg", "abs", "sign", "min", "max", "sum", "avg",
            "ceil", "floor", "round", "trunc",
            "bool", "not", "and", "or", "eq", "ne", "gt", "lt", "ge", "le",
            "nand", "nor", "xor", "xnor", "imply", "nimply",
        ).forEach { name ->
            assertTrue(name in defaults, "$name should be in BigDecimal defaults")
        }
    }

    @Test
    fun resourcesViewAlwaysIncludesMultiply() {
        val kvl = Keval.create(KevalNumbers.BigDecimal) {
            binaryOperator {
                symbol = '+'
                precedence = 1
                isLeftAssociative = true
                implementation = { a, b -> a.add(b) }
            }
        }
        assertTrue("*" in kvl.resourcesView())
        assertEquals(BigDecimal("6"), kvl.eval("2*3"))
    }
}
