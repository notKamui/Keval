package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests on AbstractSyntaxTree
 */
class ASTTest {
    /**
     * Tests Node.eval()
     */
    @Test
    fun simpleEvalTest() {
        val operators: Map<String, BinaryOperator> = Resources.defaultOperators
        val ast: Node = OperatorNode(ValueNode(3.0), operators["+"]!!.implementation, ValueNode(2.0))
        assertEquals(ast.eval(), 5.0)
    }
}
