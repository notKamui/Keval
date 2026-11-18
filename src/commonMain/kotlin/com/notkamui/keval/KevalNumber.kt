package com.notkamui.keval

/**
 * Parses numeric literals in expressions into values of type [N].
 */
interface KevalLiteralParser<N> {
    fun isValidLiteral(token: String): Boolean
    fun parseLiteral(token: String): N
}

/**
 * Built-in operators, functions, and constants loaded by [KevalBuilder.includeDefault].
 *
 * Prefer building the map with [kevalResources].
 */
interface KevalDefaults<N> {
    fun defaultResources(): Map<String, KevalOperator<N>>
}

/**
 * Hooks Keval needs for a numeric type: parsing literals, multiplication, and optional defaults.
 */
interface KevalNumber<N> : KevalLiteralParser<N>, KevalDefaults<N> {
    /** Multiplication for `*` and for implicit products such as `(2)(3)`. */
    fun multiply(a: N, b: N): N
}

typealias KevalReal = Keval<Double>

/**
 * Built-in [KevalNumber] implementations.
 */
object KevalNumbers {
    /** Default `Double` number type (all platforms). */
    val real: KevalNumber<Double> = KevalNumberDouble

    internal val defaultRealKeval: Keval<Double> by lazy {
        Keval.create(real) { includeDefault() }
    }
}

/**
 * Evaluates [expression] using this number type's default resources.
 */
fun <N> KevalNumber<N>.eval(expression: String): N =
    if (this === KevalNumberDouble) {
        @Suppress("UNCHECKED_CAST")
        KevalNumbers.defaultRealKeval.eval(expression) as N
    } else {
        Keval.create(this) { includeDefault() }.eval(expression)
    }

/**
 * Evaluates this expression using [number] and an optional [configure] block.
 */
fun <N> String.evalWith(
    number: KevalNumber<N>,
    configure: KevalBuilder<N>.() -> Unit = { includeDefault() },
): N = Keval.create(number, configure).eval(this)

/**
 * Compiles this expression using [number] and an optional [configure] block.
 */
fun <N> String.compileWith(
    number: KevalNumber<N>,
    configure: KevalBuilder<N>.() -> Unit = { includeDefault() },
): CompiledExpression<N> = Keval.create(number, configure).compile(this)
