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

// normalize tokens to be of specific form (add product symbols where they should be assumed and enclose every parameter of a function in parentheses)
private fun List<String>.normalizeTokens(symbols: Map<String, KevalOperator>, tokensToString: String): List<String> {
    var currentPos = 0
    var prevToken = TokenType.FIRST
    var parenthesesCount = 0
    val functionAtCount = mutableListOf(-1)
    val ret = mutableListOf<String>()
    this.forEach { token ->
        prevToken = when {
            token.isNumeric() -> TokenType.OPERAND.also {
                if (shouldAssumeMul(prevToken)) ret.add("*")
                ret.add(token)
            }

            token.isKevalOperator(symbols.keys) -> TokenType.OPERATOR
                .also {
                    if (shouldAssumeMul(prevToken) && (symbols[token] is KevalConstant || symbols[token] is KevalFunction)) {
                        ret.add("*")
                    }
                    ret.add(token)
                    if (symbols[token] is KevalFunction) {
                        functionAtCount.add(parenthesesCount + 1)
                    }
                }

            token == "(" -> TokenType.LPAREN
                .also {
                    parenthesesCount += 1
                    if (shouldAssumeMul(prevToken)) ret.add("*")
                    ret.add(token)
                }

            token == ")" -> TokenType.RPAREN
                .also {
                    parenthesesCount -= 1
                    ret.add(token)
                }

            token == "," -> TokenType.COMMA
                .also {
                    if (functionAtCount.last() == parenthesesCount) {
                        ret.add(token)
                    } else {
                        throw KevalInvalidSymbolException(token, tokensToString, currentPos, "comma can only be used in the context of a function")
                    }
                }

            else -> throw KevalInvalidSymbolException(token, tokensToString, currentPos)
        }
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
 * @throws KevalInvalidSymbolException if the expression contains an invalid symbol
 */
internal fun String.tokenize(symbolsSet: Map<String, KevalOperator>): List<String> {
    val limits = """ |[^a-zA-Z0-9._]|,|\(|\)"""
    val tokens = this
        .split("""(?<=($limits))|(?=($limits))""".toRegex()) // tokenizing
        .filter { it.isNotBlank() } // removing possible empty tokens
        .map { it.replace("\\s".toRegex(), "") } // sanitizing

    val tokensToString = tokens.joinToString("")

    return tokens.normalizeTokens(symbolsSet, tokensToString)
}
