package com.notkamui.keval

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Wrapper class for [Keval],
 * Contains a companion object with the evaluation method
 */
class Keval
/**
 * Constructor for a [Keval] instance with [generator] being the DSL generator of Keval (defaults to the default resources)
 */
constructor(
    generator: KevalDSL.() -> Unit = { includeDefault() }
) {
    private val kevalDSL = KevalDSL()

    init {
        kevalDSL.generator()
    }

    /**
     * Composes a binary operator to this [Keval] instance with a [symbol], [precedence], if it [isLeftAssociative] and an [implementation].
     *
     * [KevalDSLException] is thrown in case one of the field isn't set properly.
     */
    fun withOperator(
        symbol: Char,
        precedence: Int,
        isLeftAssociative: Boolean,
        implementation: (Double, Double) -> Double
    ): Keval {
        kevalDSL.operator {
            this.symbol = symbol
            this.precedence = precedence
            this.isLeftAssociative = isLeftAssociative
            this.implementation = implementation
        }
        return this
    }

    /**
     * Composes a function to this [Keval] instance with a [name], [arity] and [implementation].
     *
     * [KevalDSLException] is thrown in case one of the field isn't set properly.
     */
    fun withFunction(
        name: String,
        arity: Int,
        implementation: (DoubleArray) -> Double
    ): Keval {
        kevalDSL.function {
            this.name = name
            this.arity = arity
            this.implementation = implementation
        }
        return this
    }

    /**
     * Composes a constant to this [Keval] instance with a [name] and a [value].
     *
     * [KevalDSLException] is thrown in case one of the field isn't set properly.
     */
    fun withConstant(
        name: String,
        value: Double
    ): Keval {
        kevalDSL.constant {
            this.name = name
            this.value = value
        }
        return this
    }

    /**
     * Composes the default resources to this [Keval] instance.
     */
    fun withDefault(): Keval {
        kevalDSL.includeDefault()
        return this
    }

    /**
     * Evaluates [mathExpression] from a [String] and returns a [Double] value using the resources of this [Keval] instance.
     *
     * May throw several exceptions:
     * - [KevalInvalidSymbolException] in case there's an invalid operator in the expression
     * - [KevalInvalidExpressionException] in case the expression is invalid (i.e. mismatched parenthesis)
     * - [KevalZeroDivisionException] in case of a zero division
     */
    fun eval(
        mathExpression: String,
    ): Double {

        val operators = resourcesView()
        return mathExpression.toAbstractSyntaxTree(operators).eval()
    }

    // The tokenizer assumes multiplication, hence disallowing overriding `*` operator
    fun resourcesView(): Map<String, KevalOperator> = kevalDSL.resources
        .plus("*" to KevalBinaryOperator(3, true) { a, b -> a * b })

    companion object {
        /**
         * Evaluates [mathExpression] from a [String] and returns a [Double] value with the default resources.
         *
         * May throw several exceptions:
         * - [KevalInvalidSymbolException] in case there's an invalid operator in the expression
         * - [KevalInvalidExpressionException] in case the expression is invalid (i.e. mismatched parenthesis)
         * - [KevalZeroDivisionException] in case of a zero division
         */
        @JvmName("evaluate")
        @JvmStatic
        fun eval(
            mathExpression: String,
        ): Double {
            return mathExpression.toAbstractSyntaxTree(KevalDSL.DEFAULT_RESOURCES).eval()
        }
    }
}

/**
 * Evaluates [this] mathematical expression from a [String] and returns a [Double] value with given resources as [generator].
 *
 * May throw several exceptions:
 * - [KevalInvalidSymbolException] in case there's an invalid operator in the expression
 * - [KevalInvalidExpressionException] in case the expression is invalid (i.e. mismatched parenthesis)
 * - [KevalZeroDivisionException] in case of a zero division
 * - [KevalDSLException] in case one of the field isn't set properly
 */
fun String.keval(
    generator: KevalDSL.() -> Unit
): Double {
    return Keval(generator).eval(this)
}

/**
 * Evaluates [this] mathematical expression from a [String] and returns a [Double] value with the default resources.
 *
 * May throw several exceptions:
 * - [KevalInvalidSymbolException] in case there's an invalid operator in the expression
 * - [KevalInvalidExpressionException] in case the expression is invalid (i.e. mismatched parenthesis)
 * - [KevalZeroDivisionException] in case of a zero division
 */
fun String.keval(): Double {
    return Keval.eval(this)
}
