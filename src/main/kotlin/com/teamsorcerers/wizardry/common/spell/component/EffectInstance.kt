package com.teamsorcerers.wizardry.common.spell.component

/**
 * Contains data relevant to a single cast event of a `ModuleEffect` component.
 * Do not construct, instances are provided for calls to
 * [PatternEffect.affectBlock] and
 * [PatternEffect.affectEntity]
 * See [Instance] for detailed information on the available data.
 * @see Instance
 *
 * @see PatternEffect
 */
class EffectInstance(pattern: Pattern, targetType: TargetType, attributeValues: Map<String, Double>, manaCost: Double, caster: Interactor) : Instance(pattern, targetType, attributeValues, manaCost, caster) {
    init {
        extraData.putString("pattern_type", "effect")
    }
}