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
internal fun String.isOperator(symbolsSet: Set<Char>): Boolean = this in symbolsSet.map { it.toString() } && this !in "()"

/**
 * Tokenizes a mathematical expression
 *
 * @receiver is the string to tokenize
 * @return the list of tokens
 * @throws KevalInvalidOperatorException if the expression contains an invalid operator
 */
internal fun String.tokenize(symbolsSet: Set<Char>): List<String> {
    // All symbols are escaped for the regex
    // TODO clean that absolute garbage mess
    val symbols = symbolsSet.joinToString("|\\")
    val sanitized = this.replace("\\s".toRegex(), "")
    val tokens = sanitized
        .split("""(?<=(\$symbols|\(|\)))|(?=(\$symbols|\(|\)))""".toRegex())
        .filter { it.isNotEmpty() }
    val tokensToString = tokens.joinToString("")

    var currentPos = 0
    var prevToken = TokenType.FIRST
    val ret = mutableListOf<String>()
    tokens.forEach { token ->
        prevToken = when {
            token.isNumeric() -> {
                if (shouldAutoMul(prevToken))
                    ret.add("*")
                TokenType.OPERAND
            }
            token.isOperator(symbolsSet) -> TokenType.OPERATOR
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
