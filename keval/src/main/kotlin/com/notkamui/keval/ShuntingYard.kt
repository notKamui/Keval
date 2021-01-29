package com.notkamui.keval

private fun MutableList<Node>.addOperator(operator: Operator): Boolean {
    val right = this.removeLastOrNull() ?: return false
    val left = this.removeLastOrNull() ?: return false
    this.add(OperatorNode(left, operator, right))
    return true
}

private fun checkPrecedence(topOperator: Operator, currentOperator: Operator): Boolean {
    val topIsStronger = topOperator.precedence > currentOperator.precedence
    val isLeftCompatible = topOperator.precedence == currentOperator.precedence && currentOperator.isLeftAssociative
    return topIsStronger || isLeftCompatible
}

private fun String.parseAsOperator(
    operatorStack: MutableList<String>,
    outputQueue: MutableList<Node>,
    tokensToString: String,
    currentPos: Int
) {
    if (operatorStack.isNotEmpty()) {
        val currentOperator = Operator[this[0]]
            ?: throw KevalInvalidOperatorException(this, tokensToString, currentPos)
        while (operatorStack.isNotEmpty()) {
            val topOperator = Operator[operatorStack.last()[0]]
                ?: throw KevalInvalidOperatorException(operatorStack.last(), tokensToString, currentPos)
            if (checkPrecedence(topOperator, currentOperator) && operatorStack.last() != "(") {
                val op = operatorStack.removeLast()
                if (!outputQueue.addOperator(
                        Operator[op[0]]
                            ?: throw KevalInvalidOperatorException(op, tokensToString, currentPos)
                    )
                ) throw KevalInvalidExpressionException(tokensToString, currentPos)
            } else {
                break
            }
        }
    }
    operatorStack.add(this)
}

private fun parseOnRightParenthesis(
    operatorStack: MutableList<String>,
    outputQueue: MutableList<Node>,
    tokensToString: String,
    currentPos: Int
) {
    try {
        while (operatorStack.last() != "(") {
            if (operatorStack.isEmpty()) throw KevalInvalidExpressionException(tokensToString, currentPos)
            val op = operatorStack.removeLast()
            if (!outputQueue.addOperator(
                    Operator[op[0]]
                        ?: throw KevalInvalidOperatorException(op, tokensToString, currentPos)
                )
            ) throw KevalInvalidExpressionException(tokensToString, currentPos)
        }
        if (operatorStack.last() == "(") operatorStack.removeLast()
    } catch (e: NoSuchElementException) {
        throw KevalInvalidExpressionException(tokensToString, currentPos)
    }
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
            token.isOperator() -> token.parseAsOperator(operatorStack, outputQueue, tokensToString, currentPos)
            token == "(" -> operatorStack.add(token)
            token == ")" -> parseOnRightParenthesis(operatorStack, outputQueue, tokensToString, currentPos)
        }
        currentPos += token.length
    }

    while (operatorStack.isNotEmpty()) {
        val op = operatorStack.removeLast()
        if (!outputQueue.addOperator(
                Operator[op[0]]
                    ?: throw KevalInvalidOperatorException(op, tokensToString, currentPos)
            )
        ) throw KevalInvalidExpressionException(tokensToString, currentPos)
    }

    return outputQueue.last()
}
