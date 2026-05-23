package com.notkamui.keval

/**
 * Parses string literals into values of type [N].
 */
interface KevalLiteralParser<N> {
    fun isValidLiteral(token: String): Boolean
    fun parseLiteral(token: String): N
}

/**
 * Provides built-in operators, functions, and constants for a numeric type.
 */
interface KevalDefaults<N> {
    fun defaultResources(): Map<String, KevalOperator<N>>
}

/**
 * Describes how a numeric type is parsed, multiplied (for implicit `*`), and which defaults are available.
 */
interface KevalNumber<N> : KevalLiteralParser<N>, KevalDefaults<N> {
    /** Used for implicit and explicit multiplication; cannot be overridden by consumers. */
    fun multiply(a: N, b: N): N
}

typealias KevalReal = Keval<Double>

/**
 * Entry points for built-in numeric type implementations.
 */
object KevalNumbers {
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
 * Evaluates [expression] using the given [number] context and optional [configure] block.
 */
fun <N> String.evalWith(
    number: KevalNumber<N>,
    configure: KevalBuilder<N>.() -> Unit = { includeDefault() },
): N = Keval.create(number, configure).eval(this)

/**
 * Compiles [expression] using the given [number] context and optional [configure] block.
 */
fun <N> String.compileWith(
    number: KevalNumber<N>,
    configure: KevalBuilder<N>.() -> Unit = { includeDefault() },
): CompiledExpression<N> = Keval.create(number, configure).compile(this)
