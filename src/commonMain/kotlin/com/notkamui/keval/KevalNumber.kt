package com.notkamui.keval

/**
 * Describes how a numeric type is parsed and which built-in operators, functions, and constants
 * are available by default for that type.
 */
interface KevalNumber<N> {
    fun isValidLiteral(token: String): Boolean
    fun parseLiteral(token: String): N
    fun defaultResources(): Map<String, KevalOperator<N>>
}

/**
 * Entry points for built-in numeric type implementations.
 */
object KevalNumbers {
    val Double: KevalNumber<kotlin.Double> = KevalNumberDouble
}

/**
 * Evaluates [expression] using this number type's default resources.
 */
fun <N> KevalNumber<N>.eval(expression: String): N =
    Keval.create(this) { includeDefault() }.eval(expression)
