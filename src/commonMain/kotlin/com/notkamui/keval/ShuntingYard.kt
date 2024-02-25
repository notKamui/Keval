package com.notkamui.keval

// pops two operators and add them as operator node
private fun MutableList<Node>.addOperator(operator: (Double, Double) -> Double): Boolean {
    val right = removeLastOrNull() ?: return false
    val left = removeLastOrNull() ?: return false
    add(BinaryOperatorNode(left, operator, right))
    return true
}

// pops $arity operators and add them as function node
private fun MutableList<Node>.addFunction(arity: Int, func: (DoubleArray) -> Double): Boolean {
    val children = mutableListOf<Node>()
    repeat(arity) {
        children += removeLastOrNull() ?: return false
    }
    add(FunctionNode(func, children.reversed()))
    return true
}

// pops a constant and add it as constant node
private fun MutableList<Node>.addConstant(value: Double): Boolean {
    add(ValueNode(value))
    return true
}

private fun Boolean.throwIfInvalid(token: String, pos: Int) {
    if (!this) throw KevalInvalidExpressionException(token, pos)
}

// checks the type of the given operator to add it
private fun MutableList<Node>.offerOperator(
    operatorStack: MutableList<String>,
    tokensToString: String,
    currentPos: Int,
    operators: Map<String, KevalOperator>
) {
    val op = operators[operatorStack.removeLast()]
        ?: throw KevalInvalidExpressionException(tokensToString, currentPos)

    when (op) {
        is KevalBinaryOperator -> addOperator(op.implementation)
        is KevalFunction -> addFunction(op.arity, op.implementation)
        is KevalConstant -> addConstant(op.value)
        is KevalUnaryOperator -> TODO()
        is KevalBothOperator -> TODO()
    }.throwIfInvalid(tokensToString, currentPos)
}

// complex precedence (priority) check
private fun checkPrecedence(topOperator: KevalBinaryOperator, currentOperator: KevalBinaryOperator): Boolean {
    val topIsStronger = topOperator.precedence > currentOperator.precedence
    val isLeftCompatible = topOperator.precedence == currentOperator.precedence && currentOperator.isLeftAssociative
    return topIsStronger || isLeftCompatible
}

private fun String.parseAsOperator(
    operatorStack: MutableList<String>,
    outputQueue: MutableList<Node>,
    tokensToString: String,
    currentPos: Int,
    operators: Map<String, KevalOperator>
) {
    val binaryOperators = mutableMapOf<String, KevalBinaryOperator>()
    operators.map { entry ->
        if (entry.value is KevalBinaryOperator)
            binaryOperators += entry.key to entry.value as KevalBinaryOperator
    }

    if (operatorStack.isNotEmpty()) {
        val currentOperator = binaryOperators[this]
            ?: throw KevalInvalidSymbolException(this, tokensToString, currentPos)

        // repeat until the stack is empty or the current token is not a binary operator
        while (operatorStack.isNotEmpty() && operatorStack.last().isBinaryOperator(operators)) {
            val last = operatorStack.last()
            val topOperator = binaryOperators[last]
                ?: throw KevalInvalidSymbolException(last, tokensToString, currentPos)

            if (checkPrecedence(topOperator, currentOperator) && last != "(") {
                outputQueue.offerOperator(operatorStack, tokensToString, currentPos, binaryOperators)
            } else break
        }
    }
    operatorStack.add(this)
}

private fun parseOnRightParenthesis(
    operatorStack: MutableList<String>,
    outputQueue: MutableList<Node>,
    tokensToString: String,
    currentPos: Int,
    operators: Map<String, KevalOperator>
) {
    try {
        while (operatorStack.last() != "(") {
            if (operatorStack.isEmpty())
                throw KevalInvalidExpressionException(tokensToString, currentPos)
            outputQueue.offerOperator(operatorStack, tokensToString, currentPos, operators)
        }
        if (operatorStack.isNotEmpty() && operatorStack.last() == "(")
            operatorStack.removeLast()
        if (operatorStack.isNotEmpty() && operatorStack.last().isFunction(operators))
            outputQueue.offerOperator(operatorStack, tokensToString, currentPos, operators)
    } catch (e: NoSuchElementException) {
        throw KevalInvalidExpressionException(tokensToString, currentPos)
    }
}

private fun String.isFunction(operators: Map<String, KevalOperator>) =
    isKevalOperator(operators.keys) && operators[this] is KevalFunction

private fun String.isBinaryOperator(operators: Map<String, KevalOperator>) =
    isKevalOperator(operators.keys) && operators[this] is KevalBinaryOperator

private fun String.isConstant(operators: Map<String, KevalOperator>) =
    isKevalOperator(operators.keys) && operators[this] is KevalConstant

/**
 * Converts an infix mathematical expression into an abstract syntax tree,
 * Uses the Shunting-yard algorithm, by Edsger Dijkstra
 *
 * @receiver the string to convert
 * @return the abstract syntax tree
 * @throws KevalInvalidSymbolException if the expression contains an invalid symbol
 * @throws KevalInvalidExpressionException if the expression is invalid (i.e. mismatched parenthesis, missing operand, or empty expression)
 */
internal fun String.toAbstractSyntaxTree(operators: Map<String, KevalOperator>): Node {
    // Check if empty expression
    if (this.replace("""[()]""".toRegex(), "").isBlank())
        throw KevalInvalidExpressionException("", -1)

    val outputQueue = mutableListOf<Node>()
    val operatorStack = mutableListOf<String>()
    val tokens = this.tokenize(operators)
    val tokensToString = tokens.joinToString("")
    var currentPos = 0

    tokens.forEachIndexed { i, token ->
        when {
            token.isNumeric() -> outputQueue.add(ValueNode(token.toDouble()))
            token.isConstant(operators) -> {
                operatorStack.add(token)
                outputQueue.offerOperator(operatorStack, tokensToString, currentPos, operators)
            }
            token.isFunction(operators) -> {
                if (tokens[i + 1] != "(")
                    throw KevalInvalidExpressionException(tokensToString, currentPos)
                operatorStack.add(token)
            }
            token.isBinaryOperator(operators) -> token.parseAsOperator(
                operatorStack,
                outputQueue,
                tokensToString,
                currentPos,
                operators
            )
            token == "(" -> operatorStack.add(token)
            token == ")" -> parseOnRightParenthesis(
                operatorStack,
                outputQueue,
                tokensToString,
                currentPos,
                operators
            )
        }
        currentPos += token.length
    }

    while (operatorStack.isNotEmpty()) {
        outputQueue.offerOperator(operatorStack, tokensToString, currentPos, operators)
    }

    return outputQueue.last()
}
