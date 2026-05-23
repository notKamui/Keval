package com.notkamui.keval

/**
 * Represents an operator, may be either a binary operator, a unary operator, a function, or a constant
 */
sealed interface KevalOperator<N>

/**
 * Represents a binary operator
 *
 * @property precedence is the precedence of the operator
 * @property isLeftAssociative is true if the operator is left associative, false otherwise
 * @property implementation is the actual implementation of the operator
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
 *
 * @property arity is the arity of the function (how many arguments it takes). If null, the function is variadic
 * @property implementation is the actual implementation of the function
 */
internal data class KevalFunction<N>(
    val arity: Int?,
    val implementation: (List<N>) -> N
) : KevalOperator<N>

/**
 * Represents a constant
 *
 * @property value is the value of the constant
 */
internal data class KevalConstant<N>(
    val value: N
) : KevalOperator<N>

/**
 * Represents a node in an AST and can evaluate its value
 *
 * Can either be an operator, or a leaf (a value)
 */
internal interface Node<N> {
    /**
     * Evaluates the value of this node
     *
     * @return the value of the node
     * @throws KevalZeroDivisionException in case of a zero division
     */
    fun eval(): N
}

/**
 * An binary operator node
 *
 * @property left is its left child
 * @property op is the actual operator
 * @property right is its right child
 * @constructor Creates an operator node
 */
internal data class BinaryOperatorNode<N>(
    private val left: Node<N>,
    private val op: (N, N) -> N,
    private val right: Node<N>
) : Node<N> {
    override fun eval(): N = op(left.eval(), right.eval())
}

internal data class UnaryOperatorNode<N>(
    private val op: (N) -> N,
    private val child: Node<N>
) : Node<N> {
    override fun eval(): N = op(child.eval())
}

internal data class FunctionNode<N>(
    private val func: (List<N>) -> N,
    private val children: List<Node<N>>
) : Node<N> {
    override fun eval(): N = func(children.map { it.eval() })
}

/**
 * A value node (leaf)
 *
 * @property value is its value
 * @constructor Creates a value node
 */
internal data class ValueNode<N>(
    private val value: N
) : Node<N> {
    override fun eval(): N = value
}
