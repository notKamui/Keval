package com.notkamui.keval.framework

import com.notkamui.keval.KevalZeroDivisionException
import kotlin.math.pow

/**
 * Addition operator
 *
 * @param x is the first value
 * @param y is the second value
 * @return the result
 */
@KevalBinaryOperator(symbol = '+', precedence = 2, isLeftAssociative = true)
fun kevalAdd(x: Double, y: Double): Double = x + y

/**
 * Subtraction operator
 *
 * @param x is the first value
 * @param y is the second value
 * @return the result
 */
@KevalBinaryOperator(symbol = '-', precedence = 2, isLeftAssociative = true)
fun kevalSub(x: Double, y: Double): Double = x - y

/**
 * Product operator
 *
 * @param x is the first value
 * @param y is the second value
 * @return the result
 */
@KevalBinaryOperator(symbol = '*', precedence = 3, isLeftAssociative = true)
fun kevalMul(x: Double, y: Double): Double = x * y

/**
 * Division operator
 *
 * @param x is the first value
 * @param y is the second value
 * @return the result
 * @throws KevalZeroDivisionException if y == 0
 */
@KevalBinaryOperator(symbol = '/', precedence = 3, isLeftAssociative = true)
fun kevalDiv(x: Double, y: Double): Double {
    if (y == 0.0) throw KevalZeroDivisionException()
    return x / y
}

/**
 * Remainder operator
 *
 * @param x is the first value
 * @param y is the second value
 * @return the result
 * @throws KevalZeroDivisionException if y == 0
 */
@KevalBinaryOperator(symbol = '%', precedence = 3, isLeftAssociative = true)
fun kevalMod(x: Double, y: Double): Double {
    if (y == 0.0) throw KevalZeroDivisionException()
    return y % x
}

/**
 * Exponent operator
 *
 * @param x is the first value
 * @param y is the second value
 * @return the result
 */
@KevalBinaryOperator(symbol = '^', precedence = 4, isLeftAssociative = false)
fun kevalPow(x: Double, y: Double): Double = x.pow(y)

/**
 * Left parenthesis
 *
 * @param x _
 * @param y _
 * @return _
 */
@KevalBinaryOperator(symbol = '(', precedence = 5, isLeftAssociative = true)
fun kevalLPA(x: Double, y: Double): Double = 0.0

/**
 * Left parenthesis
 *
 * @param x _
 * @param y _
 * @return _
 */
@KevalBinaryOperator(symbol = ')', precedence = 5, isLeftAssociative = true)
fun kevalRPA(x: Double, y: Double): Double = 0.0
