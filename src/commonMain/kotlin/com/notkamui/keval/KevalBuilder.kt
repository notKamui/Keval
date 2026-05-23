package com.notkamui.keval

/**
 * This class is used to build a Keval instance with custom operators, functions, and constants.
 */
class KevalBuilder<N> internal constructor(
    private val number: KevalNumber<N>,
    baseResources: Map<String, KevalOperator<N>> = mapOf()
) {
    private val resources: MutableMap<String, KevalOperator<N>> = baseResources.toMutableMap()

    /**
     * Includes the default resources (operators, functions, constants) to the current Keval instance.
     */
    fun includeDefault(): KevalBuilder<N> = apply {
        resources += number.defaultResources()
    }

    /**
     * Defines a binary operator for the current Keval instance.
     *
     * @param definition A lambda function that configures a BinaryOperatorBuilder instance.
     */
    fun binaryOperator(definition: BinaryOperatorBuilder<N>.() -> Unit): KevalBuilder<N> = apply {
        val op = BinaryOperatorBuilder<N>().apply(definition)
        validateOperator(op.symbol, op.precedence, op.implementation)
        addOperator(op.symbol!!, KevalBinaryOperator(op.precedence!!, op.isLeftAssociative!!, op.implementation!!), isUnary = false)
    }

    /**
     * Defines a unary operator for the current Keval instance.
     *
     * @param definition A lambda function that configures a UnaryOperatorBuilder instance.
     */
    fun unaryOperator(definition: UnaryOperatorBuilder<N>.() -> Unit): KevalBuilder<N> = apply {
        val op = UnaryOperatorBuilder<N>().apply(definition)
        validateUnaryOperator(op.symbol, op.isPrefix, op.implementation)
        addOperator(op.symbol!!, KevalUnaryOperator(op.isPrefix!!, op.implementation!!), isUnary = true)
    }

    /**
     * Defines a function for the current Keval instance.
     *
     * @param definition A lambda function that configures a FunctionBuilder instance.
     */
    fun function(definition: FunctionBuilder<N>.() -> Unit): KevalBuilder<N> = apply {
        val fn = FunctionBuilder<N>().apply(definition)
        validateFunction(fn.name, fn.arity, fn.implementation)
        resources[fn.name!!] = KevalFunction(fn.arity, fn.implementation!!)
    }

    /**
     * Defines a constant for the current Keval instance.
     *
     * @param definition A lambda function that configures a ConstantBuilder instance.
     */
    fun constant(definition: ConstantBuilder<N>.() -> Unit): KevalBuilder<N> = apply {
        val const = ConstantBuilder<N>().apply(definition)
        validateConstant(const.name, const.value)
        resources[const.name!!] = KevalConstant(const.value!!)
    }

    /**
     * Builds the Keval instance with the defined resources.
     *
     * @return A Keval instance.
     */
    fun build(): Keval<N> = Keval(number, resources)

    private fun validateOperator(symbol: Char?, precedence: Int?, implementation: ((N, N) -> N)?) {
        requireNotNull(symbol) { "symbol is not set" }
        requireNotNull(implementation) { "implementation is not set" }
        requireNotNull(precedence) { "precedence is not set" }
        require(precedence >= 0) { "operator's precedence must always be positive or 0" }
        require(symbol.isOperatorSymbol()) { "a symbol must NOT be a letter, a digit, an underscore, parentheses nor a comma but was: $symbol" }
        require(symbol != '*') { "* cannot be overwritten" }
    }

    private fun validateUnaryOperator(symbol: Char?, isPrefix: Boolean?, implementation: ((N) -> N)?) {
        requireNotNull(symbol) { "symbol is not set" }
        requireNotNull(isPrefix) { "isPrefix is not set" }
        requireNotNull(implementation) { "implementation is not set" }
        require(symbol.isOperatorSymbol()) { "a symbol must NOT be a letter, a digit, an underscore, parentheses nor a comma but was: $symbol" }
    }

    private fun validateFunction(name: String?, arity: Int?, implementation: Any?) {
        requireNotNull(name) { "name is not set" }
        requireNotNull(implementation) { "implementation is not set" }
        require(arity == null || arity >= 0) { "function's arity must always be positive or 0" }
        require(name.isFunctionOrConstantName()) { "a function's name cannot start with a digit and must contain only letters, digits or underscores: $name" }
    }

    private fun validateConstant(name: String?, value: N?) {
        requireNotNull(name) { "name is not set" }
        requireNotNull(value) { "value is not set" }
        require(name.isFunctionOrConstantName()) { "a constant's name cannot start with a digit and must contain only letters, digits or underscores: $name" }
    }

    private fun Char.isOperatorSymbol() = !isLetterOrDigit() && this !in listOf('_', '(', ')', ',')
    private fun String.isFunctionOrConstantName() =
        isNotEmpty() && this[0] !in '0'..'9' && !contains("[^a-zA-Z0-9_]".toRegex())

    private fun addOperator(symbol: Char, operator: KevalOperator<N>, isUnary: Boolean) {
        when (val resource = resources[symbol.toString()]) {
            is KevalUnaryOperator -> resources[symbol.toString()] =
                if (isUnary) operator as KevalUnaryOperator<N>
                else KevalBothOperator(operator as KevalBinaryOperator<N>, resource)

            is KevalBinaryOperator -> resources[symbol.toString()] =
                if (isUnary) KevalBothOperator(resource, operator as KevalUnaryOperator<N>)
                else operator as KevalBinaryOperator<N>

            is KevalBothOperator -> resources[symbol.toString()] =
                if (isUnary) KevalBothOperator(resource.binary, operator as KevalUnaryOperator<N>)
                else KevalBothOperator(operator as KevalBinaryOperator<N>, resource.unary)

            else -> resources[symbol.toString()] = operator
        }
    }

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
        var implementation: ((N, N) -> N)? = null
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
        var implementation: ((N) -> N)? = null
    )

    /**
     * Builder representation of a function.
     *
     * @property name The identifier which represents the function.
     * @property arity The arity of the function (how many arguments it takes). If null, the function is variadic
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
        var value: N? = null
    )
}
