# Keval

***A Kotlin Multiplatform mini library for string evaluation***

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7f52ff.svg)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/com.notkamui.libs/keval.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.notkamui.libs/keval)

(You may need to watch out using it:
having to evaluate a string into a number is more often than not a code smell)

## Import

You can import Keval directly with the jar files, or using your favorite dependency manager with the Maven Central
repository:

Maven

```XML
<dependencies>
  <dependency>
    <groupId>com.notkamui.libs</groupId>
    <artifactId>keval</artifactId>
    <version>1.1.1</version>
  </dependency>
</dependencies>
```

Gradle (here with KotlinDSL)

```Kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("com.notkamui.libs:keval:1.1.1")
}
```

(In case you're using it with another language than Kotlin -- i.e. Java --, make sure you include kotlin stdlib too)

## Usage

Keval can evaluate a mathematical expression as a `String` into a `Double` value. It is customizable in the sense that
one can add new binary and unary operators, functions and constants.

The base settings of Keval already include sensible defaults for the most common mathematical operations.

Keval has support for all classic binary operators:

- Subtraction `-`
- Addition `+`
- Multiplication `*`
- Division `/`
- Exponent `^`
- Remainder (mod) `%`

Keval has support for all classic unary operators:
- Negation/Opposition `-` (prefix)
- Identity `+` (prefix) (basically does nothing)
- Factorial `!` (postfix)

Keval has support for functions of variable arity:

- Negate/Oppose `neg(expr)` (where 'expr' is an expression)
- Expression sign `sign(expr)` (where 'expr' is an expression)
- Absolute `abs(expr)` (where 'expr' is an expression)
- Square root `sqrt(expr)` (where 'expr' is an expression)
- Cube root `cbrt(expr)` (where 'expr' is an expression)
- Root of nth power `nthrt(expr_a, expr_pow)` (where 'expr_a' is an expression and 'expr_pow' is target power)
- Exponential `exp(expr)` (where 'expr' is an expression)
- Natural logarithm `ln(expr)` (where 'expr' is an expression)
- Base 10 logarithm `log10(expr)` (where 'expr' is an expression)
- Base 2 logarithm `log2(expr)` (where 'expr' is an expression)
- Sine `sin(expr)` (where 'expr' is an expression)
- Cosine `cos(expr)` (where 'expr' is an expression)
- Tangent `tan(expr)` (where 'expr' is an expression)
- Arcsine `asin(expr)` (where 'expr' is an expression)
- Arccosine `acos(expr)` (where 'expr' is an expression)
- Arctangent `atan(expr)` (where 'expr' is an expression)
- Ceiling `ceil(expr)` (where 'expr' is an expression)
- Floor `floor(expr)` (where 'expr' is an expression)
- Round `round(expr)` (where 'expr' is an expression)
- Truncate decimal part `trunc(expr)` (where 'expr' is an expression)
- Minimal/maximal/average/median value `min(expr...)`/`max(expr...)`/`avg(expr...)`/`median(expr...)` (where 'expr...' is any number of expressions to get results from)
- Percentile `percentile(expr_perc, expr_a)` (where 'expr_a' is the expression to get percentile from, and 'expr_perc' is the percent value)
- Random number generator/selector `rand(expr...)` (where 'expr...' is any number of expressions; either generates a random double (zero arguments), or generates a random integer up to expr (one argument), or selects a random value of given arguments (two or more arguments)
- Random number between 'start' and 'end' with 'step' `randRange(expr_start, expr_end, expr_step)`
- Built-in boolean operations (0.0 is equivalent to false, anything else is equivalent to true):
  - Convert to "boolean" form (0/1) `bool(expr)` (where 'expr' is an expression)
  - Invert `not(bool)` (where 'bool' is a boolean)
  - And `and(bool...)` (where 'bool...' is any number of booleans)
  - Not And `nand(bool...)` (where 'bool...' is any number of booleans)
  - Or `or(bool...)` (where 'bool...' is any number of booleans)
  - Not Or `nor(bool...)` (where 'bool...' is any number of booleans)
  - Exclusive Or `xor(bool...)` (where 'bool...' is any number of booleans)
  - Exlusive Not Or `xnor(bool...)` (where 'bool...' is any number of booleans)
  - Implies `imply(bool_a, bool_b)` (where 'bool_a' and 'bool_b' are booleans)
  - Does Not Imply `nimply(bool_a, bool_b)` (where 'bool_a' and 'bool_b' are booleans)
  - Equals `eq(expr...)` (where 'expr...' is any number of expressions; returns a boolean)
  - Does Not Equal `ne(expr...)` (where 'expr...' is any number of expressions; returns a boolean)
  - Greater Than `gt(expr_a, expr_b)` (where 'expr_a' and 'expr_b' are expressions; returns a boolean)
  - Greater Or Equal To `ge(expr_a, expr_b)` (where 'expr_a' and 'expr_b' are expressions; returns a boolean)
  - Less Than `lt(expr_a, expr_b)` (where 'expr_a' and 'expr_b' are expressions; returns a boolean)
  - Less Or Equal To `le(expr_a, expr_b)` (where 'expr_a' and 'expr_b' are expressions; returns a boolean)

Keval has support for constants, it has two built-in constant:

- π `PI`
- *e* `e` (Euler's number)

You can optionally add as many operators, functions or constants to Keval, as long as you define every field
properly, with a DSL (Domain Specific Language):

- A **binary operator** is defined by:
  - its **symbol** (a `Char` that is NOT a digit, nor a letter, nor an underscore)
  - its **precedence**/priority level (a positive `Int`)
  - its **associativity** (a `Boolean` true if left associative, false otherwise)
  - its **implementation** (a function `(Double, Double) -> Double`)
- A **unary operator** is defined by:
  - its **symbol** (a `Char` that is NOT a digit, nor a letter, nor an underscore)
  - whether it is **prefix** (a `Boolean`)
  - its **implementation** (a function `(Double) -> Double`)
- A **function** is defined by:
  - its **name** (a non-empty `String` identifier, that doesn't start with a digit, and only contains letters, digits or
    underscores)
  - its **arity**/number of arguments (a positive (or 0) `Int` or null if the function can take any number of arguments,
    also called a *variadic function*)
  - its **implementation** (a function `(DoubleArray) -> Double`)
- A **constant** is defined by:
  - its **name** (a non-empty `String` identifier, that doesn't start with a digit, and only contains letters, digits or
    underscores)
  - its **value** (a `Double`)

Keval will use the built-in operators, function and constants if you choose not to define any new resource ; but if you
choose to do so, you need to include them manually. You may also choose to use Keval as an extension function.

> Please note that adding a new resource with a name that already exists will overwrite the previous one, except in the
> case of operators, where one symbol can represent both a binary and a unary operator. For example, it is possible to
> define a binary operator `-` and a unary operator `-` at the same time.

You can use it in several ways:

```Kotlin

Keval.eval("(3+4)(2/8 * 5) % PI") // uses default resources

"(3+4)(2/8 * 5) % PI".keval() // extension ; uses default resources

Keval.create { // builder instance
    includeDefault() // this function includes the built-in resources
    
    binaryOperator { // this function adds a binary operator ; you can call it several times
        symbol = ';'
        precedence = 3
        isLeftAssociative = true
        implementation = { a, b -> a.pow(2) + b.pow(2) }
    }
    
    unaryOperator { // this function adds a unary operator ; you can call it several times
        symbol = '#'
        isPrefix = false
        implementation = { arg -> (1..arg.toInt()).fold(0.0) { acc, i -> acc + i } }
    }
  
    function { // this function adds a function ; you can call it several times
        name = "max"
        arity = 2
        implementation = { args -> max(args[0], args[1]) }
    }
  
    function { // this function adds a variadic aggregation (no arity) ; you can call it several times
        name = "sum"
        implementation = { args -> args.sum() }
    }
  
    constant { // this function adds a constant ; you can call it several times
        name = "PHI"
        value = 1.618
    }
}.eval("2*max(2, 3) ; 4# + PHI^2")

"2*max(2, 3) ; 4# + PHI^2".keval { // builder instance + extension
    includeDefault()
  
    binaryOperator {
        symbol = ';'
        precedence = 3
        isLeftAssociative = true
        implementation = { a, b -> a.pow(2) + b.pow(2) }
    }
    
    unaryOperator {
        symbol = '#'
        isPrefix = false
        implementation = { arg -> (1..arg.toInt()).fold(0.0) { acc, i -> acc + i } }
    }
  
    function {
        name = "max"
        arity = 2
        implementation = { args -> max(args[0], args[1]) }
    }
  
    function {
        name = "sum"
        implementation = { args -> args.sum() }
    }
  
    constant {
        name = "PHI"
        value = 1.618
    }
}
```

The advantage of using `Keval.create` is that you may keep an instance of it in a variable so that you can call as
many `eval` as you need.

In concordance with creating a Keval instance, you can also add resources like this:

```Kotlin
val kvl = Keval().create {}
    .withDefault() // includes default resources // it is unnecessary here since Keval() with no DSL already does it
    .withBinaryOperator( // includes a new binary operator
        ';', // symbol
        3, // precedence
        true // isLeftAssociative
    ) { a, b -> a.pow(2) + b.pow(2) } // implementation
    .withUnaryOperator( // includes a new unary operator
        '#', // symbol
        false, // isPrefix
    ) { arg -> (1..arg.toInt()).fold(0.0) { acc, i -> acc + i } } // implementation 
    .withFunction( // includes a new function
        "max", // name
        2 // arity
    ) { max(it[0], it[1]) } // implementation
    .withFunction( // includes a new variadic function
        "sum", // name
    ) { it.sum() } // implementation
    .withConstant( // includes a new constant
        "PHI", // name
        1.618 // value
    )

kvl.eval("2*max(2, 3) ; 4# + PHI^2")
```

This can be combined with creating an instance with a DSL (i.e. `Keval.create`).
***This is an especially useful syntax for Java users, since DSLs generally don't translate well over it.***

Creating a resource with a name that already exists will overwrite the previous one.

Keval assumes products/multiplications, and as such, the * symbol/name ***cannot*** be overwritten, and is the only
operator to
***always*** be present in the resource set of a Keval instance:

```Kotlin
"(2+3)(6+4)".keval() == "(2+3)*(6+4)".keval()
```

In addition, the symbols `(`,`)`,`,` are reserved and trying to create operator using one of those symbols will result with an exception.

## Error Handling

In case of an error, Keval will throw one of several `KevalException`s:

- `KevalZeroDivisionException` in the case a zero division occurs
- `KevalInvalidArgumentException` in the case a operator or function is called with an invalid argument (i.e. a negative number
  for a factorial)
- `KevalInvalidExpressionException` if the expression is invalid, with the following properties:
  - `expression` contains the fully sanitized expression
  - `position` is an estimate of the position of the error
- `KevalInvalidSymbolException` if the expression contains an invalid operator, with the following properties:
  - `invalidSymbol` contains the actual invalid operator
  - `expression` contains the fully sanitized expression
  - `position` is an estimate of the position of the error
- `KevalDSLException` if, in the DSL, one of the field is either not set, or doesn't follow its restrictions (defined
  above)

`KevalZeroDivisionException` and `KevalInvalidArgumentException` are instantiable so that you can throw them when
implementing a custom operator/function.

## Future Plans

- Support for variables (will produce a `DoubleArray` instead of a single `Double`)
