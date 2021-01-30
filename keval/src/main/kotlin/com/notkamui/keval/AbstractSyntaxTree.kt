package com.notkamui.keval

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import kotlin.math.pow

@Target(AnnotationTarget.ANNOTATION_CLASS)
private annotation class KevalSymbolDefinition

//TODO: change `name` to `String`, it is currently a `Char` to be able to replace the Enum `Operator` easily
@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalFunction(val name: Char, val argsNum: UInt)

@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalBinaryOperator(val name: Char)

@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalUnaryOperator(val name: Char)

@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalConstant(val name: Char)


// Get all annotations in the package "com.notkamui.keval" that has the annotation "KevalSymbolDefinition"
internal val KevalSymbolAnnotations: Set<ClassInfo> = ClassGraph()
        .enableAnnotationInfo()
        .acceptPackages("com.notkamui.keval")
        .scan()
        .use {
            it.getClassesWithAnnotation("com.notkamui.keval.KevalSymbolDefinition")
        }.toSet()


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
    DIV(
        '/', 3, true,
        { a, b ->
            if (b == 0.0) throw KevalZeroDivisionException()
            a / b
        }
    ),
    MOD(
        '%', 3, true,
        { a, b ->
            if (b == 0.0) throw KevalZeroDivisionException()
            a % b
        }
    ),
    POW('^', 4, false, { a, b -> a.pow(b) }),
    LPA('(', 5, true, { _, _ -> 0.0 }),
    RPA(')', 5, true, { _, _ -> 0.0 });

    companion object {
        /**
         * Gets an operator by its symbol
         *
         * @param symbol is the symbol to get the corresponding operator
         * @return the corresponding operator (or null)
         */
        operator fun get(symbol: Char): Operator? = values().firstOrNull { it.symbol == symbol }

        /**
         * Get all the symbols in a string
         *
         * @return all the operators' symbols
         */
        fun symbols(): String = values().joinToString("") { it.symbol.toString() }
    }
}
