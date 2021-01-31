package com.notkamui.keval

import com.notkamui.keval.framework.loadBuiltInOperators
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests on AbstractSyntaxTree
 */
class ASTTest {
    /**
     * Tests Node.eval()
     */
    @Test
    fun simpleEvalTest() {
        val operators = loadBuiltInOperators()
        val ast: Node = OperatorNode(ValueNode(3.0), operators['+']!!.implementation, ValueNode(2.0))
        assertEquals(ast.eval(), 5.0)
    }

    /**
     * Checks if all symbols are present
     */
    @Test
    fun symbolsTest() {
        val operators = loadBuiltInOperators()
        assertTrue {
            "+-*/%^()".all {
                it in operators.keys
            }
        }
    }
}
