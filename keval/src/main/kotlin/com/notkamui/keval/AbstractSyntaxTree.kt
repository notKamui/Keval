package com.notkamui.keval

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.math.pow

@Target(AnnotationTarget.ANNOTATION_CLASS)
private annotation class KevalSymbolDefinition

//TODO: change `name` to `String`, it is currently a `Char` to be able to replace the Enum `Operator` easily

@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalFunction(val name: Char, val argsNum: UInt)

/**
 * For now support only this.
 *
 * @property symbol is the symbol of the operator
 * @property precedence is the priority of the operator
 * @property isLeftAssociative defines if the operator is left associative (false if right associative)*/
@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalBinaryOperator(
        val symbol: Char,
        val precedence: Int,
        val isLeftAssociative: Boolean
)

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
            it.getClassesWithAnnotation(KevalSymbolDefinition::class.java.name)
        }.toSet()


@KevalBinaryOperator(symbol = '+', precedence = 2, isLeftAssociative = true)
fun KevalAdd(x: Double, y: Double): Double = x + y

@KevalBinaryOperator(symbol = '-', precedence = 2, isLeftAssociative = true)
fun KevalSub(x: Double, y: Double): Double = x - y

@KevalBinaryOperator(symbol = '*', precedence = 3, isLeftAssociative = true)
fun KevalMul(x: Double, y: Double): Double = x * y

@KevalBinaryOperator(symbol = '/', precedence = 3, isLeftAssociative = true)
fun KevalDiv(x: Double, y: Double): Double {
    if (y == 0.0) throw KevalZeroDivisionException()
    return x / y
}

@KevalBinaryOperator(symbol = '%', precedence = 3, isLeftAssociative = true)
fun KevalMod(x: Double, y: Double): Double {
    if (y == 0.0) throw KevalZeroDivisionException()
    return y % x
}

@KevalBinaryOperator(symbol = '^', precedence = 4, isLeftAssociative = false)
fun KevalPow(x: Double, y: Double): Double = x.pow(y)

@KevalBinaryOperator(symbol = '(', precedence = 5, isLeftAssociative = true)
fun KevalLPA(x: Double, y: Double): Double = 0.0

@KevalBinaryOperator(symbol = ')', precedence = 5, isLeftAssociative = true)
fun KevalRPA(x: Double, y: Double): Double = 0.0


/**
 * Gets an operator by its symbol, currently checking only in this package.
 *
 * @param symbol is the symbol to get the corresponding operator
 * @return the corresponding method (or null)
 */
fun getKevalOperator(symbol: Char): Method? =
        ClassGraph()
                .enableMethodInfo()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(KevalSymbolDefinition::class.java.packageName)
                .scan()
                .use {
                    val binOperatorClass = it.getClassesWithMethodAnnotation("com.notkamui.keval.KevalBinaryOperator")
                    binOperatorClass.flatMap { classInfo ->
                        classInfo.methodInfo.asSequence()
                    }.filter { methodInfo ->
                        methodInfo.hasAnnotation(KevalBinaryOperator::class.java.name)
                    }.firstOrNull { methodInfo ->
                        methodInfo.getAnnotationInfo(KevalBinaryOperator::class.java.name)
                                .parameterValues
                                .getValue("symbol") as Char == symbol
                    }?.loadClassAndGetMethod()
                }

/**
 * Get all the symbols in a string
 *
 * @return all the operators' symbols
 */
fun kevalSymbols(): String = ClassGraph()
        .enableMethodInfo()
        .enableClassInfo()
        .enableAnnotationInfo()
        .acceptPackages(KevalSymbolDefinition::class.java.packageName)
        .scan()
        .use {
            val binOperatorClass = it.getClassesWithMethodAnnotation("com.notkamui.keval.KevalBinaryOperator")
            binOperatorClass.flatMap { classInfo ->
                classInfo.methodInfo.asSequence()
            }.filter { methodInfo ->
                methodInfo.hasAnnotation(KevalBinaryOperator::class.java.name)
            }.fold("") { acc, methodInfo ->
                acc + methodInfo.getAnnotationInfo(KevalBinaryOperator::class.java.name)
                        .parameterValues
                        .getValue("symbol") as Char
            }
        }

fun Method.precedence(): Int {
    return getAnnotation(KevalBinaryOperator::class.java).precedence
}

fun Method.isLeftAssociative(): Boolean {
    return getAnnotation(KevalBinaryOperator::class.java).isLeftAssociative
}

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
        private val op: Method,
        private val right: Node
) : Node {
    override fun eval(): Double {
        // Because we use reflection, there is no type checks, which will make sense when
        //      we will allow functions(who can take arbitrary number of arguments) and
        //      unary operators(which take 1 argument)
        assert(op.parameterCount == 2) { "Operator must have exactly 2 parameters" }
        assert(op.parameterTypes.all { it == Double::class.java }) { "Operator must act on double" }
        assert(op.returnType == Double::class.java) { "Operator must return double" }

        try {
            return op.invoke(null, left.eval(), right.eval()) as Double // the first parameter is the class instance, all of our methods are statics, hence it is null
        } catch (e: InvocationTargetException) {
            throw e.cause!!
        }
    }
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