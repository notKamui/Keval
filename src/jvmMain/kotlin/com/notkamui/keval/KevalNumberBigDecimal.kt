package com.notkamui.keval

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object KevalNumberBigDecimal : KevalNumber<BigDecimal> {
    val mathContext: MathContext = MathContext.DECIMAL128

    private val ZERO = BigDecimal.ZERO
    private val ONE = BigDecimal.ONE
    private val NEG_ONE = BigDecimal.valueOf(-1)

    override fun isValidLiteral(token: String): Boolean = try {
        BigDecimal(token)
        true
    } catch (_: NumberFormatException) {
        false
    }

    override fun parseLiteral(token: String): BigDecimal = BigDecimal(token)

    override fun defaultResources(): Map<String, KevalOperator<BigDecimal>> = mapOf(
        "+" to KevalBothOperator(
            KevalBinaryOperator(2, true) { a, b -> a.add(b) },
            KevalUnaryOperator(true) { it }
        ),
        "-" to KevalBothOperator(
            KevalBinaryOperator(2, true) { a, b -> a.subtract(b) },
            KevalUnaryOperator(true) { it.negate() }
        ),
        "/" to KevalBinaryOperator(3, true) { a, b ->
            if (b.compareTo(ZERO) == 0) throw KevalZeroDivisionException()
            a.divide(b, mathContext)
        },
        "%" to KevalBinaryOperator(3, true) { a, b ->
            if (b.compareTo(ZERO) == 0) throw KevalZeroDivisionException()
            a.remainder(b)
        },
        "^" to KevalBinaryOperator(4, false) { a, b ->
            if (b.stripTrailingZeros().scale() > 0) {
                throw KevalInvalidArgumentException("non-integer exponent")
            }
            val exp = b.intValueExact()
            when {
                exp >= 0 -> a.pow(exp)
                a.compareTo(ZERO) == 0 -> throw KevalInvalidArgumentException("zero to a negative power")
                else -> ONE.divide(a.pow(-exp), mathContext)
            }
        },
        "*" to KevalBinaryOperator(3, true) { a, b -> a.multiply(b) },

        "neg" to KevalFunction(1) { it[0].negate() },
        "abs" to KevalFunction(1) { it[0].abs() },
        "sign" to KevalFunction(1) {
            when (it[0].compareTo(ZERO)) {
                -1 -> NEG_ONE
                1 -> ONE
                else -> ZERO
            }
        },
        "min" to KevalFunction(null) { args -> args.minWithOrNull(compareBy { it })!! },
        "max" to KevalFunction(null) { args -> args.maxWithOrNull(compareBy { it })!! },
        "sum" to KevalFunction(null) { args -> args.fold(ZERO, BigDecimal::add) },
        "avg" to KevalFunction(null) { args ->
            args.fold(ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(args.size.toLong()), mathContext)
        },
        "ceil" to KevalFunction(1) { it[0].setScale(0, RoundingMode.CEILING) },
        "floor" to KevalFunction(1) { it[0].setScale(0, RoundingMode.FLOOR) },
        "round" to KevalFunction(1) { it[0].setScale(0, RoundingMode.HALF_UP) },
        "trunc" to KevalFunction(1) { it[0].setScale(0, RoundingMode.DOWN) },

        "bool" to KevalFunction(1) { booleanToDecimal(isTruthy(it[0])) },
        "not" to KevalFunction(1) { booleanToDecimal(!isTruthy(it[0])) },
        "and" to KevalFunction(null) { it.reduceBoolean { a, b -> a && b } },
        "nand" to KevalFunction(null) { it.reduceBoolean(true) { a, b -> a && b } },
        "or" to KevalFunction(null) { it.reduceBoolean { a, b -> a || b } },
        "nor" to KevalFunction(null) { it.reduceBoolean(true) { a, b -> a || b } },
        "xor" to KevalFunction(null) { it.reduceBoolean { a, b -> a xor b } },
        "xnor" to KevalFunction(null) { it.reduceBoolean(true) { a, b -> a xor b } },
        "imply" to KevalFunction(2) { booleanOperation(it) { a, b -> !a || b } },
        "nimply" to KevalFunction(2) { booleanOperation(it) { a, b -> a && !b } },
        "eq" to KevalFunction(null) { booleanToDecimal(it.all { e -> e.compareTo(it[0]) == 0 }) },
        "ne" to KevalFunction(null) {
            booleanToDecimal(it.map { e -> e.stripTrailingZeros() }.distinct().size == it.size)
        },
        "gt" to KevalFunction(2) { booleanToDecimal(it[0].compareTo(it[1]) > 0) },
        "lt" to KevalFunction(2) { booleanToDecimal(it[0].compareTo(it[1]) < 0) },
        "ge" to KevalFunction(2) { booleanToDecimal(it[0].compareTo(it[1]) >= 0) },
        "le" to KevalFunction(2) { booleanToDecimal(it[0].compareTo(it[1]) <= 0) },
    )

    private fun isTruthy(value: BigDecimal) = value.compareTo(ZERO) != 0
    private fun booleanToDecimal(value: Boolean) = if (value) ONE else ZERO
    private fun booleanOperation(array: List<BigDecimal>, operation: (Boolean, Boolean) -> Boolean) =
        booleanToDecimal(operation(isTruthy(array[0]), isTruthy(array[1])))
    private fun List<BigDecimal>.viaBoolean(operation: List<Boolean>.() -> Boolean) =
        booleanToDecimal(operation(map(::isTruthy)))
    private fun List<BigDecimal>.reduceBoolean(invert: Boolean = false, operation: (Boolean, Boolean) -> Boolean) =
        viaBoolean {
            reduce(operation).let {
                if (invert) !it
                else it
            }
        }
}

val KevalNumbers.BigDecimal: KevalNumber<BigDecimal>
    get() = KevalNumberBigDecimal

/**
 * Evaluates a mathematical expression using default BigDecimal resources.
 */
fun String.kevalBigDecimal(
    generator: KevalBuilder<BigDecimal>.() -> Unit = { includeDefault() }
): BigDecimal = Keval.create(KevalNumberBigDecimal, generator).eval(this)
