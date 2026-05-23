package com.notkamui.keval

sealed class KevalException(message: String) : RuntimeException(message)

sealed class KevalInvalidExpressionException protected constructor(
    val expression: String,
    val position: Int,
    extraMessage: String = "",
) : KevalException(
    "Invalid expression at position $position in $expression${
        if (extraMessage.isNotBlank()) ", $extraMessage" else ""
    }"
)

internal class KevalMalformedExpressionException internal constructor(
    expression: String,
    position: Int,
    extraMessage: String = "",
) : KevalInvalidExpressionException(expression, position, extraMessage)

class KevalInvalidSymbolException internal constructor(
    val invalidSymbol: String,
    expression: String,
    position: Int,
    message: String = "",
) : KevalInvalidExpressionException(expression, position, message)

class KevalZeroDivisionException : KevalException("Division by zero")

class KevalInvalidArgumentException(message: String) : KevalException(message)

class KevalUnresolvedVariableException(val name: String) :
    KevalException("Unresolved variable: $name")

class KevalDSLException internal constructor(what: String) :
    KevalException("All required fields must be properly defined: $what")
