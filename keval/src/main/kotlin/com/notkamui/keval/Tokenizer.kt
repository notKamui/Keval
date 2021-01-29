package com.notkamui.keval

/**
 * The different token types
 */
internal enum class TokenType {
    FIRST,
    OPERAND,
    OPERATOR,
    LPAREN,
    RPAREN,
}

/**
 * Checks if an auto multiplication (*) insert is possible after a given token type
 *
 * @param tokenType is the type of the previous token
 * @return true if auto multiplication is possible, false otherwise
 */
internal fun shouldAutoMul(tokenType: TokenType): Boolean =
    tokenType == TokenType.OPERAND || tokenType == TokenType.RPAREN

/**
 * Checks if a string is numeric or not
 *
 * @receiver is the string to check
 * @return true if the string is numeric, false otherwise
 */
internal fun String.isNumeric(): Boolean {
    this.toDoubleOrNull() ?: return false
    return true
}

/**
 * Checks if a string is an operator or not
 *
 * @receiver is the string to check
 * @return true if the string is a valid operator, false otherwise
 */
internal fun String.isOperator(): Boolean = this in Operator.symbols()

/**
 * Tokenizes a mathematical expression
 *
 * @receiver is the string to tokenize
 * @return the list of tokens
 * @throws InvalidOperatorException if the expression contains an invalid operator
 */
internal fun String.tokenize(): List<String> {
    val symbols = Operator.symbols()
    val tokens = this.split("(?<=[$symbols()])|(?=[$symbols()])".toRegex())

    var prevToken = TokenType.FIRST
    val ret = mutableListOf<String>()
    tokens.filter { it.isNotEmpty() }.forEachIndexed { i, token ->
        prevToken = when {
            token.isNumeric() -> {
                if (shouldAutoMul(prevToken))
                    ret.add("*")
                TokenType.OPERAND
            }
            token.isOperator() -> TokenType.OPERATOR
            token == "(" -> {
                if (shouldAutoMul(prevToken))
                    ret.add("*")
                TokenType.LPAREN
            }
            token == ")" -> TokenType.RPAREN
            else -> throw InvalidOperatorException(token, i)
        }
        ret.add(token)
    }

    return ret
}
