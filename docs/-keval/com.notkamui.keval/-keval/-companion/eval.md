---
title: eval -
---
//[Keval](../../../index.md)/[com.notkamui.keval](../../index.md)/[Keval](../index.md)/[Companion](index.md)
/[eval](eval.md)

# eval

[jvm]  
Content  
fun [eval](eval.md)(
mathExpression: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  
More info

Evaluates a mathematical expression to a double value

#### Return

the value of the expression

## Parameters

jvm

|  Name|  Summary| 
|---|---|
| <a name="com.notkamui.keval/Keval.Companion/eval/#kotlin.String/PointingToDeclaration/"></a>mathExpression| <a name="com.notkamui.keval/Keval.Companion/eval/#kotlin.String/PointingToDeclaration/"></a><br><br>is the expression to evaluate<br><br>

#### Throws

|  Name|  Summary| 
|---|---|
| <a name="com.notkamui.keval/Keval.Companion/eval/#kotlin.String/PointingToDeclaration/"></a>[com.notkamui.keval.KevalInvalidOperatorException](../../-keval-invalid-operator-exception/index.md)| <a name="com.notkamui.keval/Keval.Companion/eval/#kotlin.String/PointingToDeclaration/"></a><br><br>in case there's an invalid operator in the expression<br><br>
| <a name="com.notkamui.keval/Keval.Companion/eval/#kotlin.String/PointingToDeclaration/"></a>[com.notkamui.keval.KevalInvalidExpressionException](../../-keval-invalid-expression-exception/index.md)| <a name="com.notkamui.keval/Keval.Companion/eval/#kotlin.String/PointingToDeclaration/"></a><br><br>in case the expression is invalid (i.e. mismatched parenthesis)<br><br>
| <a name="com.notkamui.keval/Keval.Companion/eval/#kotlin.String/PointingToDeclaration/"></a>[com.notkamui.keval.KevalZeroDivisionException](../../-keval-zero-division-exception/index.md)| <a name="com.notkamui.keval/Keval.Companion/eval/#kotlin.String/PointingToDeclaration/"></a><br><br>in case of a zero division<br><br>
  



