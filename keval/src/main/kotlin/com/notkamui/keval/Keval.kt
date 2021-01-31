package com.notkamui.keval

import com.notkamui.keval.framework.kevalLPA
import com.notkamui.keval.framework.kevalMul
import com.notkamui.keval.framework.kevalRPA
import com.notkamui.keval.resources.KevalResources

/**
 * Wrapper class for Keval.
 * Contains a companion object with the evaluation method
 *
 * @property generator is an optional resource generator (defaults to built in operators)
 */
class Keval(
    val generator: KevalResources.() -> Unit = { +loadBuiltInOperators() }
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
        fun eval(mathExpression: String): Double {
            return mathExpression.toAbstractSyntaxTree(KevalResources().loadBuiltInOperators()).eval()
        }
    }

    /**
     * Evaluates a mathematical expression to a double value
     *
     * @param mathExpression is the expression to evaluate
     * @return the value of the expression
     * @throws KevalInvalidOperatorException in case there's an invalid operator in the expression
     * @throws KevalInvalidExpressionException in case the expression is invalid (i.e. mismatched parenthesis)
     * @throws KevalZeroDivisionException in case of a zero division
     */
    fun eval(mathExpression: String): Double {
        val resources = KevalResources()
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
 * @param generator is an optional resource generator (defaults to built in operators)
 * @return the value of the expression
 * @throws KevalInvalidOperatorException in case there's an invalid operator in the expression
 * @throws KevalInvalidExpressionException in case the expression is invalid (i.e. mismatched parenthesis)
 * @throws KevalZeroDivisionException in case of a zero division
 */
fun String.keval(
    generator: KevalResources.() -> Unit = { +loadBuiltInOperators() }
): Double = Keval(generator).eval(this)
