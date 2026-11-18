package com.notkamui.keval

/**
 * Configures operators, functions, and constants for a [Keval] instance.
 *
 * Obtained through [Keval.create]. Supports the same registration DSL as [kevalResources].
 */
class KevalBuilder<N> private constructor(
    private val number: KevalNumber<N>,
    private val resourcesBuilder: KevalResourcesBuilder<N>,
) : KevalResourcesDsl<N> by resourcesBuilder {

    internal constructor(
        number: KevalNumber<N>,
        baseResources: Map<String, KevalOperator<N>> = mapOf(),
    ) : this(number, KevalResourcesBuilder(baseResources))

    /** Includes the built-in operators, functions, and constants for this number type. */
    fun includeDefault(): KevalBuilder<N> = apply {
        resourcesBuilder.addAll(number.defaultResources())
    }

    /** Creates a [Keval] instance with the resources registered so far. */
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
