package com.notkamui.keval

/**
 * Builds a [Map] of operators, functions, and constants for [KevalDefaults.defaultResources].
 *
 * Concrete [KevalOperator] implementations stay internal; this DSL is the public way to
 * construct them. The non-overridable `*` operator still comes from [KevalNumber.multiply]
 * when a [Keval] instance is built — it does not need to appear in default resources.
 *
 * Binary and unary operators that share a symbol (e.g. `+`, `-`) are merged automatically
 * when both are registered.
 */
fun <N> kevalResources(definition: KevalResourcesBuilder<N>.() -> Unit): Map<String, KevalOperator<N>> =
    KevalResourcesBuilder<N>().apply(definition).build()

/**
 * Mutable builder used by [kevalResources] and, internally, by [KevalBuilder].
 *
 * Owns the DSL property-holder types ([BinaryOperator], [UnaryOperator], [Function], [Constant]).
 */
class KevalResourcesBuilder<N> internal constructor(
    baseResources: Map<String, KevalOperator<N>> = emptyMap(),
) {
    private val resources: MutableMap<String, KevalOperator<N>> = baseResources.toMutableMap()

    /** Adds every entry from [extra] (later keys overwrite earlier ones). */
    fun addAll(extra: Map<String, KevalOperator<N>>): KevalResourcesBuilder<N> = apply {
        resources += extra
    }

    /** Defines a binary operator. */
    fun binaryOperator(definition: BinaryOperator<N>.() -> Unit): KevalResourcesBuilder<N> = apply {
        val op = BinaryOperator<N>().apply(definition)
        addOperator(
            requireBinarySymbol(op.symbol),
            KevalBinaryOperator(
                requirePrecedence(op.precedence),
                requireProperty(op.isLeftAssociative, "isLeftAssociative"),
                requireProperty(op.implementation, "implementation"),
            ),
            isUnary = false,
        )
    }

    /** Defines a unary operator. */
    fun unaryOperator(definition: UnaryOperator<N>.() -> Unit): KevalResourcesBuilder<N> = apply {
        val op = UnaryOperator<N>().apply(definition)
        addOperator(
            requireOperatorSymbol(op.symbol),
            KevalUnaryOperator(
                requireProperty(op.isPrefix, "isPrefix"),
                requireProperty(op.implementation, "implementation"),
            ),
            isUnary = true,
        )
    }

    /** Defines a function. */
    fun function(definition: Function<N>.() -> Unit): KevalResourcesBuilder<N> = apply {
        val fn = Function<N>().apply(definition)
        resources[requireIdentifier(fn.name, "function")] =
            KevalFunction(requireArity(fn.arity), requireProperty(fn.implementation, "implementation"))
    }

    /** Defines a constant. */
    fun constant(definition: Constant<N>.() -> Unit): KevalResourcesBuilder<N> = apply {
        val const = Constant<N>().apply(definition)
        resources[requireIdentifier(const.name, "constant")] =
            KevalConstant(requireProperty(const.value, "value"))
    }

    /** Returns an immutable copy of the registered resources. */
    fun build(): Map<String, KevalOperator<N>> = resources.toMap()

    /**
     * DSL properties for a binary operator.
     *
     * @property symbol The symbol which represents the operator.
     * @property precedence The precedence of the operator.
     * @property isLeftAssociative True when the operator is left associative, false otherwise.
     * @property implementation The actual implementation of the operator.
     */
    open class BinaryOperator<N>(
        var symbol: Char? = null,
        var precedence: Int? = null,
        var isLeftAssociative: Boolean? = null,
        var implementation: ((N, N) -> N)? = null,
    )

    /**
     * DSL properties for a unary operator.
     *
     * @property symbol The symbol which represents the operator.
     * @property isPrefix True when the operator is prefix, false otherwise.
     * @property implementation The actual implementation of the operator.
     */
    open class UnaryOperator<N>(
        var symbol: Char? = null,
        var isPrefix: Boolean? = null,
        var implementation: ((N) -> N)? = null,
    )

    /**
     * DSL properties for a function.
     *
     * @property name The identifier which represents the function.
     * @property arity The arity of the function (how many arguments it takes). If null, the function is variadic.
     * @property implementation The actual implementation of the function.
     */
    open class Function<N>(
        var name: String? = null,
        var arity: Int? = null,
        var implementation: ((List<N>) -> N)? = null,
    )

    /**
     * DSL properties for a constant.
     *
     * @property name The identifier which represents the constant.
     * @property value The value of the constant.
     */
    open class Constant<N>(
        var name: String? = null,
        var value: N? = null,
    )

    private fun <T> requireProperty(value: T?, name: String): T =
        requireNotNull(value) { "$name is not set" }

    private fun requireOperatorSymbol(symbol: Char?): Char {
        val value = requireProperty(symbol, "symbol")
        require(value.isOperatorSymbol()) {
            "a symbol must NOT be a letter, a digit, an underscore, parentheses nor a comma but was: $value"
        }
        return value
    }

    private fun requireBinarySymbol(symbol: Char?): Char {
        val value = requireOperatorSymbol(symbol)
        require(value != '*') { "* cannot be overwritten" }
        return value
    }

    private fun requirePrecedence(precedence: Int?): Int {
        val value = requireProperty(precedence, "precedence")
        require(value >= 0) { "operator's precedence must always be positive or 0" }
        return value
    }

    private fun requireArity(arity: Int?): Int? {
        require(arity == null || arity >= 0) { "function's arity must always be positive or 0" }
        return arity
    }

    private fun requireIdentifier(name: String?, kind: String): String {
        val value = requireProperty(name, "name")
        require(value.isFunctionOrConstantName()) {
            "a $kind's name cannot start with a digit and must contain only letters, digits or underscores: $value"
        }
        return value
    }

    private fun Char.isOperatorSymbol() = !isLetterOrDigit() && this !in listOf('_', '(', ')', ',')

    private fun String.isFunctionOrConstantName() =
        isNotEmpty() && this[0] !in '0'..'9' && !contains("[^a-zA-Z0-9_]".toRegex())

    private fun addOperator(symbol: Char, operator: KevalOperator<N>, isUnary: Boolean) {
        val key = symbol.toString()
        resources[key] = when (val existing = resources[key]) {
            is KevalUnaryOperator ->
                if (isUnary) operator
                else KevalBothOperator(operator as KevalBinaryOperator<N>, existing)

            is KevalBinaryOperator ->
                if (isUnary) KevalBothOperator(existing, operator as KevalUnaryOperator<N>)
                else operator

            is KevalBothOperator ->
                if (isUnary) KevalBothOperator(existing.binary, operator as KevalUnaryOperator<N>)
                else KevalBothOperator(operator as KevalBinaryOperator<N>, existing.unary)

            else -> operator
        }
    }
}
