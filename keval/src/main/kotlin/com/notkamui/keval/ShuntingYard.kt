package com.notkamui.keval

/**
 * Adds an operator node on top of an output stack by eating the two previous top nodes
 *
 * @param operator is the operator to add
 * @receiver is the output stack on which to add the operator node
 * @return false if there's a missing operand, true if everything went fine
 */
private fun MutableList<Node>.addOperator(operator: Operator): Boolean {
    val right = this.removeLastOrNull() ?: return false
    val left = this.removeLastOrNull() ?: return false
    this.add(OperatorNode(left, operator, right))
    return true
}

/**
 * Converts a infix mathematical expression into an abstract syntax tree.
 * Uses the Shunting-yard algorithm, by Edsger Dijkstra
 *
 * @receiver the string to convert
 * @return the abstract syntax tree
 * @throws KevalInvalidOperatorException if the expression contains an invalid operator
 * @throws KevalInvalidExpressionException if the expression is invalid (i.e. mismatched parenthesis or missing operand)
 */
internal fun String.toAbstractSyntaxTree(): Node {
    val outputQueue = mutableListOf<Node>()
    val operatorStack = mutableListOf<String>()
    val tokens = this.tokenize()
    val tokensToString = tokens.joinToString("")
    var currentPos = 0

    tokens.forEach { token ->
        when {
            token.isNumeric() -> outputQueue.add(ValueNode(token.toDouble()))
            token.isOperator() -> {
                if (operatorStack.isNotEmpty()) {
                    val currentOperator = Operator[token[0]]
                        ?: throw KevalInvalidOperatorException(token, tokensToString, currentPos)
                    while (operatorStack.isNotEmpty()) {
                        val topOperator = Operator[operatorStack.last()[0]]
                            ?: throw KevalInvalidOperatorException(operatorStack.last(), tokensToString, currentPos)
                        if (
                            (
                                topOperator.precedence > currentOperator.precedence || (
                                    topOperator.precedence == currentOperator.precedence && currentOperator.isLeftAssociative
                                    )
                                ) && operatorStack.last() != "("
                        ) {
                            val op = operatorStack.removeLast()
                            if (!outputQueue.addOperator(
                                    Operator[op[0]] ?: throw KevalInvalidOperatorException(
                                            op,
                                            tokensToString,
                                            currentPos
                                        )
                                )
                            )
                                throw KevalInvalidExpressionException(tokensToString, currentPos)
                        } else {
                            break
                        }
                    }
                }
                operatorStack.add(token)
            }
            token == "(" -> operatorStack.add(token)
            token == ")" -> {
                try {
                    while (operatorStack.last() != "(") {
                        if (operatorStack.isEmpty()) throw KevalInvalidExpressionException(tokensToString, currentPos)
                        val op = operatorStack.removeLast()
                        if (!outputQueue.addOperator(
                                Operator[op[0]] ?: throw KevalInvalidOperatorException(
                                        op,
                                        tokensToString,
                                        currentPos
                                    )
                            )
                        )
                            throw KevalInvalidExpressionException(tokensToString, currentPos)
                    }
                    if (operatorStack.last() == "(") operatorStack.removeLast()
                } catch (e: NoSuchElementException) {
                    throw KevalInvalidExpressionException(tokensToString, currentPos)
                }
            }
        }
        currentPos += token.length
    }

    while (operatorStack.isNotEmpty()) {
        val op = operatorStack.removeLast()
        if (!outputQueue.addOperator(
                Operator[op[0]] ?: throw KevalInvalidOperatorException(
                        op,
                        tokensToString,
                        currentPos
                    )
            )
        )
            throw KevalInvalidExpressionException(tokensToString, currentPos)
    }

    return outputQueue.last()
}
