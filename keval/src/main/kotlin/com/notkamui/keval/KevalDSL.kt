package com.notkamui.keval

import kotlin.math.pow

class Resources internal constructor() {
    private val _operators: MutableMap<Char, BinaryOperator> = mutableMapOf()
    val operators: Map<Char, BinaryOperator>
        get() = _operators.toMap()

    val defaultOperators: Map<Char, BinaryOperator> = Companion.defaultOperators

    operator fun Map<Char, BinaryOperator>.unaryPlus() {
        _operators += this
    }

    operator fun Pair<Char, BinaryOperator>.unaryPlus() {
        _operators += this
    }

    fun operator(definition: OperatorDSL.() -> Unit) {
        val op = OperatorDSL()
        op.definition()

        // checking if every field has been properly defined
        op.symbol ?: throw KevalDSLException("symbol")
        op.implementation ?: throw KevalDSLException("implementation")
        op.precedence ?: throw KevalDSLException("precedence")
        op.isLeftAssociative ?: throw KevalDSLException("isLeftAssociative")

        _operators += op.symbol!! to BinaryOperator(op.implementation!!, op.precedence!!, op.isLeftAssociative!!)
    }

    companion object {
        internal val defaultOperators: Map<Char, BinaryOperator> = mapOf(
            '+' to BinaryOperator({ a, b -> a + b }, 2, true),
            '-' to BinaryOperator({ a, b -> a - b }, 2, true),
            '/' to BinaryOperator(
                { a, b ->
                    if (b == 0.0) throw KevalZeroDivisionException()
                    a / b
                },
                3, true
            ),
            '%' to BinaryOperator(
                { a, b ->
                    if (b == 0.0) throw KevalZeroDivisionException()
                    a % b
                },
                3, true
            ),
            '^' to BinaryOperator({ a, b -> a.pow(b) }, 4, false),
            '*' to BinaryOperator({ a, b -> a * b }, 3, true),
        )

        data class OperatorDSL(
            var symbol: Char? = null,
            var implementation: ((Double, Double) -> Double)? = null,
            var precedence: Int? = null,
            var isLeftAssociative: Boolean? = null
        )
    }
}
