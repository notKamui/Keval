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
    fun binaryOperator(
        definition: KevalResourcesBuilder.BinaryOperator<N>.() -> Unit,
    ): KevalBuilder<N> = apply {
        resourcesBuilder.binaryOperator(definition)
    }

    /** Defines a unary operator for the current Keval instance. */
    fun unaryOperator(
        definition: KevalResourcesBuilder.UnaryOperator<N>.() -> Unit,
    ): KevalBuilder<N> = apply {
        resourcesBuilder.unaryOperator(definition)
    }

    /** Defines a function for the current Keval instance. */
    fun function(
        definition: KevalResourcesBuilder.Function<N>.() -> Unit,
    ): KevalBuilder<N> = apply {
        resourcesBuilder.function(definition)
    }

    /** Defines a constant for the current Keval instance. */
    fun constant(
        definition: KevalResourcesBuilder.Constant<N>.() -> Unit,
    ): KevalBuilder<N> = apply {
        resourcesBuilder.constant(definition)
    }

    /** Builds the [Keval] instance with the defined resources. */
    fun build(): Keval<N> = Keval(number, resourcesBuilder.build())

    /**
     * @deprecated Use [KevalResourcesBuilder.BinaryOperator].
     */
    @Deprecated(
        message = "Moved to KevalResourcesBuilder.BinaryOperator",
        replaceWith = ReplaceWith("KevalResourcesBuilder.BinaryOperator"),
    )
    class BinaryOperatorBuilder<N>(
        symbol: Char? = null,
        precedence: Int? = null,
        isLeftAssociative: Boolean? = null,
        implementation: ((N, N) -> N)? = null,
    ) : KevalResourcesBuilder.BinaryOperator<N>(
        symbol,
        precedence,
        isLeftAssociative,
        implementation,
    )

    /**
     * @deprecated Use [KevalResourcesBuilder.UnaryOperator].
     */
    @Deprecated(
        message = "Moved to KevalResourcesBuilder.UnaryOperator",
        replaceWith = ReplaceWith("KevalResourcesBuilder.UnaryOperator"),
    )
    class UnaryOperatorBuilder<N>(
        symbol: Char? = null,
        isPrefix: Boolean? = null,
        implementation: ((N) -> N)? = null,
    ) : KevalResourcesBuilder.UnaryOperator<N>(
        symbol,
        isPrefix,
        implementation,
    )

    /**
     * @deprecated Use [KevalResourcesBuilder.Function].
     */
    @Deprecated(
        message = "Moved to KevalResourcesBuilder.Function",
        replaceWith = ReplaceWith("KevalResourcesBuilder.Function"),
    )
    class FunctionBuilder<N>(
        name: String? = null,
        arity: Int? = null,
        implementation: ((List<N>) -> N)? = null,
    ) : KevalResourcesBuilder.Function<N>(
        name,
        arity,
        implementation,
    )

    /**
     * @deprecated Use [KevalResourcesBuilder.Constant].
     */
    @Deprecated(
        message = "Moved to KevalResourcesBuilder.Constant",
        replaceWith = ReplaceWith("KevalResourcesBuilder.Constant"),
    )
    class ConstantBuilder<N>(
        name: String? = null,
        value: N? = null,
    ) : KevalResourcesBuilder.Constant<N>(
        name,
        value,
    )
}
