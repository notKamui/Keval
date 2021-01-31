package com.notkamui.keval.framework

import com.notkamui.keval.KevalZeroDivisionException
import kotlin.math.pow

fun kevalAdd(x: Double, y: Double): Double = x + y

fun kevalSub(x: Double, y: Double): Double = x - y

fun kevalMul(x: Double, y: Double): Double = x * y

fun kevalDiv(x: Double, y: Double): Double {
    if (y == 0.0) throw KevalZeroDivisionException()
    return x / y
}

fun kevalMod(x: Double, y: Double): Double {
    if (y == 0.0) throw KevalZeroDivisionException()
    return y % x
}

fun kevalPow(x: Double, y: Double): Double = x.pow(y)

fun kevalLPA(x: Double, y: Double): Double = 0.0

fun kevalRPA(x: Double, y: Double): Double = 0.0
