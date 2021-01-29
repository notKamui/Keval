package com.notkamui.keval

import kotlin.math.pow

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
    private val op: Operator,
    private val right: Node
) : Node {
    override fun eval(): Double = op.apply(left.eval(), right.eval())
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

/**
 * Represents all operators
 *
 * @property symbol is the symbol of the operator
 * @property precedence is the priority of the operator
 * @property isLeftAssociative defines if the operator is left associative (false if right associative)
 * @property apply is the function linked to the operator
 * @constructor Creates an Operator type
 */
internal enum class Operator(
    val symbol: Char,
    val precedence: Int,
    val isLeftAssociative: Boolean,
    val apply: (Double, Double) -> Double
) {
    SUB('-', 2, true, { a, b -> a - b }),
    ADD('+', 2, true, { a, b -> a + b }),
    MUL('*', 3, true, { a, b -> a * b }),
    DIV('/', 3, true, { a, b -> a / b }),
    MOD('%', 3, true, { a, b -> a % b }),
    POW('^', 4, false, { a, b -> a.pow(b) });

    companion object {
        /**
         * Gets an operator by its symbol
         *
         * @param symbol is the symbol to get the corresponding operator
         * @return the corresponding operator (or null)
         */
        operator fun get(symbol: Char): Operator? = values().firstOrNull { it.symbol == symbol }

        fun symbols(): String = values().joinToString("") { it.symbol.toString() }
    }
}

/**
 * Invalid Operator Exception
 *
 * @property invalidOperator is the given invalid operator
 * @property position is the token position of said invalid operator in the expression
 */
class InvalidOperatorException(val invalidOperator: String, val position: Int) :
    Exception("Invalid operator: \"$invalidOperator\" at $position")
