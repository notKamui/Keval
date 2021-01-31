package com.notkamui.keval.stress

import com.notkamui.keval.BinaryOperator
import com.notkamui.keval.Keval
import com.notkamui.keval.framework.*
import com.notkamui.keval.testOperators.hypotenuse
import kotlin.random.Random
import kotlin.system.measureNanoTime

fun generateNoReflection(): Keval {
    return Keval {
        +('+' to BinaryOperator(::KevalAdd, 2, true))
        +('-' to BinaryOperator(::KevalSub, 2, true))
        +('/' to BinaryOperator(::KevalDiv, 3, true))
        +('%' to BinaryOperator(::KevalMod, 3, true))
        +('^' to BinaryOperator(::KevalPow, 4, false))
    }
}

fun generateReflection(): Keval {
    return Keval {
        +loadBuiltInOperators()
    }
}

fun generatePartialReflection(): Keval {
    return Keval {
        +('+' to BinaryOperator(::KevalAdd, 2, true))
        +('-' to BinaryOperator(::KevalSub, 2, true))
        +('/' to BinaryOperator(::KevalDiv, 3, true))
        +('%' to BinaryOperator(::KevalMod, 3, true))
        +('^' to BinaryOperator(::KevalPow, 4, false))
        +loadResources("com.notkamui.keval.testOperators")
    }
}

fun generateExtendedNoReflection(): Keval {
    return Keval {
        +('+' to BinaryOperator(::KevalAdd, 2, true))
        +('-' to BinaryOperator(::KevalSub, 2, true))
        +('/' to BinaryOperator(::KevalDiv, 3, true))
        +('%' to BinaryOperator(::KevalMod, 3, true))
        +('^' to BinaryOperator(::KevalPow, 4, false))
        +(';' to BinaryOperator(::hypotenuse, 4, false))
    }
}

val ops = setOf('+', '-', '*', '^')
val extendedOps = ops + ';'
fun generateSExpression(_ops: Set<Char>): String {
    return (0..Random.nextInt(1, 4))
            .fold(Random.nextDouble(0.1, 10.0).toString()) { acc, _ ->
                acc + _ops.random() + Random.nextDouble(0.1, 10.0)
            }
}

fun generateExpression(_ops: Set<Char>): String {
    fun generateExpressionInner(depth: Int): String {
        return when (depth) {
            1 -> '(' + generateSExpression(_ops) + ')' +
                    _ops.random() +
                    '(' + generateSExpression(_ops) + ')'
            0 -> generateSExpression(_ops)
            else -> '(' + generateExpressionInner(Random.nextInt(0, depth)) + ')' +
                    _ops.random() +
                    '(' + generateExpressionInner(Random.nextInt(0, depth)) + ')'
        }
    }
    return generateExpressionInner(4)
}

fun checkFullReflectionVSNoReflection() {
    val expressionSet = List(1000) { generateExpression(ops) }
    var oneNoReflection: List<Double>
    val oneNoReflectionTime = measureNanoTime {
        oneNoReflection =
                generateNoReflection().let {
                    expressionSet.map { expression ->
                        it.eval(expression)
                    }
                }
    }
    println("One no reflect: $oneNoReflectionTime")

    var oneReflection: List<Double>
    val oneReflectionTime = measureNanoTime {
        oneReflection =
                generateReflection().let {
                    expressionSet.map { expression ->
                        it.eval(expression)
                    }
                }
    }
    println("One reflect: $oneReflectionTime")

    var manyNoReflection: List<Double>
    val manyNoReflectionTime = measureNanoTime {
        manyNoReflection =
                expressionSet.map { expression ->
                    generateNoReflection().eval(expression)
                }
    }
    println("Many no reflect: $manyNoReflectionTime")

    var manyReflection: List<Double>
    val manyReflectionTime = measureNanoTime {
        manyReflection =
                expressionSet.map { expression ->
                    generateReflection().eval(expression)
                }
    }
    println("Many reflect: $manyReflectionTime")
}

fun checkPartialReflectionVSExtendedNoReflection() {
    val expressionSet = List(1000) { generateExpression(extendedOps) }
    var oneExtendedNoReflection: List<Double>
    val oneExtendedNoReflectionTime = measureNanoTime {
        oneExtendedNoReflection =
                generateExtendedNoReflection().let {
                    expressionSet.map { expression ->
                        it.eval(expression)
                    }
                }
    }
    println("One extended no reflect: $oneExtendedNoReflectionTime")

    var onePartialReflection: List<Double>
    val onePartialReflectionTime = measureNanoTime {
        onePartialReflection =
                generatePartialReflection().let {
                    expressionSet.map { expression ->
                        it.eval(expression)
                    }
                }
    }
    println("One partial reflect: $onePartialReflectionTime")

    var manyExtendedNoReflection: List<Double>
    val manyExtendedNoReflectionTime = measureNanoTime {
        manyExtendedNoReflection =
                expressionSet.map { expression ->
                    generateExtendedNoReflection().eval(expression)
                }
    }
    println("Many extended no reflect: $manyExtendedNoReflectionTime")

    var manyPartialReflection: List<Double>
    val manyPartialReflectionTime = measureNanoTime {
        manyPartialReflection =
                expressionSet.map { expression ->
                    generatePartialReflection().eval(expression)
                }
    }
    println("Many partial reflect: $manyPartialReflectionTime")
}

fun main() {
    ManualClassLoader.load() //warm up the JVM
    checkFullReflectionVSNoReflection()
    checkPartialReflectionVSExtendedNoReflection()
}


class Dummy {
    fun m() {
        1+1
    }
}

object ManualClassLoader {
    internal fun load() {
        for (i in 0..999999) {
            val dummy = Dummy()
            dummy.m()
        }
    }
}