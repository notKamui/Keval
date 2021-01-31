package com.notkamui.keval.framework

import com.notkamui.keval.KevalZeroDivisionException
import kotlin.math.pow

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