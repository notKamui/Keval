package com.notkamui.keval

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Main class for evaluating mathematical expressions.
 * It can be customized with additional operators, functions, and constants.
 */
class Keval<N> internal constructor(
    private val number: KevalNumber<N>,
    private val resources: Map<String, KevalOperator<N>>,
    private val operators: Map<String, KevalOperator<N>>,
) {

    internal constructor(
        number: KevalNumber<N>,
        resources: Map<String, KevalOperator<N>>,
    ) : this(
        number = number,
        resources = resources,
        operators = resources + (
            "*" to KevalBinaryOperator(3, true) { a, b -> number.multiply(a, b) }
            ),
    )

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

    fun withConstant(
        name: String,
        value: N
    ): Keval<N> = KevalBuilder(number, resources)
        .constant {
            this.name = name
            this.value = value
        }
        .build()

    fun withDefault(): Keval<N> = KevalBuilder(number, resources).includeDefault().build()

    fun compile(mathExpression: String): CompiledExpression<N> {
        val root = mathExpression.toAST(number, operators)
        return CompiledExpression(root, root.collectVariables())
    }

    fun eval(mathExpression: String): N = eval(mathExpression, emptyMap())

    fun eval(mathExpression: String, bindings: Map<String, N>): N =
        compile(mathExpression).eval(bindings)

    fun evalOrNull(mathExpression: String): N? = evalOrNull(mathExpression, emptyMap())

    fun evalOrNull(mathExpression: String, bindings: Map<String, N>): N? = try {
        eval(mathExpression, bindings)
    } catch (_: KevalException) {
        null
    }

    fun evalResult(mathExpression: String): Result<N> = evalResult(mathExpression, emptyMap())

    fun evalResult(mathExpression: String, bindings: Map<String, N>): Result<N> = try {
        Result.success(eval(mathExpression, bindings))
    } catch (e: KevalException) {
        Result.failure(e)
    }

    /**
     * Returns the operator resources of this [Keval] instance, including the non-overridable `*` operator.
     */
    fun resourcesView(): Map<String, KevalOperator<N>> = operators

    companion object {
        @JvmStatic
        fun <N> create(
            number: KevalNumber<N>,
            generator: KevalBuilder<N>.() -> Unit = { includeDefault() }
        ): Keval<N> =
            KevalBuilder(number).apply(generator).build()

        @JvmName("evaluate")
        @JvmStatic
        fun eval(mathExpression: String): Double =
            KevalNumbers.defaultRealKeval.eval(mathExpression)
    }
}

fun String.keval(generator: KevalBuilder<Double>.() -> Unit): Double =
    Keval.create(KevalNumbers.real, generator).eval(this)

fun String.keval(): Double = KevalNumbers.real.eval(this)

fun String.kevalOrNull(): Double? = KevalNumbers.defaultRealKeval.evalOrNull(this)

fun String.kevalResult(): Result<Double> = KevalNumbers.defaultRealKeval.evalResult(this)
