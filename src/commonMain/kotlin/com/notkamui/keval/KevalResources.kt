package com.notkamui.keval

/**
 * Defines the built-in operators, functions, and constants returned by
 * [KevalDefaults.defaultResources], using the same DSL as [Keval.create].
 *
 * Register both a binary and a unary operator under the same symbol (for example `+` or `-`)
 * when you need both forms. Do not register `*`; implement [KevalNumber.multiply] instead.
 */
fun <N> kevalResources(definition: KevalResourcesBuilder<N>.() -> Unit): Map<String, KevalOperator<N>> =
    KevalResourcesBuilder<N>().apply(definition).build()

/**
 * DSL for registering operators, functions, and constants — used by [Keval.create] and [kevalResources].
 */
interface KevalResourcesDsl<N> {
    /** Adds a binary operator. */
    fun binaryOperator(definition: KevalResourcesBuilder.BinaryOperator<N>.() -> Unit)

    /** Adds a unary operator. */
    fun unaryOperator(definition: KevalResourcesBuilder.UnaryOperator<N>.() -> Unit)

    /** Adds a function. */
    fun function(definition: KevalResourcesBuilder.Function<N>.() -> Unit)

    /** Adds a constant. */
    fun constant(definition: KevalResourcesBuilder.Constant<N>.() -> Unit)
}

/**
 * Receiver for [kevalResources]. Prefer calling [kevalResources] rather than constructing this type yourself.
 */
class KevalResourcesBuilder<N> internal constructor(
    baseResources: Map<String, KevalOperator<N>> = emptyMap(),
) : KevalResourcesDsl<N> {
    private val resources: MutableMap<String, KevalOperator<N>> = baseResources.toMutableMap()

    /** Merges additional operators, functions, and constants (same name overwrites). */
    fun addAll(extra: Map<String, KevalOperator<N>>) {
        resources += extra
    }

    override fun binaryOperator(definition: BinaryOperator<N>.() -> Unit) {
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

    override fun unaryOperator(definition: UnaryOperator<N>.() -> Unit) {
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

    override fun function(definition: Function<N>.() -> Unit) {
        val fn = Function<N>().apply(definition)
        resources[requireIdentifier(fn.name, "function")] =
            KevalFunction(requireArity(fn.arity), requireProperty(fn.implementation, "implementation"))
    }

    override fun constant(definition: Constant<N>.() -> Unit) {
        val const = Constant<N>().apply(definition)
        resources[requireIdentifier(const.name, "constant")] =
            KevalConstant(requireProperty(const.value, "value"))
    }

    /** The operators, functions, and constants registered so far. */
    fun build(): Map<String, KevalOperator<N>> = resources.toMap()

    /**
     * Settings for a binary operator in the DSL.
     *
     * @property symbol Operator symbol (not a letter, digit, `_`, `(`, `)`, or `,`).
     * @property precedence Priority level (`0` or greater).
     * @property isLeftAssociative `true` if left-associative.
     * @property implementation `(left, right) -> result`.
     */
    open class BinaryOperator<N>(
        var symbol: Char? = null,
        var precedence: Int? = null,
        var isLeftAssociative: Boolean? = null,
        var implementation: ((N, N) -> N)? = null,
    )

    /**
     * Settings for a unary operator in the DSL.
     *
     * @property symbol Operator symbol (not a letter, digit, `_`, `(`, `)`, or `,`).
     * @property isPrefix `true` for prefix (e.g. `-x`), `false` for postfix (e.g. `x!`).
     * @property implementation `(argument) -> result`.
     */
    open class UnaryOperator<N>(
        var symbol: Char? = null,
        var isPrefix: Boolean? = null,
        var implementation: ((N) -> N)? = null,
    )

    /**
     * Settings for a function in the DSL.
     *
     * @property name Function name (letter/digit/`_` identifier, not starting with a digit).
     * @property arity Argument count, or `null` for a variadic function.
     * @property implementation `(arguments) -> result`.
     */
    open class Function<N>(
        var name: String? = null,
        var arity: Int? = null,
        var implementation: ((List<N>) -> N)? = null,
    )

    /**
     * Settings for a constant in the DSL.
     *
     * @property name Constant name (letter/digit/`_` identifier, not starting with a digit).
     * @property value Constant value.
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
