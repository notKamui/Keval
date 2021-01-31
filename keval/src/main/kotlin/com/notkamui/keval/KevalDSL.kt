package com.notkamui.keval.framework

import com.notkamui.keval.BinaryOperator
import com.notkamui.keval.KevalZeroDivisionException
import kotlin.math.pow

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
            '(' to BinaryOperator({ _, _ -> 0.0 }, 5, true),
            ')' to BinaryOperator({ _, _ -> 0.0 }, 5, true)
        )

        data class Operator(
            var symbol: Char? = null,
            var implementation: ((Double, Double) -> Double)? = null,
            var precedence: Int = 1,
            var isLeftAssociative: Boolean = false
        )
    }
}
