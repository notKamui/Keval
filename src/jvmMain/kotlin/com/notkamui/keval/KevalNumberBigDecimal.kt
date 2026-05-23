package com.notkamui.keval

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class KevalNumberBigDecimal private constructor(
    val mathContext: MathContext,
) : KevalNumber<BigDecimal> {

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

    override fun multiply(a: BigDecimal, b: BigDecimal): BigDecimal = a.multiply(b)

    override fun defaultResources(): Map<String, KevalOperator<BigDecimal>> = defaultResources

    private val defaultResources: Map<String, KevalOperator<BigDecimal>> by lazy { buildDefaultResources() }

    private fun buildDefaultResources(): Map<String, KevalOperator<BigDecimal>> {
        val logical = BooleanLogic.operators(
            isTruthy = { it.compareTo(ZERO) != 0 },
            trueValue = ONE,
            falseValue = ZERO,
            compare = { a, b -> a.compareTo(b) },
        )
        val arithmetic = mapOf<String, KevalOperator<BigDecimal>>(
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
        )
        return arithmetic + logical
    }

    companion object {
        val Default: KevalNumberBigDecimal = KevalNumberBigDecimal(MathContext.DECIMAL128)

        fun withContext(context: MathContext): KevalNumberBigDecimal = KevalNumberBigDecimal(context)
    }
}

val KevalNumbers.BigDecimal: KevalNumber<BigDecimal>
    get() = KevalNumberBigDecimal.Default

private val defaultBigDecimalKeval: Keval<BigDecimal> by lazy {
    Keval.create(KevalNumbers.BigDecimal) { includeDefault() }
}

fun String.kevalBigDecimal(
    generator: KevalBuilder<BigDecimal>.() -> Unit = { includeDefault() }
): BigDecimal = Keval.create(KevalNumbers.BigDecimal, generator).eval(this)

fun String.kevalBigDecimal(
    bindings: Map<String, BigDecimal>,
    generator: KevalBuilder<BigDecimal>.() -> Unit = { includeDefault() },
): BigDecimal = Keval.create(KevalNumbers.BigDecimal, generator).eval(this, bindings)

fun String.kevalBigDecimalOrNull(): BigDecimal? = defaultBigDecimalKeval.evalOrNull(this)

fun String.kevalBigDecimalResult(): Result<BigDecimal> = defaultBigDecimalKeval.evalResult(this)

fun String.compileBigDecimal(
    generator: KevalBuilder<BigDecimal>.() -> Unit = { includeDefault() },
): CompiledExpression<BigDecimal> = Keval.create(KevalNumbers.BigDecimal, generator).compile(this)
