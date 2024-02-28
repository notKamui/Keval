package com.notkamui.keval

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Main class for evaluating mathematical expressions.
 * It can be customized with additional operators, functions, and constants.
 *
 * @param generator A lambda function that configures this Keval instance using the KevalBuilder DSL.
 */
class Keval(generator: KevalBuilder.() -> Unit = { includeDefault() }) {
    private val kevalBuilder = KevalBuilder()

    init {
        kevalBuilder.generator()
    }

    /**
     * Adds a binary operator to this Keval instance.
     *
     * @param symbol The symbol representing the operator.
     * @param precedence The precedence of the operator.
     * @param isLeftAssociative Whether the operator is left associative.
     * @param implementation The implementation of the operator.
     * @return This Keval instance.
     */
    fun withOperator(
        symbol: Char,
        precedence: Int,
        isLeftAssociative: Boolean,
        implementation: (Double, Double) -> Double
    ): Keval = apply {
        kevalBuilder.operator {
            this.symbol = symbol
            this.precedence = precedence
            this.isLeftAssociative = isLeftAssociative
            this.implementation = implementation
        }
    }

    /**
     * Adds a unary operator to this Keval instance.
     *
     * @param symbol The symbol representing the operator.
     * @param isPrefix Whether the operator is prefix.
     * @param implementation The implementation of the operator.
     * @return This Keval instance.
     */
    fun withUnaryOperator(
        symbol: Char,
        isPrefix: Boolean,
        implementation: (Double) -> Double
    ): Keval = apply {
        kevalBuilder.unaryOperator {
            this.symbol = symbol
            this.isPrefix = isPrefix
            this.implementation = implementation
        }
    }

    /**
     * Adds a function to this Keval instance.
     *
     * @param name The name of the function.
     * @param arity The number of arguments the function takes.
     * @param implementation The implementation of the function.
     * @return This Keval instance.
     */
    fun withFunction(
        name: String,
        arity: Int,
        implementation: (DoubleArray) -> Double
    ): Keval = apply {
        kevalBuilder.function {
            this.name = name
            this.arity = arity
            this.implementation = implementation
        }
    }

    /**
     * Adds a constant to this Keval instance.
     *
     * @param name The name of the constant.
     * @param value The value of the constant.
     * @return This Keval instance.
     */
    fun withConstant(
        name: String,
        value: Double
    ): Keval = apply {
        kevalBuilder.constant {
            this.name = name
            this.value = value
        }
    }

    /**
     * Adds the default resources to this Keval instance.
     *
     * @return This Keval instance.
     */
    fun withDefault(): Keval = apply {
        kevalBuilder.includeDefault()
    }

    /**
     * Evaluates a mathematical expression.
     *
     * @param mathExpression The mathematical expression to evaluate.
     * @return The result of the evaluation.
     * @throws KevalInvalidSymbolException If there's an invalid operator in the expression.
     * @throws KevalInvalidExpressionException If the expression is invalid (i.e., mismatched parentheses).
     * @throws KevalZeroDivisionException If a division by zero occurs.
     */
    fun eval(
        mathExpression: String,
    ): Double {
        val operators = resourcesView()
        return mathExpression.toAST(operators).eval()
    }

    /**
     * Returns the resources of this [Keval] instance.
     * The tokenizer assumes multiplication, hence disallowing overriding `*` operator
     */
    fun resourcesView(): Map<String, KevalOperator> =
        kevalBuilder.resources + ("*" to KevalBinaryOperator(3, true) { a, b -> a * b })

    companion object {

        /**
         * Evaluates a mathematical expression using the default resources.
         *
         * @param mathExpression The mathematical expression to evaluate.
         * @return The result of the evaluation.
         * @throws KevalInvalidSymbolException If there's an invalid operator in the expression.
         * @throws KevalInvalidExpressionException If the expression is invalid (i.e., mismatched parentheses).
         * @throws KevalZeroDivisionException If a division by zero occurs.
         */
        @JvmName("evaluate")
        @JvmStatic
        fun eval(
            mathExpression: String,
        ): Double = mathExpression.toAST(KevalBuilder.DEFAULT_RESOURCES).eval()
    }
}

/**
 * Evaluates a mathematical expression using the provided resources.
 *
 * @receiver The mathematical expression to evaluate.
 * @param generator A lambda function that configures a KevalBuilder instance.
 * @return The result of the evaluation.
 * @throws KevalInvalidSymbolException If there's an invalid operator in the expression.
 * @throws KevalInvalidExpressionException If the expression is invalid (i.e., mismatched parentheses).
 * @throws KevalZeroDivisionException If a division by zero occurs.
 * @throws KevalDSLException If one of the fields isn't set properly.
 */
fun String.keval(
    generator: KevalBuilder.() -> Unit
): Double = Keval(generator).eval(this)

/**
 * Evaluates a mathematical expression using the default resources.
 *
 * @receiver The mathematical expression to evaluate.
 * @return The result of the evaluation.
 * @throws KevalInvalidSymbolException If there's an invalid operator in the expression.
 * @throws KevalInvalidExpressionException If the expression is invalid (i.e., mismatched parentheses).
 * @throws KevalZeroDivisionException If a division by zero occurs.
 */
fun String.keval(): Double = Keval.eval(this)
