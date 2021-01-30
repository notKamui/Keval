package com.notkamui.keval

/**
 * Generic Keval Exception
 *
 * @param message is the message to display in the stacktrace
 */
sealed class KevalException(message: String) : Exception(message)

/**
 * Invalid Expression Exception. Is thrown when the expression is considered invalid
 * (i.e. Mismatched parenthesis or missing operands)
 *
 * @property expression is the invalid expression
 * @property position is the estimated position of the error
 */
open class KevalInvalidExpressionException(val expression: String, val position: Int) :
    KevalException("Invalid expression at $position in $expression")

/**
 * Invalid Operator Exception. Is thrown when an invalid/unknown operator is found
 *
 * @property invalidOperator is the given invalid operator
 * @param expression is the invalid expression
 * @param position is the estimated position of the error
 */
class KevalInvalidOperatorException(val invalidOperator: String, expression: String, position: Int) :
    KevalInvalidExpressionException(expression, position)

/**
 * Zero Division Exception. Is thrown when a zero division occurs
 * (i.e. x/0, x%0)
 */
class KevalZeroDivisionException : KevalException("Division by zero")
