package com.notkamui.keval

import kotlin.math.pow

/**
 * Resource wrapper for KevalDSL
 */
class KevalDSL internal constructor() {
    private val _resources: MutableMap<String, KevalOperator> = mutableMapOf()
    internal val resources: Map<String, KevalOperator>
        get() = _resources.toMap()

    /**
     * Includes default operators and functions to Keval instance
     */
    fun includeDefault() {
        _resources += DEFAULT_OPERATORS
    }

    /**
     * Adds a new operator to Keval instance,
     * every field MUST be defined: symbol, precedence, isLeftAssociative, implementation
     *
     * @param definition is the definition of the above fields
     */
    fun operator(definition: BinaryOperatorDSL.() -> Unit) {
        val op = BinaryOperatorDSL()
        op.definition()

        // checking if every field has been properly defined
        op.symbol ?: throw KevalDSLException("symbol")
        if (op.symbol!![0] in '0'..'9')
            throw IllegalArgumentException("Symbols cannot start with a digit: ${op.symbol}")
        op.implementation ?: throw KevalDSLException("implementation")
        op.precedence ?: throw KevalDSLException("precedence")
        if (op.precedence!! < 0)
            throw IllegalArgumentException("Operator precedence must always be positive or 0")
        op.isLeftAssociative ?: throw KevalDSLException("isLeftAssociative")

        _resources += op.symbol!! to KevalBinaryOperator(op.precedence!!, op.isLeftAssociative!!, op.implementation!!)
    }

    /**
     * Adds a new function to Keval instance,
     * every field MUST be defined: name, arity, implementation
     *
     * @param definition is the definition of the above fields
     */
    fun function(definition: FunctionDSL.() -> Unit) {
        val fn = FunctionDSL()
        fn.definition()

        // checking if every field has been properly defined
        fn.name ?: throw KevalDSLException("name")
        fn.arity ?: throw KevalDSLException("arity")
        if (fn.arity!! < 0)
            throw IllegalArgumentException("Function arity must always be positive or 0")
        fn.implementation ?: throw KevalDSLException("implementation")

        _resources += fn.name!! to KevalFunction(fn.arity!!, fn.implementation!!)
    }

    companion object {
        internal val DEFAULT_OPERATORS: Map<String, KevalOperator> = mapOf(
            "+" to KevalBinaryOperator(2, true) { a, b -> a + b },
            "-" to KevalBinaryOperator(2, true) { a, b -> a - b },
            "/" to KevalBinaryOperator(3, true) { a, b ->
                if (b == 0.0) throw KevalZeroDivisionException()
                a / b
            },
            "%" to KevalBinaryOperator(3, true) { a, b ->
                if (b == 0.0) throw KevalZeroDivisionException()
                a % b
            },
            "^" to KevalBinaryOperator(4, false) { a, b -> a.pow(b) },
            "*" to KevalBinaryOperator(3, true) { a, b -> a * b },

            "neg" to KevalFunction(1) { args -> -args[0] }
        )

        /**
         * DSL representation of a binary operator
         *
         * @property symbol is the symbol which represents the operator
         * @property precedence is the precedence of the operator
         * @property isLeftAssociative is true when the operator is left associative, false otherwise
         * @property implementation is the actual implementation of the operator
         */
        data class BinaryOperatorDSL(
            var symbol: String? = null,
            var precedence: Int? = null,
            var isLeftAssociative: Boolean? = null,
            var implementation: ((Double, Double) -> Double)? = null
        )

        /**
         * DSL representation of a function
         *
         * @property name is the identifier which represents the function
         * @property arity is the arity of the function (how many arguments it takes)
         * @property implementation is the actual implementation of the function
         */
        data class FunctionDSL(
            var name: String? = null,
            var arity: Int? = null,
            var implementation: ((DoubleArray) -> Double)? = null,
        )
    }
}
