package com.notkamui.keval

import com.notkamui.keval.framework.Resources
import com.notkamui.keval.framework.kevalLPA
import com.notkamui.keval.framework.kevalMul
import com.notkamui.keval.framework.kevalRPA

/**
 * Wrapper class for Keval.
 * Contains a companion object with the evaluation method
 */
class Keval(
    val generator: Resources.() -> Unit = { Resources.defaultOperators }
) {
    companion object {
        /**
         * Evaluates a mathematical expression to a double value
         *
         * @param mathExpression is the expression to evaluate
         * @return the value of the expression
         * @throws KevalInvalidOperatorException in case there's an invalid operator in the expression
         * @throws KevalInvalidExpressionException in case the expression is invalid (i.e. mismatched parenthesis)
         * @throws KevalZeroDivisionException in case of a zero division
         */
        fun eval(
            mathExpression: String,
        ): Double {
            return mathExpression.toAbstractSyntaxTree(Resources.defaultOperators).eval()
        }
    }

    fun eval(
        mathExpression: String,
    ): Double {
        val resources = Resources()
        resources.generator()

        val operators: Map<Char, BinaryOperator> = resources.operators
            // The method `tokenize` assumes multiplication, hence disallowing overriding `*` operator
            .plus('*' to BinaryOperator(::kevalMul, 3, true))
            // Whatever your operators are, `(`,`)` should always exists
            .plus('(' to BinaryOperator(::kevalRPA, 5, true))
            .plus(')' to BinaryOperator(::kevalLPA, 5, true))
        return mathExpression.toAbstractSyntaxTree(operators).eval()
    }
}

/**
 * Evaluates a mathematical expression to a double value
 *
 * @receiver is the expression to evaluate
 * @return the value of the expression
 * @throws KevalInvalidOperatorException in case there's an invalid operator in the expression
 * @throws KevalInvalidExpressionException in case the expression is invalid (i.e. mismatched parenthesis)
 * @throws KevalZeroDivisionException in case of a zero division
 */
fun String.keval(
    generator: Resources.() -> Unit
): Double {
    return Keval(generator).eval(this)
}

fun String.keval(): Double {
    return Keval.eval(this)
}
