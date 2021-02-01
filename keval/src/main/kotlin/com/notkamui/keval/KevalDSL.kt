package com.notkamui.keval

import kotlin.math.pow

class Resources internal constructor() {
    private val _operators: MutableMap<String, KevalBinaryOperator> = mutableMapOf()
    val operators: Map<String, KevalBinaryOperator>
        get() = _operators.toMap()

    val defaultOperators: Map<String, KevalBinaryOperator> = DEFAULT_OPERATORS

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
        op.isLeftAssociative ?: throw KevalDSLException("isLeftAssociative")

        _operators += op.symbol!! to KevalBinaryOperator(op.implementation!!, op.precedence!!, op.isLeftAssociative!!)
    }

    companion object {
        internal val DEFAULT_OPERATORS: Map<String, KevalBinaryOperator> = mapOf(
            "+" to KevalBinaryOperator({ a, b -> a + b }, 2, true),
            "-" to KevalBinaryOperator({ a, b -> a - b }, 2, true),
            "/" to KevalBinaryOperator(
                { a, b ->
                    if (b == 0.0) throw KevalZeroDivisionException()
                    a / b
                },
                3, true
            ),
            "%" to KevalBinaryOperator(
                { a, b ->
                    if (b == 0.0) throw KevalZeroDivisionException()
                    a % b
                },
                3, true
            ),
            "^" to KevalBinaryOperator({ a, b -> a.pow(b) }, 4, false),
            "*" to KevalBinaryOperator({ a, b -> a * b }, 3, true),
        )

        data class BinaryOperatorDSL(
            var symbol: String? = null,
            var implementation: ((Double, Double) -> Double)? = null,
            var precedence: Int? = null,
            var isLeftAssociative: Boolean? = null
        )

        data class FunctionDSL(
            var name: String? = null,
            var arity: Int? = null,
            var implementation: ((Array<Double>) -> Double)? = null,
        )
    }
}
