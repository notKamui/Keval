---
title: keval -
---
//[Keval](../index.md)/[com.notkamui.keval](index.md)/[keval](keval.md)

# keval

[jvm]  
Content  
fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[keval](keval.md)(
generator: [KevalDSL](-keval-d-s-l/index.md).()
-> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  
More info

Evaluates a mathematical expression to a double value with given resources

#### Return

the value of the expression

## Parameters

jvm

|  Name|  Summary| 
|---|---|
| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a><receiver>| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a><br><br>is the expression to evaluate<br><br>
| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a>generator| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a><br><br>is the DSL generator of Keval<br><br>

#### Throws

|  Name|  Summary| 
|---|---|
| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a>[com.notkamui.keval.KevalInvalidOperatorException](-keval-invalid-operator-exception/index.md)| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a><br><br>in case there's an invalid operator in the expression<br><br>
| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a>[com.notkamui.keval.KevalInvalidExpressionException](-keval-invalid-expression-exception/index.md)| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a><br><br>in case the expression is invalid (i.e. mismatched parenthesis)<br><br>
| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a>[com.notkamui.keval.KevalZeroDivisionException](-keval-zero-division-exception/index.md)| <a name="com.notkamui.keval//keval/kotlin.String#kotlin.Function1[com.notkamui.keval.KevalDSL,kotlin.Unit]/PointingToDeclaration/"></a><br><br>in case of a zero division<br><br>

[jvm]  
Content  
fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[keval](
keval.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  
More info

Evaluates a mathematical expression to a double value with the default resources

#### Return

the value of the expression

#### Throws

|  Name|  Summary| 
|---|---|
| <a name="com.notkamui.keval//keval/kotlin.String#/PointingToDeclaration/"></a>[com.notkamui.keval.KevalInvalidOperatorException](-keval-invalid-operator-exception/index.md)| <a name="com.notkamui.keval//keval/kotlin.String#/PointingToDeclaration/"></a><br><br>in case there's an invalid operator in the expression<br><br>
| <a name="com.notkamui.keval//keval/kotlin.String#/PointingToDeclaration/"></a>[com.notkamui.keval.KevalInvalidExpressionException](-keval-invalid-expression-exception/index.md)| <a name="com.notkamui.keval//keval/kotlin.String#/PointingToDeclaration/"></a><br><br>in case the expression is invalid (i.e. mismatched parenthesis)<br><br>
| <a name="com.notkamui.keval//keval/kotlin.String#/PointingToDeclaration/"></a>[com.notkamui.keval.KevalZeroDivisionException](-keval-zero-division-exception/index.md)| <a name="com.notkamui.keval//keval/kotlin.String#/PointingToDeclaration/"></a><br><br>in case of a zero division<br><br>
  



