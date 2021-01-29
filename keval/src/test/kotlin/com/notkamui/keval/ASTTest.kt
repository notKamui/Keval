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
        val fake = Operator['a']
        val fake2 = Operator['-']!!
        assertNull(fake)
        assertTrue {
            fake2.symbol == '-' &&
                fake2.precedence == 2 &&
                fake2.isLeftAssociative &&
                fake2.apply(5.0, 2.0) == 3.0
        }
        assertTrue {
            Operator['-']!! == Operator.SUB &&
                Operator['+']!! == Operator.ADD &&
                Operator['*']!! == Operator.MUL &&
                Operator['/']!! == Operator.DIV &&
                Operator['^']!! == Operator.POW &&
                Operator['%']!! == Operator.MOD &&
                Operator['(']!! == Operator.LPA &&
                Operator[')']!! == Operator.RPA
        }
    }

    @Test
    fun symbolsTest() {
        assertTrue {
            Operator.symbols().all {
                it.toString() in "+-*/%^()"
            }
        }
    }
}
