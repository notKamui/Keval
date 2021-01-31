package com.notkamui.keval.resources

import com.notkamui.keval.BinaryOperator
import com.notkamui.keval.framework.KevalSymbolDefinition
import com.notkamui.keval.framework.getKevalOperator
import com.notkamui.keval.framework.isLeftAssociative
import com.notkamui.keval.framework.kevalSymbolsDefault
import com.notkamui.keval.framework.precedence

/**
 * KevalResources allows for loading resources for Keval to be used during evaluations
 * (operators, functions, constants)
 */
class KevalResources internal constructor() {
    private val _operators: MutableMap<Char, BinaryOperator> = mutableMapOf()

    /**
     * Keval's loaded resources
     */
    val operators: Map<Char, BinaryOperator>
        get() = _operators.toMap()

    /**
     * Add a group of resources to Keval
     *
     * @receiver the resources used by Keval
     */
    operator fun Map<Char, BinaryOperator>.unaryPlus() {
        _operators += this
    }

    /**
     * Loads annotated resources into Keval with given package name(s)
     *
     * @param _package is the package name where to search for resources
     * @param packages are the other package names where to search for resources
     * @return the loaded resources
     */
    fun loadResources(_package: String, vararg packages: String): Map<Char, BinaryOperator> =
        kevalSymbolsDefault(_package, *packages).map {
            val method = getKevalOperator(it, _package, *packages)!!
            it to BinaryOperator(
                { x, y ->
                    method.invoke(null, x, y) as Double
                },
                method.precedence(),
                method.isLeftAssociative()
            )
        }.toMap()

    /**
     * Loads all annotated resources into Keval
     *
     * @return the loaded resources
     */
    fun loadAllResources(): Map<Char, BinaryOperator> =
        loadResources("")

    /**
     * Loads all built in operators into Keval
     *
     * @return the loaded operators
     */
    fun loadBuiltInOperators(): Map<Char, BinaryOperator> =
        loadResources(KevalSymbolDefinition::class.java.packageName)
}
