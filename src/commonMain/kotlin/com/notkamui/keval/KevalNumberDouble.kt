package com.notkamui.keval

import kotlin.math.*
import kotlin.random.Random

object KevalNumberDouble : KevalNumber<Double> {
    override fun isValidLiteral(token: String): Boolean = token.toDoubleOrNull() != null

    override fun parseLiteral(token: String): Double = token.toDouble()

    override fun multiply(a: Double, b: Double): Double = a * b

    override fun defaultResources(): Map<String, KevalOperator<Double>> = DEFAULT_RESOURCES

    private val DEFAULT_RESOURCES: Map<String, KevalOperator<Double>> = buildDefaultResources()

    private fun buildDefaultResources(): Map<String, KevalOperator<Double>> {
        val logical = BooleanLogic.operators(
            isTruthy = { it != 0.0 },
            trueValue = 1.0,
            falseValue = 0.0,
            compare = { a, b -> a.compareTo(b) },
        )
        return mapOf(
            "+" to KevalBothOperator(
                KevalBinaryOperator(2, true) { a, b -> a + b },
                KevalUnaryOperator(true) { it }
            ),
            "-" to KevalBothOperator(
                KevalBinaryOperator(2, true) { a, b -> a - b },
                KevalUnaryOperator(true) { -it }
            ),
            "/" to KevalBinaryOperator(3, true) { a, b ->
                if (b == 0.0) throw KevalZeroDivisionException()
                a / b
            },
            "%" to KevalBinaryOperator(3, true) { a, b ->
                if (b == 0.0) throw KevalZeroDivisionException()
                a % b
            },
            "^" to KevalBinaryOperator(4, false) { a, b -> a.pow(b) },
            "*" to KevalBinaryOperator(3, true) { a, b -> a * b },
            "!" to KevalUnaryOperator(false) {
                if (it < 0) throw KevalInvalidArgumentException("factorial of a negative number")
                if (floor(it) != it) throw KevalInvalidArgumentException("factorial of a non-integer")
                var result = 1.0
                for (i in 2..it.toInt()) {
                    result *= i
                }
                result
            },
            "neg" to KevalFunction(1) { -it[0] },
            "sign" to KevalFunction(1) { if (it[0] < 0) -1.0 else if (it[0] > 0) 1.0 else 0.0 },
            "abs" to KevalFunction(1) { it[0].absoluteValue },
            "sqrt" to KevalFunction(1) { sqrt(it[0]) },
            "cbrt" to KevalFunction(1) { cbrt(it[0]) },
            "nthrt" to KevalFunction(2) { it[1].pow(1 / it[0]) },
            "exp" to KevalFunction(1) { exp(it[0]) },
            "ln" to KevalFunction(1) { ln(it[0]) },
            "log10" to KevalFunction(1) { log10(it[0]) },
            "log2" to KevalFunction(1) { log2(it[0]) },
            "sin" to KevalFunction(1) { sin(it[0]) },
            "cos" to KevalFunction(1) { cos(it[0]) },
            "tan" to KevalFunction(1) { tan(it[0]) },
            "asin" to KevalFunction(1) { asin(it[0]) },
            "acos" to KevalFunction(1) { acos(it[0]) },
            "atan" to KevalFunction(1) { atan(it[0]) },
            "ceil" to KevalFunction(1) { ceil(it[0]) },
            "floor" to KevalFunction(1) { floor(it[0]) },
            "round" to KevalFunction(1) { round(it[0]) },
            "trunc" to KevalFunction(1) { it[0].toInt().toDouble() },
            "min" to KevalFunction(null) { it.min() },
            "max" to KevalFunction(null) { it.max() },
            "sum" to KevalFunction(null) { it.sum() },
            "avg" to KevalFunction(null) { it.average() },
            "median" to KevalFunction(null) { it.sorted()[it.size / 2] },
            "percentile" to KevalFunction(null) {
                if (it.size <= 1) throw KevalInvalidArgumentException("percentile requires at least 2 values")
                val perc = it[0]
                if (perc !in 0.0..100.0) throw KevalInvalidArgumentException("percentile must be between 0 and 100")
                val sorted = it.sorted()
                val index = ((perc / 100) * sorted.size).toInt()
                sorted[index]
            },
            "rand" to KevalFunction(null) {
                when (it.size) {
                    0 -> Random.Default.nextDouble()
                    1 -> (0..it[0].toInt()).random().toDouble()
                    else -> it.random()
                }
            },
            "randRange" to KevalFunction(3) {
                val start = it[0]
                val end = it[1]
                val step = it[2]
                if (step <= 0) throw KevalInvalidArgumentException("step must be greater than 0")
                val numberOfSteps = ((end - start) / step).toInt()
                val randomStepIndex = Random.nextInt(0, numberOfSteps + 1)
                start + randomStepIndex * step
            },
            "PI" to KevalConstant(PI),
            "e" to KevalConstant(E),
        ) + logical
    }
}
