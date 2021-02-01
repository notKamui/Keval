package com.notkamui.keval

import kotlin.math.pow

class Resources internal constructor() {
    private val _operators: MutableMap<String, KevalOperator> = mutableMapOf()
    val operators: Map<String, KevalOperator>
        get() = _operators.toMap()

    val defaultOperators: Map<String, KevalOperator> = DEFAULT_OPERATORS

    operator fun Map<String, KevalBinaryOperator>.unaryPlus() {
        _operators += this
    }

    operator fun Pair<String, KevalBinaryOperator>.unaryPlus() {
        _operators += this
    }

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

        _operators += op.symbol!! to KevalBinaryOperator(op.precedence!!, op.isLeftAssociative!!, op.implementation!!)
    }

    fun function(definition: FunctionDSL.() -> Unit) {
        val fn = FunctionDSL()
        fn.definition()

        // checking if every field has been properly defined
        fn.name ?: throw KevalDSLException("name")
        fn.arity ?: throw KevalDSLException("arity")
        if (fn.arity!! < 0)
            throw IllegalArgumentException("Function arity must always be positive or 0")
        fn.implementation ?: throw KevalDSLException("implementation")

        _operators += fn.name!! to KevalFunction(fn.arity!!, fn.implementation!!)
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

        data class BinaryOperatorDSL(
            var symbol: String? = null,
            var precedence: Int? = null,
            var isLeftAssociative: Boolean? = null,
            var implementation: ((Double, Double) -> Double)? = null
        )

        data class FunctionDSL(
            var name: String? = null,
            var arity: Int? = null,
            var implementation: ((Array<Double>) -> Double)? = null,
        )
    }
}
