# Keval

***A Kotlin/JVM mini library for string evaluation***

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/0161eb0c1caa473cbb7f7e7f375e50c6)](https://www.codacy.com/gh/notKamui/Keval/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=notKamui/Keval&amp;utm_campaign=Badge_Grade)
[![CodeFactor](https://www.codefactor.io/repository/github/notkamui/keval/badge)](https://www.codefactor.io/repository/github/notkamui/keval)

(You may need to watch out using it:
having to evaluate a string into a number is more often than not a code smell)

## Usage

Keval can evaluate a mathematical expression as a `String` into a `Double` value.

Keval has full support for all classic binary operators:

- Subtraction `-`
- Addition `+`
- Multiplication `*`
- Division `/`
- Exponent `^`
- Remainder (mod) `%`

Keval has support for functions of variable arity, it has one built-in function:

- Negate/Oppose `neg(expr)` (where 'expr' is an expression)

You can optionally add as many binary operators or functions to Keval, as long as you define every field properly, with
a DSL (Domain Specific Language):

- A **binary operator** is defined by:
  - its **symbol** (a `Char` that is NOT a digit, nor a letter, nor an underscore)
  - its **precedence**/priority level (a positive `Int`)
  - its **associativity** (a `Boolean` true if left associative, false otherwise)
  - its **implementation** (a function `(Double, Double) -> Double`)
- A **function** is defined by:
  - its **name** (a non-empty `String` identifier, that doesn't start with a digit, and only contains letters, digits or
    underscores)
  - its **arity**/number of arguments (a positive (or 0) `Int`)
  - its **implementation** (a function `(DoubleArray) -> Double`)

Keval will use the default operators and function if you choose not to define any new resource ; but if you choose to do
so, you need to include them manually. You may also choose to use it as an extension function.

You can use it in four ways:

```Kotlin
Keval.eval("(3+4)(2/8 * 5)") // uses default resources

"(3+4)(2/8 * 5)".keval() // extension ; uses default resources

Keval { // DSL instance
    includeDefaults() // this function includes the built-in resources
    
    operator { // this DSL adds a binary operator ; you can call it several times
        symbol = ';'
        precedence = 3
        isLeftAssociative = true
        implementation = { a, b -> a.pow(2) + b.pow(2) }
    }
  
    function { // this DSL adds a function ; you can call it several times
        name = "max"
        arity = 2
        implementation = { args -> max(args[0], args[1]) }
    }
}.eval("2*max(2, 3) ; 4")

"2*max(2, 3) ; 4".keval { // DSL instance + extension
  includeDefaults()

  operator {
    symbol = ';'
    precedence = 3
    isLeftAssociative = true
    implementation = { a, b -> a.pow(2) + b.pow(2) }
  }

  function {
    name = "max"
    arity = 2
    implementation = { args -> max(args[0], args[1]) }
  }
}
```

The advantage of using `Keval {}` is that you may keep an instance of it in a variable so that you can call as
many `eval` as you need.

Creating a resource with a name that already exists will overwrite the previous one.

Keval assumes products/multiplications, and as such, the * symbol/name ***cannot*** be overwritten, and is the only
operator to
***always*** be present in the resource set of a Keval instance:

```Kotlin
"(2+3)(6+4)".keval() == "(2+3)*(6+4)".keval()
```

## Error Handling

In case of an error, Keval will throw one of several `KevalException`s:

- `KevalZeroDivisionException` in the case a zero division occurs
- `KevalInvalidExpressionException` if the expression is invalid, with the following properties:
  - `expression` contains the fully sanitized expression
  - `position` is an estimate of the position of the error
- `KevalInvalidOperatorException` if the expression contains an invalid operator, with the following properties:
  - `invalidOperator` contains the actual invalid operator
  - `expression` contains the fully sanitized expression
  - `position` is an estimate of the position of the error

`KevalZeroDivisionException` is instantiable so that you can throw it when implementing a custom operator/function.

Keval will also throw an `IllegalArgumentException` if, in the DSL, one of the field is either not set, or doesn't
follow its restrictions (defined above).

## Future Plans

- Support for constants (PI, PHI, etc)
- Support for variables (will produce a `DoubleArray` instead of a single `Double`)
- Support for multiplateform
