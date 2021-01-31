package com.notkamui.keval.framework

import com.notkamui.keval.KevalZeroDivisionException
import kotlin.math.pow

fun KevalAdd(x: Double, y: Double): Double = x + y

fun KevalSub(x: Double, y: Double): Double = x - y

fun KevalMul(x: Double, y: Double): Double = x * y

fun KevalDiv(x: Double, y: Double): Double {
    if (y == 0.0) throw KevalZeroDivisionException()
    return x / y
}

fun KevalMod(x: Double, y: Double): Double {
    if (y == 0.0) throw KevalZeroDivisionException()
    return y % x
}

fun KevalPow(x: Double, y: Double): Double = x.pow(y)

fun KevalLPA(x: Double, y: Double): Double = 0.0

fun KevalRPA(x: Double, y: Double): Double = 0.0