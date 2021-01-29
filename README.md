# Keval

***A Kotlin mini library for string evaluation***

(You may need to watch out using it:
having to evaluate a string into a number is more often than not a code smell)

## What Does It Do ?

Keval can evaluate a mathematical expression as a `String` into a `Double` value.

Keval has full support for all classic binary operators:

- Subtraction `-`
- Addition `+`
- Multiplication `*`
- Division `/`
- Exponent `^`
- Remainder (mod) `%`

You can use it in two ways:

```Kotlin
Keval.eval("(3+4)(2/8 * 5)")

"(3+4)(2/8 * 5)".keval()
```

(The static option will mainly be used by Java users)

Keval has support for optional product symbol:

```Kotlin
"(2+3)(6+4)".keval() == "(2+3)*(6+4)".keval()
```

In case of an error, Keval will throw one of several `KevalException`s:

- `KevalZeroDivisionException` in the case a zero division occurs
- `KevalInvalidExpressionException` if the expression is invalid, with the following properties:
    - `expression` contains the fully sanitized expression
    - `position` is an estimate of the position of the error
- `KevalInvalidOperatorException` if the expression contains an invalid operator, with the following properties:
    - `invalidOperator` contains the actual invalid operator
    - `expression` contains the fully sanitized expression
    - `position` is an estimate of the position of the error

## Future Plans

- Full support for unary operators (oppose/negate, factorial, etc)
- Full support for operators of variable arity (more than 2)
- Support for constants (PI, PHI, etc)
- Support for functions (sin, cos, tan)
- Support for custom operators from the user ?