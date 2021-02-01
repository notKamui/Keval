package com.notkamui.keval

private enum class TokenType {
    FIRST,
    OPERAND,
    OPERATOR,
    LPAREN,
    RPAREN,
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
            token.isOperator(symbolsSet) -> TokenType.OPERATOR
            token == "(" -> {
                if (shouldAssumeMul(prevToken))
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
internal fun String.isOperator(symbolsSet: Set<String>): Boolean = this in symbolsSet

/**
 * Tokenizes a mathematical expression
 *
 * @receiver is the string to tokenize
 * @return the list of tokens
 * @throws KevalInvalidOperatorException if the expression contains an invalid operator
 */
internal fun String.tokenize(symbolsSet: Set<String>): List<String> {
    // All symbols are properly escaped for the regex
    val symbols = symbolsSet.joinToString("|") {
        it.replace("[^a-zA-Z0-9]".toRegex()) { c -> "\\${c.value}" }
    }
    val tokens = this
        .replace("\\s".toRegex(), "") // sanitizing expression
        .split("""(?<=($symbols|\(|\)))|(?=($symbols|\(|\)))""".toRegex()) // tokenizing
        .filter { it.isNotEmpty() } // removing possible empty tokens
    val tokensToString = tokens.joinToString("")

    return tokens.assumeMul(symbolsSet, tokensToString)
}
