package com.teamsorcerers.wizardry.common.spell.component

import com.teamsorcerers.wizardry.Wizardry.Companion.logManager
import com.teamsorcerers.wizardry.configs.ServerConfigs
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import org.apache.logging.log4j.Logger

object ComponentRegistry {
    private val LOGGER: Logger = logManager.makeLogger(ComponentRegistry::class.java)
    lateinit var modules: MutableMap<String, Module> private set
    lateinit var modifiers: MutableMap<String, Modifier> private set
    lateinit var spellComponents: MutableMap<List<Item>, ISpellComponent> private set
    var entityTarget: TargetComponent? = null
    var blockTarget: TargetComponent? = null

    fun addModule(module: Module) {
        tryRegister(module, modules)
    }

    fun addModifier(modifier: Modifier) {
        tryRegister(modifier, modifiers)
    }

    fun getComponentForItems(items: List<Item>): ISpellComponent? {
        for (spells in spellComponents.keys)
            if (listStartsWith(items, spells))
                return spellComponents[spells]
        return null
    }

    fun initialize() {
        modules = HashMap()
        modifiers = HashMap()
        spellComponents = HashMap()

        loadTargets()
    }

    private fun listStartsWith(list: List<Item>, other: List<Item?>?): Boolean {
        if (other!!.size > list.size) return false
        val listIter = list.listIterator()
        val otherIter = other.listIterator()
        while (listIter.hasNext() && otherIter.hasNext()) if (listIter.next() != otherIter.next()) return false
        return true
    }

    private fun <Component : ISpellComponent> tryRegister(component: Component, map: MutableMap<String, in Component>): Boolean {
        val items = component.items
        for (keys in spellComponents.keys) {
            if (listStartsWith(keys, items)) {
                LOGGER.warn("Spell component registration failed for {} {}, recipe hidden by {}", component.javaClass.simpleName, component.name, spellComponents[keys]?.name)
                return false
            }
        }
        map[component.name] = component
        spellComponents[component.items] = component
        return true
    }

    private fun loadTargets() {
        if (entityTarget != null) spellComponents.remove(entityTarget?.items)
        if (blockTarget != null) spellComponents.remove(blockTarget?.items)
        entityTarget = TargetComponent("entityTarget", Registries.ITEM[Identifier.of(ServerConfigs.entityTargetItem)])
        blockTarget = TargetComponent("blockTarget", Registries.ITEM[Identifier.of(ServerConfigs.blockTargetItem)])
        spellComponents[entityTarget!!.items] = entityTarget!!
        spellComponents[blockTarget!!.items] = blockTarget!!
    }
}