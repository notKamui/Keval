package com.notkamui.keval

internal class Parser(private val tokens: Iterator<String>, private val operators: Map<String, KevalOperator>) {
    private var currentToken: String? = tokens.next()

    private fun consume(expected: String) {
        if (currentToken != expected) {
            throw KevalInvalidExpressionException(currentToken ?: "", -1)
        }
        currentToken = if (tokens.hasNext()) tokens.next() else null
    }

    private fun expression(minPrecedence: Int = 0): Node {
        var node = primary()
        while (currentToken != null && operators[currentToken!!] is KevalBinaryOperator) {
            val op = operators[currentToken!!] as KevalBinaryOperator
            if (op.precedence < minPrecedence) break
            consume(currentToken!!)
            val rightAssociativity = if (op.isLeftAssociative) 1 else 0
            node = BinaryOperatorNode(node, op.implementation, expression(op.precedence + rightAssociativity))
        }
        return node
    }

    private fun primary(): Node {
        if (currentToken == "(") {
            consume("(")
            val node = expression()
            consume(")")
            return node
        } else if (operators.containsKey(currentToken)) {
            val op = operators[currentToken!!]
            if (op is KevalFunction) {
                val functionName = currentToken!!
                consume(functionName)
                consume("(")
                val args = mutableListOf<Node>()
                while (currentToken != ")") {
                    args.add(expression())
                    if (currentToken == ",") {
                        consume(",")
                    }
                }
                consume(")")
                if (args.size != op.arity) {
                    throw KevalInvalidExpressionException(currentToken ?: "", -1)
                }
                return FunctionNode(op.implementation, args)
            }
        }
        val node = ValueNode(currentToken!!.toDouble())
        consume(currentToken!!)
        return node
    }

    fun parse(): Node = expression()
}

/**
 * Converts an infix mathematical expression into an abstract syntax tree,
 * The operators that are supported are defined in the operators map, which each have a precedence and associativity.
 *
 * Here is the following simplified dynamic grammar:
 *
 * expression = term | expression "+" term | expression "-" term
 * term = factor | term "*" factor | term "/" factor | term "%" factor
 * factor = unary | factor "^" unary
 * unary = primary | "-" unary | "+" unary
 * primary = number | "(" expression ")"
 *
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
