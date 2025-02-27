package com.teamsorcerers.wizardry.common.spell.component

import com.teamsorcerers.wizardry.common.spell.component.ISpellComponent
import net.minecraft.item.Item
import java.util.*
import java.util.stream.Collectors

open class Module(
    // Identifying data - must be unique
    open val pattern: Pattern,
    override val name: String,
    override val items: List<Item>,
    // Base Costs
    val baseManaCost: Double,
    // Modifier and Usage Metadata
    private val modifierCosts: Map<String, Double>,
    private val attributeValues: Map<String, List<Double>>
) : ISpellComponent {

    val id: String get() = "${pattern.id}:$name"
    val translationKey: String get() = "wizardry.spell.$id"
    fun getTranslationSubKey(subKey: String): String { return "$translationKey.$subKey" }
    override fun toString(): String { return "$id = [$items]"; }

    fun getCostPerModifier(modifier: String): Double {
        return modifierCosts[modifier] ?: 0.05
    }

    fun getAttributeValue(attribute: String, count: Int): Double {
        var count = count
        val values = attributeValues.getOrDefault(attribute, listOf(1.0))
        if (count < 0) count = 0
        if (count >= values.size) count = values.size - 1
        return values[count]
    }

    /**
     * All attributes used by this module (or that at least have a non-default value)
     */
    val allAttributes: List<String>
        get() = LinkedList(attributeValues.keys)

    /**
     * All attributes that will use modifiers. Attributes with just a single value are
     * non-default, but unmodifiable, and as such should not be available for having modifiers
     */
    val attributes: List<String>
        get() = attributeValues.keys.stream().filter { attribute: String -> attributeValues[attribute]!!.size > 1 }.collect(Collectors.toList())
}