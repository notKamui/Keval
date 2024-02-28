package com.notkamui.keval

import kotlin.math.*

class KevalBuilder internal constructor() {
    private val _resources: MutableMap<String, KevalOperator> = mutableMapOf()
    internal val resources: Map<String, KevalOperator>
        get() = _resources.toMap()

    fun includeDefault() {
        _resources += DEFAULT_RESOURCES
    }

    fun operator(definition: BinaryOperatorBuilder.() -> Unit) {
        val op = BinaryOperatorBuilder().apply(definition)
        validateOperator(op.symbol, op.precedence, op.implementation)
        addOperator(op.symbol!!, KevalBinaryOperator(op.precedence!!, op.isLeftAssociative!!, op.implementation!!), isUnary = false)
    }

    fun unaryOperator(definition: UnaryOperatorBuilder.() -> Unit) {
        val op = UnaryOperatorBuilder().apply(definition)
        validateUnaryOperator(op.symbol, op.isPrefix, op.implementation)
        addOperator(op.symbol!!, KevalUnaryOperator(op.isPrefix!!, op.implementation!!), isUnary = true)
    }

    fun function(definition: FunctionBuilder.() -> Unit) {
        val fn = FunctionBuilder().apply(definition)
        validateFunction(fn.name, fn.arity, fn.implementation)
        _resources[fn.name!!] = KevalFunction(fn.arity!!, fn.implementation!!)
    }

    fun constant(definition: ConstantBuilder.() -> Unit) {
        val const = ConstantBuilder().apply(definition)
        validateConstant(const.name, const.value)
        _resources[const.name!!] = KevalConstant(const.value!!)
    }

    private fun validateOperator(symbol: Char?, precedence: Int?, implementation: ((Double, Double) -> Double)?) {
        requireNotNull(symbol) { "symbol is not set" }
        requireNotNull(implementation) { "implementation is not set" }
        requireNotNull(precedence) { "precedence is not set" }
        require(precedence >= 0) { "operator's precedence must always be positive or 0" }
        require(symbol.isOperatorSymbol()) { "a symbol must NOT be a letter, a digit, an underscore, parentheses nor a comma but was: $symbol" }
        require(symbol != '*') { "* cannot be overwritten" }
    }

    private fun validateUnaryOperator(symbol: Char?, isPrefix: Boolean?, implementation: ((Double) -> Double)?) {
        requireNotNull(symbol) { "symbol is not set" }
        requireNotNull(isPrefix) { "isPrefix is not set" }
        requireNotNull(implementation) { "implementation is not set" }
        require(symbol.isOperatorSymbol()) { "a symbol must NOT be a letter, a digit, an underscore, parentheses nor a comma but was: $symbol" }
    }

    private fun validateFunction(name: String?, arity: Int?, implementation: Any?) {
        requireNotNull(name) { "name is not set" }
        requireNotNull(implementation) { "implementation is not set" }
        requireNotNull(arity) { "arity is not set" }
        require(arity >= 0) { "function's arity must always be positive or 0" }
        require(name.isFunctionOrConstantName()) { "a function's name cannot start with a digit and must contain only letters, digits or underscores: $name" }
    }

    private fun validateConstant(name: String?, value: Double?) {
        requireNotNull(name) { "name is not set" }
        requireNotNull(value) { "value is not set" }
        require(name.isFunctionOrConstantName()) { "a constant's name cannot start with a digit and must contain only letters, digits or underscores: $name" }
    }

    private fun Char.isOperatorSymbol() = !isLetterOrDigit() && this !in listOf('_', '(', ')', ',')
    private fun String.isFunctionOrConstantName() =
        isNotEmpty() && this[0] !in '0'..'9' && !contains("[^a-zA-Z0-9_]".toRegex())

    private fun addOperator(symbol: Char, operator: KevalOperator, isUnary: Boolean) {
        when (val resource = _resources[symbol.toString()]) {
            is KevalUnaryOperator -> _resources[symbol.toString()] =
                KevalBothOperator(operator as KevalBinaryOperator, resource)

            is KevalBinaryOperator -> _resources[symbol.toString()] =
                KevalBothOperator(resource, operator as KevalUnaryOperator)

            is KevalBothOperator -> _resources[symbol.toString()] =
                if (isUnary) KevalBothOperator(operator as KevalBinaryOperator, resource.unary)
                else KevalBothOperator(resource.binary, operator as KevalUnaryOperator)

            else -> _resources[symbol.toString()] = operator
        }
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
         * Builder representation of a binary operator
         *
         * @property symbol is the symbol which represents the operator
         * @property precedence is the precedence of the operator
         * @property isLeftAssociative is true when the operator is left associative, false otherwise
         * @property implementation is the actual implementation of the operator
         */
        data class BinaryOperatorBuilder(
            var symbol: Char? = null,
            var precedence: Int? = null,
            var isLeftAssociative: Boolean? = null,
            var implementation: ((Double, Double) -> Double)? = null
        )

        /**
         * Builder representation of a unary operator
         *
         * @property symbol is the symbol which represents the operator
         * @property isPrefix is true when the operator is prefix, false otherwise
         * @property implementation is the actual implementation of the operator
         */
        data class UnaryOperatorBuilder(
            var symbol: Char? = null,
            var isPrefix: Boolean? = null,
            var implementation: ((Double) -> Double)? = null
        )

        /**
         * Builder representation of a function
         *
         * @property name is the identifier which represents the function
         * @property arity is the arity of the function (how many arguments it takes)
         * @property implementation is the actual implementation of the function
         */
        data class FunctionBuilder(
            var name: String? = null,
            var arity: Int? = null,
            var implementation: ((DoubleArray) -> Double)? = null,
        )

        /**
         * Builder representation of a constant
         *
         * @property name is the identifier which represents the constant
         * @property value is the value of the constant
         */
        data class ConstantBuilder(
            var name: String? = null,
            var value: Double? = null
        )
    }
}
