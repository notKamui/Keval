package com.notkamui.keval

import kotlin.math.*

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
        _resources += DEFAULT_RESOURCES
    }

    /**
     * Adds a new operator to Keval instance,
     * every field MUST be defined: symbol, precedence, isLeftAssociative, implementation
     *
     * @param definition is the definition of the above fields
     * @throws KevalDSLException if at least one of the field isn't set properly
     */
    fun operator(definition: BinaryOperatorDSL.() -> Unit) {
        val op = BinaryOperatorDSL()
        op.definition()

        // checking if every field has been properly defined
        val symbol = op.symbol ?: throw KevalDSLException("symbol is not set")
        if (symbol.isLetterOrDigit() || symbol in listOf('_', '(', ')', ',')) {
            throw KevalDSLException("a symbol must NOT be a letter, a digit, an underscore, parentheses nor a comma but was: $symbol")
        }
        if (symbol == '*') {
            throw KevalDSLException("* cannot be overwritten")
        }
        val implementation = op.implementation ?: throw KevalDSLException("implementation is not set")
        val precedence = op.precedence ?: throw KevalDSLException("precedence is not set")
        if (precedence < 0) {
            throw KevalDSLException("operator's precedence must always be positive or 0")
        }
        val isLeftAssociative = op.isLeftAssociative ?: throw KevalDSLException("isLeftAssociative is not set")

        _resources[symbol.toString()] = KevalBinaryOperator(
            precedence,
            isLeftAssociative,
            implementation
        )
    }

    /**
     * Adds a new function to Keval instance,
     * every field MUST be defined: name, arity, implementation
     *
     * @param definition is the definition of the above fields
     * @throws KevalDSLException if at least one of the field isn't set
     */
    fun function(definition: FunctionDSL.() -> Unit) {
        val fn = FunctionDSL()
        fn.definition()

        // checking if every field has been properly defined
        val name = fn.name ?: throw KevalDSLException("name is not set")
        if (name.isEmpty() ||
            name[0] in '0'..'9' ||
            name.contains("[^a-zA-Z0-9_]".toRegex())
        ) {
            throw KevalDSLException("a function's name cannot start with a digit and must contain only letters, digits or underscores: ${fn.name}")
        }
        val arity = fn.arity ?: throw KevalDSLException("arity is not set")
        if (arity < 0) {
            throw KevalDSLException("function's arity must always be positive or 0")
        }
        val implementation = fn.implementation ?: throw KevalDSLException("implementation is not set")

        _resources[name] = KevalFunction(arity, implementation)
    }

    /**
     * Adds a new function to Keval instance,
     * every field MUST be defined: name, value
     *
     * @param definition is the definition of the above fields
     * @throws KevalDSLException if at least one of the field isn't set
     */
    fun constant(definition: ConstantDSL.() -> Unit) {
        val const = ConstantDSL()
        const.definition()

        // checking if every field has been properly defined
        val name = const.name ?: throw KevalDSLException("name is not set")
        if (name.isEmpty() ||
            name[0] in '0'..'9' ||
            name.contains("[^a-zA-Z0-9_]".toRegex())
        ) {
            throw KevalDSLException("a constant's name cannot start with a digit and must contain only letters, digits or underscores: ${const.name}")
        }
        val value = const.value ?: throw KevalDSLException("value is not set")

        _resources[name] = KevalConstant(value)
    }

    companion object {
        internal val DEFAULT_RESOURCES: Map<String, KevalOperator> = mapOf(
            // binary operators
            "+" to KevalBothOperator(
                KevalBinaryOperator(2, true) { a, b -> a + b },
                KevalUnaryOperator(true) { it }
            ),
            "-" to KevalBothOperator(
                KevalBinaryOperator(2, true) { a, b -> a - b },
                KevalUnaryOperator(true) { -it }
            ),

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

            // unary operators
            "!" to KevalUnaryOperator(false) {
                if (it < 0) throw KevalInvalidFactorialException("factorial of a negative number")
                if (floor(it) != it) throw KevalInvalidFactorialException("factorial of a non-integer")
                var result = 1.0
                for (i in 2..it.toInt()) {
                    result *= i
                }
                result
            },

            // functions
            "neg" to KevalFunction(1) { -it[0] },
            "abs" to KevalFunction(1) { it[0].absoluteValue },
            "sqrt" to KevalFunction(1) { sqrt(it[0]) },
            "cbrt" to KevalFunction(1) { cbrt(it[0]) },
            "exp" to KevalFunction(1) { exp(it[0]) },
            "ln" to KevalFunction(1) { ln(it[0]) },
            "log10" to KevalFunction(1) { log10(it[0]) },
            "log2" to KevalFunction(1) { log2(it[0]) },
            "sin" to KevalFunction(1) { sin(it[0]) },
            "cos" to KevalFunction(1) { cos(it[0]) },
            "tan" to KevalFunction(1) { tan(it[0]) },
            "asin" to KevalFunction(1) { asin(it[0]) },
            "acos" to KevalFunction(1) { acos(it[0]) },
            "atan" to KevalFunction(1) { atan(it[0]) },
            "ceil" to KevalFunction(1) { ceil(it[0]) },
            "floor" to KevalFunction(1) { floor(it[0]) },
            "round" to KevalFunction(1) { round(it[0]) },

            // constants
            "PI" to KevalConstant(PI),
            "e" to KevalConstant(E)
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
            var symbol: Char? = null,
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

        /**
         * DSL representation of a constant
         *
         * @property name is the identifier which represents the constant
         * @property value is the value of the constant
         */
        data class ConstantDSL(
            var name: String? = null,
            var value: Double? = null
        )
    }
}
