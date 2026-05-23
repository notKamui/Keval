package com.notkamui.keval

private enum class TokenType {
    FIRST, OPERAND, OPERATOR, LPAREN, RPAREN, COMMA,
}

private val IDENTIFIER_REGEX = Regex("[a-zA-Z_][a-zA-Z0-9_]*")

internal fun String.isIdentifierName(): Boolean =
    isNotEmpty() && this[0] !in '0'..'9' && IDENTIFIER_REGEX.matches(this)

private fun <N> String.isVariableOperand(symbols: Map<String, KevalOperator<N>>): Boolean =
    isIdentifierName() && this !in symbols

private fun shouldAssumeMul(tokenType: TokenType): Boolean =
    tokenType == TokenType.OPERAND || tokenType == TokenType.RPAREN

private fun <N> Sequence<String>.normalizeTokens(
    number: KevalNumber<N>,
    symbols: Map<String, KevalOperator<N>>,
): List<String> {
    var currentPos = 0
    var prevToken = TokenType.FIRST
    var parenthesesCount = 0
    val functionAtCount = mutableListOf(-1)
    val ret = mutableListOf<String>()
    this.forEach { token ->
        prevToken = when {
            token.isNumeric(number) || symbols[token] is KevalConstant || token.isVariableOperand(symbols) ->
                TokenType.OPERAND.also {
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

internal fun <N> String.isNumeric(number: KevalNumber<N>): Boolean =
    number.isValidLiteral(this)

internal fun String.isKevalOperator(symbolsSet: Set<String>): Boolean = this in symbolsSet

internal fun <N> String.tokenize(
    number: KevalNumber<N>,
    symbolsSet: Map<String, KevalOperator<N>>,
): List<String> =
    TOKENIZER_REGEX.findAll(this)
        .map(MatchResult::value)
        .filter(String::isNotBlank)
        .map { SANITIZE_REGEX.replace(it, "") }
        .normalizeTokens(number, symbolsSet)

private val SANITIZE_REGEX = """\s+""".toRegex()

private val TOKENIZER_REGEX =
    """(\d+\.\d+(?:[eE][+-]?\d+)?|\d+(?:[eE][+-]?\d+)?|[a-zA-Z_]\w*|[^\w\s])""".toRegex()
