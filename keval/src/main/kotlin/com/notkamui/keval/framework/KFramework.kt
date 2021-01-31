package com.notkamui.keval.framework

import com.notkamui.keval.BinaryOperator
import io.github.classgraph.ClassGraph
import java.lang.reflect.Method

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

/**
 * Gets an operator by its symbol, currently checking only in this package.
 *
 * @param symbol is the symbol to get the corresponding operator
 * @return the corresponding method (or null)
 */
fun getKevalOperator(symbol: Char, _package: String, vararg packages: String): Method? =
        ClassGraph()
                .enableMethodInfo()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(_package, *packages)
                .scan()
                .use {
                    val binOperatorClass = it.getClassesWithMethodAnnotation(KevalBinaryOperator::class.java.name)
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
 * Get all the symbols
 *
 * @return all the operators' symbols
 */
fun kevalSymbolsDefault(_package: String, vararg packages: String): List<Char> = ClassGraph()
        .enableMethodInfo()
        .enableClassInfo()
        .enableAnnotationInfo()
        .acceptPackages(_package, *packages)
        .scan()
        .use {
            val binOperatorClass = it.getClassesWithMethodAnnotation(KevalBinaryOperator::class.java.name)
            binOperatorClass.flatMap { classInfo ->
                classInfo.methodInfo.asSequence()
            }.filter { methodInfo ->
                methodInfo.hasAnnotation(KevalBinaryOperator::class.java.name)
            }.map { methodInfo ->
                methodInfo.getAnnotationInfo(KevalBinaryOperator::class.java.name)
                        .parameterValues
                        .getValue("symbol") as Char
            }
        }

fun Method.precedence(): Int =
        getAnnotation(KevalBinaryOperator::class.java).precedence

fun Method.isLeftAssociative(): Boolean =
        getAnnotation(KevalBinaryOperator::class.java).isLeftAssociative


class Resources internal constructor() {
    private val _operators: MutableMap<Char, BinaryOperator> = mutableMapOf()
    val operators: Map<Char, BinaryOperator>
        get() = _operators

    operator fun Map<Char, BinaryOperator>.unaryPlus() {
        _operators += this
    }

    operator fun Pair<Char, BinaryOperator>.unaryPlus() {
        _operators += this
    }


    fun loadResources(_package: String, vararg packages: String): Map<Char, BinaryOperator> {
        return kevalSymbolsDefault(_package, *packages).map {
            val mthd = getKevalOperator(it, _package, *packages)!!
            it to BinaryOperator(
                    { x, y ->
                        mthd.invoke(null, x, y) as Double
                    },
                    mthd.precedence(),
                    mthd.isLeftAssociative())
        }.toMap()
    }

    fun loadAllResources(): Map<Char, BinaryOperator> =
            loadResources("")

    @Deprecated(message = "reflection is super slow", replaceWith = ReplaceWith("""{
            +('+' to BinaryOperator(::KevalAdd, 2, true))
            +('-' to BinaryOperator(::KevalSub, 2, true))
            +('/' to BinaryOperator(::KevalDiv, 3, true))
            +('%' to BinaryOperator(::KevalMod, 3, true))
            +('^' to BinaryOperator(::KevalPow, 4, false))
        }"""))
    fun loadBuiltInOperators(): Map<Char, BinaryOperator> =
            loadResources(KevalSymbolDefinition::class.java.packageName)
}