---
title: com.notkamui.keval -
---
//[Keval](../index.md)/[com.notkamui.keval](index.md)

# Package com.notkamui.keval

## Types

|  Name|  Summary| 
|---|---|
| <a name="com.notkamui.keval/Keval///PointingToDeclaration/"></a>[Keval](-keval/index.md)| <a name="com.notkamui.keval/Keval///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>class [Keval](-keval/index.md)(**
generator**: [KevalDSL](-keval-d-s-l/index.md).() -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))  <br>More info  <br>Wrapper class for Keval, Contains a companion object with the evaluation method  <br><br><br>
| <a name="com.notkamui.keval/KevalDSL///PointingToDeclaration/"></a>[KevalDSL](-keval-d-s-l/index.md)| <a name="com.notkamui.keval/KevalDSL///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>class [KevalDSL](-keval-d-s-l/index.md)  <br>More info  <br>Resource wrapper for KevalDSL  <br><br><br>
| <a name="com.notkamui.keval/KevalDSLException///PointingToDeclaration/"></a>[KevalDSLException](-keval-d-s-l-exception/index.md)| <a name="com.notkamui.keval/KevalDSLException///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>class [KevalDSLException](-keval-d-s-l-exception/index.md) : [KevalException](-keval-exception/index.md)  <br>More info  <br>DSL Exception.  <br><br><br>
| <a name="com.notkamui.keval/KevalException///PointingToDeclaration/"></a>[KevalException](-keval-exception/index.md)| <a name="com.notkamui.keval/KevalException///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>sealed class [KevalException](-keval-exception/index.md) : [Exception](https://docs.oracle.com/javase/8/docs/api/java/lang/Exception.html)  <br>More info  <br>Generic Keval Exception  <br><br><br>
| <a name="com.notkamui.keval/KevalInvalidExpressionException///PointingToDeclaration/"></a>[KevalInvalidExpressionException](-keval-invalid-expression-exception/index.md)| <a name="com.notkamui.keval/KevalInvalidExpressionException///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open class [KevalInvalidExpressionException](-keval-invalid-expression-exception/index.md) : [KevalException](-keval-exception/index.md)  <br>More info  <br>Invalid Expression Exception.  <br><br><br>
| <a name="com.notkamui.keval/KevalInvalidOperatorException///PointingToDeclaration/"></a>[KevalInvalidOperatorException](-keval-invalid-operator-exception/index.md)| <a name="com.notkamui.keval/KevalInvalidOperatorException///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>class [KevalInvalidOperatorException](-keval-invalid-operator-exception/index.md) : [KevalInvalidExpressionException](-keval-invalid-expression-exception/index.md)  <br>More info  <br>Invalid Operator Exception.  <br><br><br>
| <a name="com.notkamui.keval/KevalZeroDivisionException///PointingToDeclaration/"></a>[KevalZeroDivisionException](-keval-zero-division-exception/index.md)| <a name="com.notkamui.keval/KevalZeroDivisionException///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>class [KevalZeroDivisionException](-keval-zero-division-exception/index.md) : [KevalException](-keval-exception/index.md)  <br>More info  <br>Zero Division Exception.  <br><br><br>

## Functions

|  Name|  Summary| 
|---|---|
| <a name="com.notkamui.keval//keval/kotlin.String#/PointingToDeclaration/"></a>[keval](keval.md)| <a name="com.notkamui.keval//keval/kotlin.String#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[keval](keval.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>More info  <br>Evaluates a mathematical expression to a double value with the default resources  <br><br><br>[jvm]  <br>Content  <br>fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[keval](keval.md)(generator: [KevalDSL](-keval-d-s-l/index.md).() -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>More info  <br>Evaluates a mathematical expression to a double value with given resources  <br><br><br>

