package com.notkamui.keval

/**
 * Represents an operator, may be either a binary operator, a unary operator, a function, or a constant
 */
sealed interface KevalOperator<N>

/**
 * Represents a binary operator
 */
internal data class KevalBinaryOperator<N>(
    val precedence: Int,
    val isLeftAssociative: Boolean,
    val implementation: (N, N) -> N
) : KevalOperator<N>

internal data class KevalUnaryOperator<N>(
    val isPrefix: Boolean,
    val implementation: (N) -> N,
) : KevalOperator<N>

internal data class KevalBothOperator<N>(
    val binary: KevalBinaryOperator<N>,
    val unary: KevalUnaryOperator<N>,
) : KevalOperator<N>

/**
 * Represents a function
 */
internal data class KevalFunction<N>(
    val arity: Int?,
    val implementation: (List<N>) -> N
) : KevalOperator<N>

/**
 * Represents a constant
 */
internal data class KevalConstant<N>(
    val value: N
) : KevalOperator<N>

/**
 * Represents a node in an AST and can evaluate its value
 */
internal interface Node<N> {
    fun eval(bindings: Map<String, N> = emptyMap()): N
    fun collectVariables(): Set<String>
}

internal data class BinaryOperatorNode<N>(
    private val left: Node<N>,
    private val op: (N, N) -> N,
    private val right: Node<N>
) : Node<N> {
    override fun eval(bindings: Map<String, N>): N = op(left.eval(bindings), right.eval(bindings))
    override fun collectVariables(): Set<String> = left.collectVariables() + right.collectVariables()
}

internal data class UnaryOperatorNode<N>(
    private val op: (N) -> N,
    private val child: Node<N>
) : Node<N> {
    override fun eval(bindings: Map<String, N>): N = op(child.eval(bindings))
    override fun collectVariables(): Set<String> = child.collectVariables()
}

internal data class FunctionNode<N>(
    private val func: (List<N>) -> N,
    private val children: List<Node<N>>
) : Node<N> {
    override fun eval(bindings: Map<String, N>): N = func(children.map { it.eval(bindings) })
    override fun collectVariables(): Set<String> = children.flatMap { it.collectVariables() }.toSet()
}

internal data class Function1Node<N>(
    private val func: (List<N>) -> N,
    private val arg: Node<N>,
) : Node<N> {
    override fun eval(bindings: Map<String, N>): N = func(listOf(arg.eval(bindings)))
    override fun collectVariables(): Set<String> = arg.collectVariables()
}

internal data class Function2Node<N>(
    private val func: (List<N>) -> N,
    private val arg1: Node<N>,
    private val arg2: Node<N>,
) : Node<N> {
    override fun eval(bindings: Map<String, N>): N = func(listOf(arg1.eval(bindings), arg2.eval(bindings)))
    override fun collectVariables(): Set<String> = arg1.collectVariables() + arg2.collectVariables()
}

internal data class Function3Node<N>(
    private val func: (List<N>) -> N,
    private val arg1: Node<N>,
    private val arg2: Node<N>,
    private val arg3: Node<N>,
) : Node<N> {
    override fun eval(bindings: Map<String, N>): N =
        func(listOf(arg1.eval(bindings), arg2.eval(bindings), arg3.eval(bindings)))
    override fun collectVariables(): Set<String> =
        arg1.collectVariables() + arg2.collectVariables() + arg3.collectVariables()
}

internal data class Function4Node<N>(
    private val func: (List<N>) -> N,
    private val arg1: Node<N>,
    private val arg2: Node<N>,
    private val arg3: Node<N>,
    private val arg4: Node<N>,
) : Node<N> {
    override fun eval(bindings: Map<String, N>): N =
        func(listOf(arg1.eval(bindings), arg2.eval(bindings), arg3.eval(bindings), arg4.eval(bindings)))
    override fun collectVariables(): Set<String> =
        arg1.collectVariables() + arg2.collectVariables() + arg3.collectVariables() + arg4.collectVariables()
}

internal data class ValueNode<N>(
    private val value: N
) : Node<N> {
    override fun eval(bindings: Map<String, N>): N = value
    override fun collectVariables(): Set<String> = emptySet()
}

internal data class VariableNode<N>(
    val name: String,
) : Node<N> {
    override fun eval(bindings: Map<String, N>): N =
        bindings[name] ?: throw KevalUnresolvedVariableException(name)
    override fun collectVariables(): Set<String> = setOf(name)
}

internal fun <N> createFunctionNode(
    func: (List<N>) -> N,
    args: List<Node<N>>,
): Node<N> = when (args.size) {
    1 -> Function1Node(func, args[0])
    2 -> Function2Node(func, args[0], args[1])
    3 -> Function3Node(func, args[0], args[1], args[2])
    4 -> Function4Node(func, args[0], args[1], args[2], args[3])
    else -> FunctionNode(func, args)
}
