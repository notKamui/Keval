package com.notkamui.keval

/**
 * Wrapper class for Keval,
 * Contains a companion object with the evaluation method
 *
 * @param generator is the DSL generator of Keval (defaults to the default resources)
 */
class Keval
@Throws(KevalDSLException::class)
constructor(
    generator: KevalDSL.() -> Unit = { includeDefault() }
) {
    private val kevalDSL = KevalDSL()

    init {
        kevalDSL.generator()
    }

    /**
     * Composes a binary operator to Keval
     *
     * @param symbol is the symbol which represents the operator
     * @param precedence is the precedence of the operator
     * @param isLeftAssociative is true when the operator is left associative, false otherwise
     * @param implementation is the actual implementation of the operator
     * @return the Keval instance
     * @throws KevalDSLException if at least one of the field isn't set properly
     */
    @Throws(KevalDSLException::class)
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
     * Composes a function to Keval
     *
     * @param name is the identifier which represents the function
     * @param arity is the arity of the function (how many arguments it takes)
     * @param implementation is the actual implementation of the function
     * @return the Keval instance
     * @throws KevalDSLException if at least one of the field isn't set properly
     */
    @Throws(KevalDSLException::class)
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
     * Composes a constant to Keval
     *
     * @param name is the identifier which represents the constant
     * @param value is the value of the constant
     * @return the Keval instance
     * @throws KevalDSLException if at least one of the field isn't set properly
     */
    @Throws(KevalDSLException::class)
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
     * Composes the default resources to Keval
     *
     * @return the Keval instance
     */
    fun withDefault(): Keval {
        kevalDSL.includeDefault()
        return this
    }

    /**
     * Evaluates a mathematical expression to a double value with given resources
     *
     * @param mathExpression is the expression to evaluate
     * @return the value of the expression
     * @throws KevalInvalidSymbolException in case there's an invalid operator in the expression
     * @throws KevalInvalidExpressionException in case the expression is invalid (i.e. mismatched parenthesis)
     * @throws KevalZeroDivisionException in case of a zero division
     */
    @Throws(
        KevalInvalidSymbolException::class,
        KevalInvalidSymbolException::class,
        KevalZeroDivisionException::class
    )
    fun eval(
        mathExpression: String,
    ): Double {
        // The tokenizer assumes multiplication, hence disallowing overriding `*` operator
        val operators = kevalDSL.resources
            .plus("*" to KevalBinaryOperator(3, true) { a, b -> a * b })

        return mathExpression.toAbstractSyntaxTree(operators).eval()
    }

    companion object {
        /**
         * Evaluates a mathematical expression to a double value
         *
         * @param mathExpression is the expression to evaluate
         * @return the value of the expression
         * @throws KevalInvalidSymbolException in case there's an invalid operator in the expression
         * @throws KevalInvalidExpressionException in case the expression is invalid (i.e. mismatched parenthesis)
         * @throws KevalZeroDivisionException in case of a zero division
         */
        @Throws(
            KevalInvalidSymbolException::class,
            KevalInvalidSymbolException::class,
            KevalZeroDivisionException::class
        )
        fun eval(
            mathExpression: String,
        ): Double {
            return mathExpression.toAbstractSyntaxTree(KevalDSL.DEFAULT_RESOURCES).eval()
        }
    }
}

/**
 * Evaluates a mathematical expression to a double value with given resources
 *
 * @receiver is the expression to evaluate
 * @param generator is the DSL generator of Keval
 * @return the value of the expression
 * @throws KevalInvalidSymbolException in case there's an invalid operator in the expression
 * @throws KevalInvalidExpressionException in case the expression is invalid (i.e. mismatched parenthesis)
 * @throws KevalZeroDivisionException in case of a zero division
 * @throws KevalDSLException if at least one of the field isn't set properly
 */
@Throws(
    KevalInvalidSymbolException::class,
    KevalInvalidSymbolException::class,
    KevalZeroDivisionException::class,
    KevalDSLException::class
)
fun String.keval(
    generator: KevalDSL.() -> Unit
): Double {
    return Keval(generator).eval(this)
}

/**
 * Evaluates a mathematical expression to a double value with the default resources
 *
 * @receiver is the expression to evaluate
 * @return the value of the expression
 * @throws KevalInvalidSymbolException in case there's an invalid operator in the expression
 * @throws KevalInvalidExpressionException in case the expression is invalid (i.e. mismatched parenthesis)
 * @throws KevalZeroDivisionException in case of a zero division
 */
@Throws(
    KevalInvalidSymbolException::class,
    KevalInvalidSymbolException::class,
    KevalZeroDivisionException::class
)
fun String.keval(): Double {
    return Keval.eval(this)
}
