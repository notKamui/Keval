package com.notkamui.keval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ASTTest {
    @Test
    fun simpleEvalTest() {
        val ast: Node = OperatorNode(ValueNode(3.0), Operator.ADD, ValueNode(2.0))
        assertEquals(ast.eval(), 5.0)
    }

    @Test
    fun operatorGetterTest() {
        val op = Operator['%']
        val op2 = Operator['a']
        val op3 = Operator['-']!!
        assertEquals(Operator.MOD, op)
        assertNull(op2)
        assertTrue(op3.symbol == '-' && op3.precedence == 2 && op3.isLeftAssociative && op3.apply(5.0, 2.0) == 3.0)
    }

    @Test
    fun symbolsTest() {
        assertTrue {
            Operator.symbols().all {
                it.toString() in "+-*/%^"
            }
        }
    }
}
