package com.notkamui.keval

private fun String.pluralize(count: Int): String = if (count == 1) this else "${this}s"

internal class Parser<N>(
    private val number: KevalNumber<N>,
    private val tokens: Iterator<String>,
    private val tokensToString: String,
    private val operators: Map<String, KevalOperator<N>>
) {
    private var currentTokenOrNull: String? = tokens.next()
    private val currentToken: String
        get() = currentTokenOrNull ?: throw KevalMalformedExpressionException(tokensToString, -1)

    private var currentPos = 0
    private var openParenthesesCount = 0

    private fun consume(expected: String) {
        if (currentTokenOrNull != expected) {
            throw KevalMalformedExpressionException(
                tokensToString,
                currentPos,
                "expected $expected but found ${currentTokenOrNull ?: "end of expression"}",
            )
        }

        if (currentToken == "(") {
            openParenthesesCount++
        } else if (currentToken == ")") {
            if (openParenthesesCount == 0) {
                throw KevalMalformedExpressionException(
                    tokensToString,
                    currentPos,
                    "unexpected closing parenthesis"
                )
            }
            openParenthesesCount--
        }
        currentPos += currentTokenOrNull?.length ?: 0 // Update the current position
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

    private fun getBinaryOperator(token: String): KevalBinaryOperator<N> = operators[token].let {
        if (it is KevalBothOperator) {
            it.binary
        } else {
            it as KevalBinaryOperator
        }
    }

    private fun getUnaryOperator(token: String): KevalUnaryOperator<N> = operators[token].let {
        if (it is KevalBothOperator) {
            it.unary
        } else {
            it as KevalUnaryOperator
        }
    }

    private fun handleBinaryOperator(node: Node<N>, minPrecedence: Int): Node<N> {
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

    private fun handleUnaryOperator(node: Node<N>? = null): Node<N> {
        val op = getUnaryOperator(currentToken)
        consume(currentToken)
        return UnaryOperatorNode(op.implementation, node ?: primary())
    }

    private fun handleFunction(): Node<N> {
        val functionName = currentToken
        consume(functionName)
        val op = operators[functionName] as KevalFunction
        consume("(")
        val args = mutableListOf<Node<N>>()
        while (currentTokenOrNull != ")") {
            args.add(expression())
            if (op.arity != null && args.size > op.arity) {
                throw KevalMalformedExpressionException(
                    tokensToString,
                    currentPos,
                    "expected ${op.arity} ${"argument".pluralize(op.arity)} but found ${args.size}",
                )
            }
            if (currentTokenOrNull == ",") {
                consume(",")
            }
        }
        consume(")")
        if (op.arity != null && args.size < op.arity) {
            throw KevalMalformedExpressionException(
                tokensToString,
                currentPos,
                "expected ${op.arity} ${"argument".pluralize(op.arity)} but found ${args.size}",
            )
        }
        return createFunctionNode(op.implementation, args)
    }

    private fun handleConstant(): Node<N> {
        val op = operators[currentToken] as KevalConstant
        consume(currentToken)
        return ValueNode(op.value)
    }

    private fun expression(minPrecedence: Int = 0): Node<N> {
        var node = primary()
        while (currentTokenOrNull != null && isUnaryOrBothPostfix(currentToken)) {
            node = handleUnaryOperator(node)
        }
        node = handleBinaryOperator(node, minPrecedence)
        return node
    }

    private fun primary(): Node<N> {
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
        val token = currentToken
        if (number.isValidLiteral(token)) {
            consume(currentToken)
            return ValueNode(number.parseLiteral(token))
        }
        if (token.isIdentifierName()) {
            consume(currentToken)
            return VariableNode(token)
        }
        throw KevalMalformedExpressionException(
            tokensToString,
            currentPos,
            "expected number or symbol but found $token",
        )
    }

    fun parse(): Node<N> {
        val node = expression()
        if (currentTokenOrNull != null) {
            throw KevalMalformedExpressionException(
                tokensToString,
                currentPos,
                "unexpected token $currentTokenOrNull"
            )
        }
        return node
    }
}

/**
 * Converts an infix mathematical expression into an abstract syntax tree,
 * The operators that are supported are defined in the operators map, which each have a precedence and associativity.
 *
 * @receiver the string to convert
 * @return the abstract syntax tree
 * @throws KevalInvalidSymbolException if the expression contains an invalid symbol
 * @throws KevalMalformedExpressionException if the expression is invalid (i.e. mismatched parenthesis, missing operand, or empty expression)
 */
internal fun <N> String.toAST(
    number: KevalNumber<N>,
    operators: Map<String, KevalOperator<N>>,
): Node<N> {
    if (this.replace("""[()]""".toRegex(), "").isBlank())
        throw KevalMalformedExpressionException("", -1)

    val tokens = this.tokenize(number, operators)
    val tokensToString = tokens.joinToString("")

    val parser = Parser(number, tokens.iterator(), tokensToString, operators)
    return parser.parse()
}
