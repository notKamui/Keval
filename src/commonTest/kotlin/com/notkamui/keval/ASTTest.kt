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
        val operators = KevalBuilder.DEFAULT_RESOURCES
        val plus = (operators["+"] as? KevalBothOperator)!!.binary.implementation
        val ast: Node = BinaryOperatorNode(ValueNode(3.0), plus, ValueNode(2.0))
        assertEquals(ast.eval(), 5.0)
    }

    @Test
    fun testBinaryOperatorNode() {
        val left = ValueNode(5.0)
        val right = ValueNode(3.0)
        val node = BinaryOperatorNode(left, { a, b -> a - b }, right)
        assertEquals(2.0, node.eval())
    }

    @Test
    fun testUnaryOperatorNode() {
        val child = ValueNode(7.0)
        val node = UnaryOperatorNode({ a -> -a }, child)
        assertEquals(-7.0, node.eval())
    }

    @Test
    fun testFunctionNode() {
        val children = listOf(ValueNode(3.0), ValueNode(4.0), ValueNode(5.0))
        val node = FunctionNode({ a -> a.sum() }, children)
        assertEquals(12.0, node.eval())
    }

    @Test
    fun testValueNode() {
        val node = ValueNode(9.0)
        assertEquals(9.0, node.eval())
    }

    @Test
    fun testNestedFunctionNodes() {
        val node1 = FunctionNode({ a -> a.sum() }, listOf(ValueNode(3.0), ValueNode(4.0)))
        val node2 = FunctionNode({ a -> a.sum() }, listOf(node1, ValueNode(5.0)))
        val mainNode = FunctionNode({ a -> a.sum() }, listOf(node2, node1))
        assertEquals(19.0, mainNode.eval())
    }
}
