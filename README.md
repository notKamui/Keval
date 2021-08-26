# Keval

***A Kotlin/JVM mini library for string evaluation***

[![Maven Central](https://img.shields.io/maven-central/v/com.notkamui.libs/keval.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.notkamui.libs%22%20AND%20a:%22keval%22)
[![CodeFactor](https://www.codefactor.io/repository/github/notkamui/keval/badge)](https://www.codefactor.io/repository/github/notkamui/keval)

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
    <version>{version}</version>
  </dependency>
</dependencies>
```

Gradle (here with KotlinDSL)

```Kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("com.notkamui.libs:keval:{version}")
}
```

(In case you're using it with another language than Kotlin -- i.e. Java --, make sure you include kotlin stdlib too)

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

Keval has support for constants, it has two built-in constant:

- π `PI`
- *e* `e` (Euler's number)

You can optionally add as many binary operators, functions or constants to Keval, as long as you define every field
properly, with a DSL (Domain Specific Language):

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
- A **constant** is defined by:
  - its **name** (a non-empty `String` identifier, that doesn't start with a digit, and only contains letters, digits or
    underscores)
  - its **value** (a `Double`)

Keval will use the built-in operators, function and constants if you choose not to define any new resource ; but if you
choose to do so, you need to include them manually. You may also choose to use Keval as an extension function.

You can use it in several ways:

```Kotlin

Keval.eval("(3+4)(2/8 * 5) % PI") // uses default resources

"(3+4)(2/8 * 5) % PI".keval() // extension ; uses default resources

Keval { // DSL instance
    includeDefault() // this function includes the built-in resources
    
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
  
    constant { // this DSL adds a constant ; you can call it several times
        name = "PHI"
        value = 1.618
    }
}.eval("2*max(2, 3) ; 4 + PHI^2")

"2*max(2, 3) ; 4 + PHI^2".keval { // DSL instance + extension
    includeDefault()
  
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
  
    constant {
        name = "PHI"
        value = 1.618
    }
}
```

The advantage of using `Keval {}` is that you may keep an instance of it in a variable so that you can call as
many `eval` as you need.

In concordance with creating a Keval instance, you can also add resources like this:

```Kotlin
val kvl = Keval()
    .withDefault() // includes default resources // it is unnecessary here since Keval() with no DSL already does it
    .withOperator( // includes a new binary operator
        ';', // symbol
        3, // precedence
        true // isLeftAssociative
    ) { a, b -> a.pow(2) + b.pow(2) } // implementation
    .withFunction( // includes a new function
        "max", // name
        2 // arity
    ) { max(it[0], it[1]) } // implementation
    .withConstant( // includes a new constant
        "PHI", // name
        1.618 // value
    )

kvl.eval("2*max(2, 3) ; 4 + PHI^2")
```

This can be combined with creating an instance with a DSL (i.e. `Keval {}`).
***This is an especially useful syntax for Java users, since DSLs generally don't translate well over it.***

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
- `KevalDSLException` if, in the DSL, one of the field is either not set, or doesn't follow its restrictions (defined
  above)

`KevalZeroDivisionException` is instantiable so that you can throw it when implementing a custom operator/function.

## Future Plans

- Support for variables (will produce a `DoubleArray` instead of a single `Double`)
