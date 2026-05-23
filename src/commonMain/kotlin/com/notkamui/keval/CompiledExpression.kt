package com.notkamui.keval

/**
 * A compiled mathematical expression that can be evaluated repeatedly without re-parsing.
 */
class CompiledExpression<N> internal constructor(
    private val root: Node<N>,
    val variables: Set<String>,
) {
    fun eval(): N = eval(emptyMap())

    fun eval(bindings: Map<String, N>): N = root.eval(bindings)

    fun evalOrNull(): N? = evalOrNull(emptyMap())

    fun evalOrNull(bindings: Map<String, N>): N? = try {
        eval(bindings)
    } catch (_: KevalException) {
        null
    }

    fun evalResult(): Result<N> = evalResult(emptyMap())

    fun evalResult(bindings: Map<String, N>): Result<N> = try {
        Result.success(eval(bindings))
    } catch (e: KevalException) {
        Result.failure(e)
    }
}
