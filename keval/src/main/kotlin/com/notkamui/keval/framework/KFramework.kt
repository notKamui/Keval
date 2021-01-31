package com.notkamui.keval.framework

import com.notkamui.keval.BinaryOperator

class Resources internal constructor() {
    private val _operators: MutableMap<Char, BinaryOperator> = mutableMapOf()
    val operators: Map<Char, BinaryOperator>
        get() = _operators.toMap()

    val defaultOperators: Map<Char, BinaryOperator> = Resources.defaultOperators

    operator fun Map<Char, BinaryOperator>.unaryPlus() {
        _operators += this
    }

    operator fun Pair<Char, BinaryOperator>.unaryPlus() {
        _operators += this
    }

    operator fun Operator.unaryPlus() {
        _operators += symbol!! to BinaryOperator(implementation!!, precedence, isLeftAssociative)
    }

    operator fun Collection<Char>.unaryMinus() {
        _operators -= this
    }

    operator fun Char.unaryMinus() {
        _operators -= this
    }

    fun operator(definition: Operator.() -> Unit): Operator {
        val op = Operator()
        op.definition()
        return op
    }

    companion object {
        val defaultOperators: Map<Char, BinaryOperator> = mapOf(
                '+' to BinaryOperator(::KevalAdd, 2, true),
                '-' to BinaryOperator(::KevalSub, 2, true),
                '/' to BinaryOperator(::KevalDiv, 3, true),
                '%' to BinaryOperator(::KevalMod, 3, true),
                '^' to BinaryOperator(::KevalPow, 4, false),
                '*' to BinaryOperator(::KevalMul, 3, true),
                '(' to BinaryOperator(::KevalRPA, 5, true),
                ')' to BinaryOperator(::KevalLPA, 5, true)
        )

        data class Operator(
                var symbol: Char? = null,
                var implementation: ((Double, Double) -> Double)? = null,
                var precedence: Int = 1,
                var isLeftAssociative: Boolean = false
        )
    }
}