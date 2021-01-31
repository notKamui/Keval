package com.notkamui.keval.framework

import io.github.classgraph.ClassGraph
import io.github.classgraph.MethodInfo
import io.github.classgraph.ScanResult
import java.lang.reflect.Method

/**
 * Defines Keval's symbols
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
internal annotation class KevalSymbolDefinition

// TODO: change `name` to `String`, it is currently a `Char` to be able to replace the Enum `Operator` easily

/**
 * Annotation class to designate functions for Keval
 *
 * @property name is the name of the function
 * @property arity is the number of arguments the function takes
 */
@ExperimentalUnsignedTypes
@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalFunction(val name: Char, val arity: UInt)

/**
 * Annotation class to designate binary operators for Keval
 *
 * @property symbol is the symbol of the operator
 * @property precedence is the priority of the operator
 * @property isLeftAssociative defines if the operator is left associative (false if right associative)
 */
@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalBinaryOperator(
    val symbol: Char,
    val precedence: Int,
    val isLeftAssociative: Boolean
)

/**
 * Annotation class to designate unary operators for Keval
 *
 * @property symbol is the symbol of the operator
 */
@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalUnaryOperator(val symbol: Char)

/**
 * Annotation class to designate constants for Keval
 *
 * @property symbol is the symbol of the constant
 */
@Target(AnnotationTarget.FUNCTION)
@KevalSymbolDefinition
annotation class KevalConstant(val symbol: Char)

private fun scanClassGraph(_package: String, vararg packages: String): ScanResult = ClassGraph()
    .enableMethodInfo()
    .enableClassInfo()
    .enableAnnotationInfo()
    .acceptPackages(_package, *packages)
    .scan()

private fun ScanResult.getMethodInfos(): List<MethodInfo> = this
    .getClassesWithMethodAnnotation(KevalBinaryOperator::class.java.name)
    .flatMap { classInfo ->
        classInfo.methodInfo.asSequence()
    }.filter { methodInfo ->
        methodInfo.hasAnnotation(KevalBinaryOperator::class.java.name)
    }

/**
 * Gets an operator by its symbol, currently checking only in this package.
 *
 * @param symbol is the symbol to get the corresponding operator
 * @return the corresponding method (or null)
 */
fun getKevalOperator(symbol: Char, _package: String, vararg packages: String): Method? =
    scanClassGraph(_package, *packages)
        .use {
            it.getMethodInfos().firstOrNull { methodInfo ->
                methodInfo.getAnnotationInfo(KevalBinaryOperator::class.java.name)
                    .parameterValues
                    .getValue("symbol") as Char == symbol
            }?.loadClassAndGetMethod()
        }

/**
 * Get all the symbols
 *
 * @return all the operators' symbols
 */
fun kevalSymbolsDefault(_package: String, vararg packages: String): List<Char> =
    scanClassGraph(_package, *packages)
        .use {
            it.getMethodInfos().map { methodInfo ->
                methodInfo.getAnnotationInfo(KevalBinaryOperator::class.java.name)
                    .parameterValues
                    .getValue("symbol") as Char
            }
        }

/**
 * Gets the precedence of an operator
 */
fun Method.precedence(): Int =
    getAnnotation(KevalBinaryOperator::class.java).precedence

/**
 * Gets the associativity of an operator
 */
fun Method.isLeftAssociative(): Boolean =
    getAnnotation(KevalBinaryOperator::class.java).isLeftAssociative
