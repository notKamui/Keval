package com.notkamui.keval

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Main class for evaluating mathematical expressions.
 * It can be customized with additional operators, functions, and constants.
 */
class Keval internal constructor(private val resources: Map<String, KevalOperator>) {

    /**
     * Creates a new instance which contains a binary operator.
     *
     * @param symbol The symbol representing the operator.
     * @param precedence The precedence of the operator.
     * @param isLeftAssociative Whether the operator is left associative.
     * @param implementation The implementation of the operator.
     * @return This Keval instance.
     * @throws KevalDSLException If one of the fields isn't set properly.
     */
    fun withBinaryOperator(
        symbol: Char,
        precedence: Int,
        isLeftAssociative: Boolean,
        implementation: (Double, Double) -> Double
    ): Keval = KevalBuilder(resources)
        .binaryOperator {
            this.symbol = symbol
            this.precedence = precedence
            this.isLeftAssociative = isLeftAssociative
            this.implementation = implementation
        }
        .build()


    /**
     * Adds a unary operator to this Keval instance.
     *
     * @param symbol The symbol representing the operator.
     * @param isPrefix Whether the operator is prefix.
     * @param implementation The implementation of the operator.
     * @return This Keval instance.
     * @throws KevalDSLException If one of the fields isn't set properly.
     */
    fun withUnaryOperator(
        symbol: Char,
        isPrefix: Boolean,
        implementation: (Double) -> Double
    ): Keval = KevalBuilder(resources)
        .unaryOperator {
            this.symbol = symbol
            this.isPrefix = isPrefix
            this.implementation = implementation
        }
        .build()

    /**
     * Adds a function to this Keval instance.
     *
     * @param name The name of the function.
     * @param arity The number of arguments the function takes.
     * @param implementation The implementation of the function.
     * @return This Keval instance.
     * @throws KevalDSLException If one of the fields isn't set properly.
     */
    fun withFunction(
        name: String,
        arity: Int,
        implementation: (DoubleArray) -> Double
    ): Keval = KevalBuilder(resources)
        .function {
            this.name = name
            this.arity = arity
            this.implementation = implementation
        }
        .build()

    /**
     * Adds a constant to this Keval instance.
     *
     * @param name The name of the constant.
     * @param value The value of the constant.
     * @return This Keval instance.
     * @throws KevalDSLException If one of the fields isn't set properly.
     */
    fun withConstant(
        name: String,
        value: Double
    ): Keval = KevalBuilder(resources)
        .constant {
            this.name = name
            this.value = value
        }
        .build()

    /**
     * Adds the default resources to this Keval instance.
     *
     * @return This Keval instance.
     */
    fun withDefault(): Keval = KevalBuilder(resources).includeDefault().build()

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
        resources + ("*" to KevalBinaryOperator(3, true) { a, b -> a * b })

    companion object {

        /**
         * Creates a new instance of [Keval] with the provided resources.
         *
         * @param generator A lambda function that configures a KevalBuilder instance.
         * @return The new instance of Keval.
         * @throws KevalDSLException If one of the fields isn't set properly.
         */
        @JvmStatic
        fun create(generator: KevalBuilder.() -> Unit = { includeDefault() }): Keval =
            KevalBuilder().apply(generator).build()

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
): Double = KevalBuilder().apply(generator).build().eval(this)

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
