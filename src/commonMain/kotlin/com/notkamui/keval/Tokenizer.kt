package com.notkamui.keval

private enum class TokenType {
    FIRST, OPERAND, OPERATOR, LPAREN, RPAREN, COMMA,
}

private fun shouldAssumeMul(tokenType: TokenType): Boolean =
    tokenType == TokenType.OPERAND || tokenType == TokenType.RPAREN

// normalize tokens to be of specific form (add product symbols where they should be assumed)
private fun Sequence<String>.normalizeTokens(symbols: Map<String, KevalOperator>): List<String> {
    var currentPos = 0
    var prevToken = TokenType.FIRST
    var parenthesesCount = 0
    val functionAtCount = mutableListOf(-1)
    val ret = mutableListOf<String>()
    this.forEach { token ->
        prevToken = when {
            token.isNumeric() || symbols[token] is KevalConstant -> TokenType.OPERAND.also {
                if (shouldAssumeMul(prevToken)) ret.add("*")
                ret.add(token)
            }

            token.isKevalOperator(symbols.keys) -> TokenType.OPERATOR.also {
                if (shouldAssumeMul(prevToken) && (symbols[token] is KevalConstant || symbols[token] is KevalFunction)) {
                    ret.add("*")
                }
                ret.add(token)
                if (symbols[token] is KevalFunction) {
                    functionAtCount.add(parenthesesCount + 1)
                }
            }

            token == "(" -> TokenType.LPAREN.also {
                parenthesesCount += 1
                if (shouldAssumeMul(prevToken)) ret.add("*")
                ret.add(token)
            }

            token == ")" -> TokenType.RPAREN.also {
                if (functionAtCount.last() == parenthesesCount) {
                    functionAtCount.removeLast()
                }
                parenthesesCount -= 1
                ret.add(token)
            }

            token == "," -> TokenType.COMMA.also {
                if (functionAtCount.last() == parenthesesCount) {
                    ret.add(token)
                } else {
                    throw KevalInvalidSymbolException(
                        token, joinToString(""), currentPos, "comma can only be used in the context of a function"
                    )
                }
            }

            else -> throw KevalInvalidSymbolException(token, joinToString(""), currentPos)
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
    toDoubleOrNull() ?: return false
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
internal fun String.tokenize(symbolsSet: Map<String, KevalOperator>): List<String> =
    TOKENIZER_REGEX.findAll(this)
        .map(MatchResult::value)
        .filter(String::isNotBlank)
        .map { SANITIZE_REGEX.replace(it, "") }
        .normalizeTokens(symbolsSet)

private val SANITIZE_REGEX = """\s+""".toRegex()

private val TOKENIZER_REGEX = """(\d+\.\d+|\d+|[a-zA-Z_]\w*|[^\w\s])""".toRegex()
