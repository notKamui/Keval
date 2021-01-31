package a

import com.notkamui.keval.framework.KevalBinaryOperator
import com.notkamui.keval.keval
import kotlin.math.pow
import kotlin.math.sqrt

@KevalBinaryOperator(precedence = 3, isLeftAssociative = false, symbol = ';')
fun hypotenuse(x: Double, y: Double): Double = sqrt(x.pow(2) + y.pow(2))

fun main() {
    println("3;4".keval())
}