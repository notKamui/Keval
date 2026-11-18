package com.notkamui.keval

/**
 * Builds a [Keval] instance with custom operators, functions, and constants.
 *
 * Resource registration is shared with [kevalResources] via [KevalResourcesBuilder].
 */
class KevalBuilder<N> internal constructor(
    private val number: KevalNumber<N>,
    baseResources: Map<String, KevalOperator<N>> = mapOf(),
) {
    private val resourcesBuilder = KevalResourcesBuilder(baseResources)

    /** Includes the number type's [KevalDefaults.defaultResources]. */
    fun includeDefault(): KevalBuilder<N> = apply {
        resourcesBuilder.addAll(number.defaultResources())
    }

    /** Defines a binary operator for the current Keval instance. */
    fun binaryOperator(definition: BinaryOperatorBuilder<N>.() -> Unit): KevalBuilder<N> = apply {
        resourcesBuilder.binaryOperator(definition)
    }

    /** Defines a unary operator for the current Keval instance. */
    fun unaryOperator(definition: UnaryOperatorBuilder<N>.() -> Unit): KevalBuilder<N> = apply {
        resourcesBuilder.unaryOperator(definition)
    }

    /** Defines a function for the current Keval instance. */
    fun function(definition: FunctionBuilder<N>.() -> Unit): KevalBuilder<N> = apply {
        resourcesBuilder.function(definition)
    }

    /** Defines a constant for the current Keval instance. */
    fun constant(definition: ConstantBuilder<N>.() -> Unit): KevalBuilder<N> = apply {
        resourcesBuilder.constant(definition)
    }

    /** Builds the [Keval] instance with the defined resources. */
    fun build(): Keval<N> = Keval(number, resourcesBuilder.build())

    /**
     * Builder representation of a binary operator.
     *
     * @property symbol The symbol which represents the operator.
     * @property precedence The precedence of the operator.
     * @property isLeftAssociative True when the operator is left associative, false otherwise.
     * @property implementation The actual implementation of the operator.
     */
    data class BinaryOperatorBuilder<N>(
        var symbol: Char? = null,
        var precedence: Int? = null,
        var isLeftAssociative: Boolean? = null,
        var implementation: ((N, N) -> N)? = null,
    )

    /**
     * Builder representation of a unary operator.
     *
     * @property symbol The symbol which represents the operator.
     * @property isPrefix True when the operator is prefix, false otherwise.
     * @property implementation The actual implementation of the operator.
     */
    data class UnaryOperatorBuilder<N>(
        var symbol: Char? = null,
        var isPrefix: Boolean? = null,
        var implementation: ((N) -> N)? = null,
    )

    /**
     * Builder representation of a function.
     *
     * @property name The identifier which represents the function.
     * @property arity The arity of the function (how many arguments it takes). If null, the function is variadic.
     * @property implementation The actual implementation of the function.
     */
    data class FunctionBuilder<N>(
        var name: String? = null,
        var arity: Int? = null,
        var implementation: ((List<N>) -> N)? = null,
    )

    /**
     * Builder representation of a constant.
     *
     * @property name The identifier which represents the constant.
     * @property value The value of the constant.
     */
    data class ConstantBuilder<N>(
        var name: String? = null,
        var value: N? = null,
    )
}
