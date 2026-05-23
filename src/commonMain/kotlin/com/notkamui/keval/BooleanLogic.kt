package com.notkamui.keval

internal object BooleanLogic {

    fun <N> operators(
        isTruthy: (N) -> Boolean,
        trueValue: N,
        falseValue: N,
        compare: (N, N) -> Int,
    ): Map<String, KevalOperator<N>> {
        fun toBool(value: Boolean) = if (value) trueValue else falseValue

        fun List<N>.reduceBoolean(invert: Boolean = false, operation: (Boolean, Boolean) -> Boolean): N =
            toBool(
                map(isTruthy).reduce(operation).let { if (invert) !it else it }
            )

        fun booleanOperation(array: List<N>, operation: (Boolean, Boolean) -> Boolean): N =
            toBool(operation(isTruthy(array[0]), isTruthy(array[1])))

        fun allEqual(args: List<N>): Boolean =
            args.all { compare(it, args[0]) == 0 }

        fun allDistinct(args: List<N>): Boolean =
            args.indices.all { i ->
                args.indices.all { j -> i == j || compare(args[i], args[j]) != 0 }
            }

        return mapOf(
            "bool" to KevalFunction(1) { toBool(isTruthy(it[0])) },
            "not" to KevalFunction(1) { toBool(!isTruthy(it[0])) },
            "and" to KevalFunction(null) { it.reduceBoolean { a, b -> a && b } },
            "nand" to KevalFunction(null) { it.reduceBoolean(true) { a, b -> a && b } },
            "or" to KevalFunction(null) { it.reduceBoolean { a, b -> a || b } },
            "nor" to KevalFunction(null) { it.reduceBoolean(true) { a, b -> a || b } },
            "xor" to KevalFunction(null) { it.reduceBoolean { a, b -> a xor b } },
            "xnor" to KevalFunction(null) { it.reduceBoolean(true) { a, b -> a xor b } },
            "imply" to KevalFunction(2) { booleanOperation(it) { a, b -> !a || b } },
            "nimply" to KevalFunction(2) { booleanOperation(it) { a, b -> a && !b } },
            "eq" to KevalFunction(null) { toBool(allEqual(it)) },
            "ne" to KevalFunction(null) { toBool(allDistinct(it)) },
            "gt" to KevalFunction(2) { toBool(compare(it[0], it[1]) > 0) },
            "lt" to KevalFunction(2) { toBool(compare(it[0], it[1]) < 0) },
            "ge" to KevalFunction(2) { toBool(compare(it[0], it[1]) >= 0) },
            "le" to KevalFunction(2) { toBool(compare(it[0], it[1]) <= 0) },
        )
    }
}
