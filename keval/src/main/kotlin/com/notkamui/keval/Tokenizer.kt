package com.notkamui.keval

/**
 * The different token types
 */
private enum class TokenType {
    FIRST,
    OPERAND,
    OPERATOR,
    LPAREN,
    RPAREN,
}

private fun shouldAutoMul(tokenType: TokenType): Boolean =
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
 * Checks if a string is an operator or not (neither LPAREN nor RPAREN are operators in this context
 *
 * @receiver is the string to check
 * @return true if the string is a valid operator, false otherwise
 */
internal fun String.isOperator(): Boolean = this in kevalSymbols() && this !in "()"

/**
 * Tokenizes a mathematical expression
 *
 * @receiver is the string to tokenize
 * @return the list of tokens
 * @throws KevalInvalidOperatorException if the expression contains an invalid operator
 */
internal fun String.tokenize(): List<String> {
    val symbols = kevalSymbols()
    val sanitized = this.replace("\\s".toRegex(), "")
    val tokens = sanitized.split("(?<=[$symbols])|(?=[$symbols])".toRegex())
    val tokensToString = tokens.joinToString("")

    var currentPos = 0
    var prevToken = TokenType.FIRST
    val ret = mutableListOf<String>()
    tokens.filter { it.isNotEmpty() }.forEach { token ->
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
            else -> throw KevalInvalidOperatorException(token, tokensToString, currentPos)
        }
        ret.add(token)
        currentPos += token.length
    }

    return ret
}
