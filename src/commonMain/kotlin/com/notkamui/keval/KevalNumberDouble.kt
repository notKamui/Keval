package com.notkamui.keval

import kotlin.math.*
import kotlin.random.Random

object KevalNumberDouble : KevalNumber<Double> {
    override fun isValidLiteral(token: String): Boolean = token.toDoubleOrNull() != null

    override fun parseLiteral(token: String): Double = token.toDouble()

    override fun defaultResources(): Map<String, KevalOperator<Double>> = mapOf(
        // binary operators
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

        // unary operators
        "!" to KevalUnaryOperator(false) {
            if (it < 0) throw KevalInvalidArgumentException("factorial of a negative number")
            if (floor(it) != it) throw KevalInvalidArgumentException("factorial of a non-integer")
            var result = 1.0
            for (i in 2..it.toInt()) {
                result *= i
            }
            result
        },

        // functions
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

            if (step > 0) throw KevalInvalidArgumentException("step must be greater than 0")
            val numberOfSteps = ((end - start) / step).toInt()
            val randomStepIndex = Random.nextInt(0, numberOfSteps + 1)
            start + randomStepIndex * step
        },

        // logical functions
        "bool" to KevalFunction(1) { booleanToDouble(it[0] != 0.0) },
        "not" to KevalFunction(1) { booleanToDouble(!doubleToBoolean(it[0])) },
        "and" to KevalFunction(null) { it.reduceBoolean { a, b -> a && b } },
        "nand" to KevalFunction(null) { it.reduceBoolean(true) { a, b -> a && b } },
        "or" to KevalFunction(null) { it.reduceBoolean { a, b -> a || b } },
        "nor" to KevalFunction(null) { it.reduceBoolean(true) { a, b -> a || b } },
        "xor" to KevalFunction(null) { it.reduceBoolean { a, b -> a xor b } },
        "xnor" to KevalFunction(null) { it.reduceBoolean(true) { a, b -> a xor b } },
        "imply" to KevalFunction(2) { booleanOperation(it) { a, b -> !a || b } },
        "nimply" to KevalFunction(2) { booleanOperation(it) { a, b -> a && !b } },
        "eq" to KevalFunction(null) { booleanToDouble(it.all { e -> e == it[0] }) },
        "ne" to KevalFunction(null) { booleanToDouble(it.distinct().size == it.size) },
        "gt" to KevalFunction(2) { booleanToDouble(it[0] > it[1]) },
        "lt" to KevalFunction(2) { booleanToDouble(it[0] < it[1]) },
        "ge" to KevalFunction(2) { booleanToDouble(it[0] >= it[1]) },
        "le" to KevalFunction(2) { booleanToDouble(it[0] <= it[1]) },

        // constants
        "PI" to KevalConstant(PI),
        "e" to KevalConstant(E)
    )

    private fun doubleToBoolean(value: Double) = value != 0.0
    private fun booleanToDouble(value: Boolean) = if (value) 1.0 else 0.0
    private fun booleanOperation(array: List<Double>, operation: (Boolean, Boolean) -> Boolean) =
        booleanToDouble(operation(doubleToBoolean(array[0]), doubleToBoolean(array[1])))
    private fun List<Double>.viaBoolean(operation: List<Boolean>.() -> Boolean) =
        booleanToDouble(operation(map(::doubleToBoolean)))
    private fun List<Double>.reduceBoolean(invert: Boolean = false, operation: (Boolean, Boolean) -> Boolean) =
        viaBoolean {
            reduce(operation).let {
                if (invert) !it
                else it
            }
        }
}
