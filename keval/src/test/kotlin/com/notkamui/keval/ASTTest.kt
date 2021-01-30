package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests on AbstractSyntaxTree
 */
class ASTTest {
    @Test
    fun testDefinitionAnnotations() {
        assertTrue {
            KevalSymbolAnnotations.map { it.name }.containsAll(
                    setOf(
                            KevalBinaryOperator::class.java.name,
                            KevalConstant::class.java.name,
                            KevalFunction::class.java.name,
                            KevalUnaryOperator::class.java.name
                    )
            )
        }
    }

    /**
     * Tests Node.eval()
     */
    @Test
    fun simpleEvalTest() {
        val ast: Node = OperatorNode(ValueNode(3.0), getKevalOperator('+')!!, ValueNode(2.0))
        assertEquals(ast.eval(), 5.0)
    }

    /**
     * Checks if all symbols are present
     */
    @Test
    fun symbolsTest() {
        assertTrue {
            "+-*/%^()".all {
                it.toString() in kevalSymbols()
            }
        }
    }
}
