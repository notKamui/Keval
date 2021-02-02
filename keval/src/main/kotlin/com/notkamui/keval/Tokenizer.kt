package com.notkamui.keval

private enum class TokenType {
    FIRST,
    OPERAND,
    OPERATOR,
    LPAREN,
    RPAREN,
    COMMA,
}

private fun shouldAssumeMul(tokenType: TokenType): Boolean =
    tokenType == TokenType.OPERAND || tokenType == TokenType.RPAREN

// This functions add product symbols where they should be assumed
private fun List<String>.assumeMul(symbolsSet: Set<String>, tokensToString: String): List<String> {
    var currentPos = 0
    var prevToken = TokenType.FIRST
    val ret = mutableListOf<String>()
    this.forEach { token ->
        prevToken = when {
            token.isNumeric() -> {
                if (shouldAssumeMul(prevToken))
                    ret.add("*")
                TokenType.OPERAND
            }
            token.isKevalOperator(symbolsSet) -> TokenType.OPERATOR
            token == "(" -> {
                if (shouldAssumeMul(prevToken))
                    ret.add("*")
                TokenType.LPAREN
            }
            token == ")" -> TokenType.RPAREN
            token == "," -> TokenType.COMMA
            else -> throw KevalInvalidOperatorException(token, tokensToString, currentPos)
        }
        ret.add(token)
        currentPos += token.length
    }
    return ret
}

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
 * Checks if a string is a Keval Operator or not
 *
 * @receiver is the string to check
 * @return true if the string is a valid operator, false otherwise
 */
internal fun String.isKevalOperator(symbolsSet: Set<String>): Boolean = this in symbolsSet

/**
 * Tokenizes a mathematical expression
 *
 * @receiver is the string to tokenize
 * @return the list of tokens
 * @throws KevalInvalidOperatorException if the expression contains an invalid operator
 */
internal fun String.tokenize(symbolsSet: Set<String>): List<String> {
    val limits = """ |[^a-zA-Z0-9._]|,|\(|\)"""
    val tokens = this
        .split("""(?<=($limits))|(?=($limits))""".toRegex()) // tokenizing
        .filter { it.isNotBlank() } // removing possible empty tokens
        .map { it.replace("\\s".toRegex(), "") } // sanitizing

    val tokensToString = tokens.joinToString("")

    return tokens.assumeMul(symbolsSet, tokensToString)
}
