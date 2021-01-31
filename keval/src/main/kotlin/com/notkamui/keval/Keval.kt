package com.notkamui.keval

import com.notkamui.keval.framework.KevalLPA
import com.notkamui.keval.framework.KevalMul
import com.notkamui.keval.framework.KevalRPA
import com.notkamui.keval.framework.Resources

/**
 * Wrapper class for Keval.
 * Contains a companion object with the evaluation method
 */
class Keval(
        val generator: Resources.() -> Unit = { +loadBuiltInOperators() }
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
            return mathExpression.toAbstractSyntaxTree(Resources().loadBuiltInOperators()).eval()
        }
    }

    fun eval(
            mathExpression: String,
    ): Double {
        val resources = Resources()
        resources.generator()


        val operators: Map<Char, BinaryOperator> = resources.operators
                // The method `tokenize` assumes multiplication, hence disallowing overriding `*` operator
                .plus('*' to BinaryOperator(::KevalMul, 3, true))
                // Whatever your operators are, `(`,`)` should always exists
                .plus('(' to BinaryOperator(::KevalRPA, 5, true))
                .plus(')' to BinaryOperator(::KevalLPA, 5, true))
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
        generator: Resources.() -> Unit = { +loadBuiltInOperators() }
): Double {
    return Keval(generator).eval(this)
}