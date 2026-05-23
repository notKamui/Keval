package com.notkamui.keval

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Main class for evaluating mathematical expressions.
 * It can be customized with additional operators, functions, and constants.
 */
class Keval<N> internal constructor(
    private val number: KevalNumber<N>,
    private val resources: Map<String, KevalOperator<N>>
) {

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
        implementation: (N, N) -> N
    ): Keval<N> = KevalBuilder(number, resources)
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
        implementation: (N) -> N
    ): Keval<N> = KevalBuilder(number, resources)
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
     * @param arity The number of arguments the function takes. `null` if the function should be variadic.
     * @param implementation The implementation of the function.
     * @return This Keval instance.
     * @throws KevalDSLException If one of the fields isn't set properly.
     */
    fun withFunction(
        name: String,
        arity: Int? = null,
        implementation: (List<N>) -> N
    ): Keval<N> = KevalBuilder(number, resources)
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
        value: N
    ): Keval<N> = KevalBuilder(number, resources)
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
    fun withDefault(): Keval<N> = KevalBuilder(number, resources).includeDefault().build()

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
    ): N {
        val operators = resourcesView()
        return mathExpression.toAST(number, operators).eval()
    }

    /**
     * Returns the resources of this [Keval] instance.
     * The tokenizer assumes multiplication, hence disallowing overriding `*` operator
     */
    fun resourcesView(): Map<String, KevalOperator<N>> =
        resources + ("*" to requireNotNull(number.defaultResources()["*"]) {
            "Number type must define a default * operator"
        })

    companion object {

        /**
         * Creates a new instance of [Keval] with the provided resources.
         *
         * @param number The numeric type context for parsing and default resources.
         * @param generator A lambda function that configures a KevalBuilder instance.
         * @return The new instance of Keval.
         * @throws KevalDSLException If one of the fields isn't set properly.
         */
        @JvmStatic
        fun <N> create(
            number: KevalNumber<N>,
            generator: KevalBuilder<N>.() -> Unit = { includeDefault() }
        ): Keval<N> =
            KevalBuilder(number).apply(generator).build()

        /**
         * Evaluates a mathematical expression using the default Double resources.
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
        ): Double = create(KevalNumbers.Double) { includeDefault() }.eval(mathExpression)
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
    generator: KevalBuilder<Double>.() -> Unit
): Double = Keval.create(KevalNumbers.Double, generator).eval(this)

/**
 * Evaluates a mathematical expression using the default resources.
 *
 * @receiver The mathematical expression to evaluate.
 * @return The result of the evaluation.
 * @throws KevalInvalidSymbolException If there's an invalid operator in the expression.
 * @throws KevalInvalidExpressionException If the expression is invalid (i.e., mismatched parentheses).
 * @throws KevalZeroDivisionException If a division by zero occurs.
 */
fun String.keval(): Double = KevalNumbers.Double.eval(this)
