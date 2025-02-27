package com.teamsorcerers.wizardry.common.spell.component

import net.minecraft.item.Item

class Modifier(override val name: String, override val items: List<Item>) : ISpellComponent {
    val translationKey: String get() = "wizardry.modifier.$name"
}