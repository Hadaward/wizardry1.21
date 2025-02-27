package com.teamsorcerers.wizardry.common.init

import com.mojang.serialization.Lifecycle
import com.teamsorcerers.wizardry.Wizardry
import com.teamsorcerers.wizardry.common.init.ModPatterns.ModuleReloadListener.loadYamlModifier
import com.teamsorcerers.wizardry.common.spell.component.ComponentRegistry
import com.teamsorcerers.wizardry.common.spell.component.Pattern
import com.teamsorcerers.wizardry.common.spell.effect.EffectArcane
import com.teamsorcerers.wizardry.common.spell.effect.EffectBurn
import com.teamsorcerers.wizardry.common.spell.loading.ModifierLoader
import com.teamsorcerers.wizardry.common.spell.loading.ModuleLoader
import com.teamsorcerers.wizardry.common.spell.shape.ShapeRay
import com.teamsorcerers.wizardry.common.spell.shape.ShapeSelf
import com.teamsorcerers.wizardry.common.spell.shape.ShapeZone
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.registry.DefaultedRegistry
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import java.io.InputStream

object ModPatterns {
    val PATTERN_KEY: RegistryKey<Registry<Pattern>> = RegistryKey.ofRegistry(Wizardry.getID("pattern"))
    val PATTERN = DefaultedRegistry("${Wizardry.MODID}:self", PATTERN_KEY, Lifecycle.experimental())

    val shapeSelf = ShapeSelf()
    val shapeRay = ShapeRay()
    val shapeZone = ShapeZone()

    val effectArcane = EffectArcane()
    val effectBurn = EffectBurn()

    fun init() {
        register("self", shapeSelf)
        register("ray", shapeRay)
        register("zone", shapeZone)

        register("arcane", effectArcane)
        register("burn", effectBurn)
    }

    private fun register(path: String, pattern: Pattern) {
        Registry.register(PATTERN, Wizardry.getID(path), pattern)
    }

    object ModuleReloadListener : SimpleSynchronousResourceReloadListener {
        private val LOGGER = Wizardry.logManager.makeLogger(ModuleReloadListener::class.java)

        override fun getFabricId(): Identifier { return Wizardry.getID("pattern") }

        override fun reload(manager: ResourceManager) {
            ComponentRegistry.initialize()

            for (id in manager.findResources("module") { path -> path.path.startsWith(".json") }) {
                try { manager.getResource(id.key).loadJsonModule() }
                catch (e: Exception) { LOGGER.error("Error occurred while loading module json $id", e) }
            }
            for (id in manager.findResources("module") { path -> path.path.endsWith(".yaml") }) {
                try { manager.getResource(id.key).loadYamlModule() }
                catch (e: Exception) { LOGGER.error("Error occurred while loading module yaml $id", e) }
            }
            for (id in manager.findResources("modifier") { path -> path.path.endsWith(".json") }) {
                try { manager.getResource(id.key).loadJsonModifier() }
                catch (e: Exception) { LOGGER.error("Error occurred while loading modifier json $id", e) }
            }
            for (id in manager.findResources("modifier") { path -> path.path.endsWith(".yaml") }) {
                try { manager.getResource(id.key).loadYamlModifier() }
                catch (e: Exception) { LOGGER.error("Error occurred while loading modifier json $id", e) }
            }
        }

        private fun Resource.loadJsonModule() { ComponentRegistry.addModule(ModuleLoader.loadJson(this.inputStream)); this.inputStream.close() }

        private fun Resource.loadYamlModule() { ModuleLoader.loadYaml(this.inputStream).forEach(ComponentRegistry::addModule); this.inputStream.close() }

        private fun Resource.loadJsonModifier() { ComponentRegistry.addModifier(ModifierLoader.loadJson(this.inputStream)); this.inputStream.close() }

        private fun Resource.loadYamlModifier() { ModifierLoader.loadYaml(this.inputStream).forEach(ComponentRegistry::addModifier); this.inputStream.close() }
    }
}