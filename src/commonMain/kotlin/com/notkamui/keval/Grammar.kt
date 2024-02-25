package com.notkamui.keval

internal class Parser(private val tokens: Iterator<String>, private val operators: Map<String, KevalOperator>) {
    private var currentTokenOrNull: String? = tokens.next()
    private val currentToken: String
        get() = currentTokenOrNull ?: throw KevalInvalidExpressionException("", -1)

    private fun consume(expected: String) {
        if (currentTokenOrNull != expected) {
            throw KevalInvalidExpressionException(currentTokenOrNull ?: "", -1)
        }
        currentTokenOrNull = if (tokens.hasNext()) tokens.next() else null
    }

    private fun isBinaryOrBoth(token: String): Boolean = operators[token].let {
        it is KevalBinaryOperator || it is KevalBothOperator
    }

    private fun isUnaryOrBothPrefix(token: String): Boolean = operators[token].let {
        (it is KevalUnaryOperator && it.isPrefix) || (it is KevalBothOperator && it.unary.isPrefix)
    }

    private fun isUnaryOrBothPostfix(token: String): Boolean = operators[token].let {
        (it is KevalUnaryOperator && !it.isPrefix) || (it is KevalBothOperator && !it.unary.isPrefix)
    }

    private fun getBinaryOperator(token: String): KevalBinaryOperator = operators[token].let {
        if (it is KevalBothOperator) {
            it.binary
        } else {
            it as KevalBinaryOperator
        }
    }

    private fun getUnaryOperator(token: String): KevalUnaryOperator = operators[token].let {
        if (it is KevalBothOperator) {
            it.unary
        } else {
            it as KevalUnaryOperator
        }
    }

    private fun handleBinaryOperator(node: Node, minPrecedence: Int): Node {
        var result = node
        while (currentTokenOrNull != null && isBinaryOrBoth(currentToken)) {
            val op = getBinaryOperator(currentToken)
            if (op.precedence < minPrecedence) break
            consume(currentToken)
            val rightAssociativity = if (op.isLeftAssociative) 1 else 0
            result = BinaryOperatorNode(result, op.implementation, expression(op.precedence + rightAssociativity))
        }
        return result
    }

    private fun handleUnaryOperator(node: Node? = null): Node {
        val op = getUnaryOperator(currentToken)
        consume(currentToken)
        return UnaryOperatorNode(op.implementation, node ?: primary())
    }

    private fun handleFunction(): Node {
        val functionName = currentToken
        consume(functionName)
        consume("(")
        val args = mutableListOf<Node>()
        while (currentTokenOrNull != ")") {
            args.add(expression())
            if (currentTokenOrNull == ",") {
                consume(",")
            }
        }
        consume(")")
        val op = operators[functionName] as KevalFunction
        if (args.size != op.arity) {
            throw KevalInvalidExpressionException(currentTokenOrNull ?: "", -1)
        }
        return FunctionNode(op.implementation, args)
    }

    private fun handleConstant(): Node {
        val op = operators[currentToken] as KevalConstant
        consume(currentToken)
        return ValueNode(op.value)
    }

    private fun expression(minPrecedence: Int = 0): Node {
        var node = primary()
        node = handleBinaryOperator(node, minPrecedence)
        if (currentTokenOrNull != null && isUnaryOrBothPostfix(currentToken)) {
            node = handleUnaryOperator(node)
        }
        return node
    }

    private fun primary(): Node {
        if (currentTokenOrNull != null && isUnaryOrBothPrefix(currentToken)) {
            return handleUnaryOperator()
        } else if (currentTokenOrNull == "(") {
            consume("(")
            val node = expression()
            consume(")")
            return node
        } else if (operators.containsKey(currentTokenOrNull)) {
            val op = operators[currentToken]
            if (op is KevalFunction) {
                return handleFunction()
            } else if (op is KevalConstant) {
                return handleConstant()
            }
        }
        val node = ValueNode(currentToken.toDouble())
        consume(currentToken)
        return node
    }

    fun parse(): Node = expression()
}

/**
 * Converts an infix mathematical expression into an abstract syntax tree,
 * The operators that are supported are defined in the operators map, which each have a precedence and associativity.
 *
 * @receiver the string to convert
 * @return the abstract syntax tree
 * @throws KevalInvalidSymbolException if the expression contains an invalid symbol
 * @throws KevalInvalidExpressionException if the expression is invalid (i.e. mismatched parenthesis, missing operand, or empty expression)
 */
internal fun String.toAST(operators: Map<String, KevalOperator>): Node {
    if (this.replace("""[()]""".toRegex(), "").isBlank())
        throw KevalInvalidExpressionException("", -1)

    val tokens = this.tokenize(operators)
    val tokensToString = tokens.joinToString("")

    val parser = Parser(tokens.iterator(), operators)
    return parser.parse()
}