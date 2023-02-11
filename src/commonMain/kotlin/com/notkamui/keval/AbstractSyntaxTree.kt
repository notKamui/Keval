package com.notkamui.keval

/**
 * Represents an operator, may be either a binary operator, or a function
 */
sealed interface KevalOperator

/**
 * Represents a binary operator
 *
 * @property precedence is the precedence of the operator
 * @property isLeftAssociative is true if the operator is left associative, false otherwise
 * @property implementation is the actual implementation of the operator
 */
internal data class KevalBinaryOperator(
    val precedence: Int,
    val isLeftAssociative: Boolean,
    val implementation: (Double, Double) -> Double
) : KevalOperator

/**
 * Represents a function
 *
 * @property arity is the arity of the function (how many arguments it takes)
 * @property implementation is the actual implementation of the function
 */
internal data class KevalFunction(
    val arity: Int,
    val implementation: (DoubleArray) -> Double
) : KevalOperator

/**
 * Represents a constant
 *
 * @property value is the value of the constant
 */
internal data class KevalConstant(
    val value: Double
) : KevalOperator

/**
 * Represents a node in an AST and can evaluate its value
 *
 * Can either be an operator, or a leaf (a value)
 */
internal interface Node {
    /**
     * Evaluates the value of this node
     *
     * @return the value of the node
     * @throws KevalZeroDivisionException in case of a zero division
     */
    fun eval(): Double
}

/**
 * An binary operator node
 *
 * @property left is its left child
 * @property op is the actual operator
 * @property right is its right child
 * @constructor Creates an operator node
 */
internal data class OperatorNode(
    private val left: Node,
    private val op: (Double, Double) -> Double,
    private val right: Node
) : Node {
    override fun eval(): Double = op(left.eval(), right.eval())
}

internal data class FunctionNode(
    private val func: (DoubleArray) -> Double,
    private val children: List<Node>
) : Node {
    override fun eval(): Double = func(children.map(Node::eval).toDoubleArray())
}

/**
 * A value node (leaf)
 *
 * @property value is its value
 * @constructor Creates a value node
 */
internal data class ValueNode(
    private val value: Double
) : Node {
    override fun eval(): Double = value
}
