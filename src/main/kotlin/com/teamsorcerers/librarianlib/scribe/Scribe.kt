package com.teamsorcerers.librarianlib.scribe

import com.teamsorcerers.librarianlib.scribe.nbt.*
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.Prism
import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.potion.Potion
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.sound.SoundEvent
import net.minecraft.stat.StatType
import net.minecraft.village.VillagerProfession
import net.minecraft.village.VillagerType
import net.minecraft.world.chunk.ChunkStatus

public object Scribe {
    @JvmStatic
    public val nbt: NbtPrism = Prism<NbtSerializer<*>>().also { prism ->
        prism.register(
            // java types
            ArraySerializerFactory(prism),
            ListSerializerFactory(prism),
            MapSerializerFactory(prism),
            ObjectSerializerFactory(prism),
            EnumSerializerFactory(prism),

            // kotlin types
            PairSerializerFactory(prism),
            TripleSerializerFactory(prism),

            // minecraft types
//            DefaultedListSerializerFactory(prism),
             TagPassthroughSerializerFactory(prism),
             MCPairSerializerFactory(prism),
             TextSerializerFactory(prism)
        )

        prism.register(
            // primitives
            PrimitiveLongSerializer,
            PrimitiveIntSerializer,
            PrimitiveShortSerializer,
            PrimitiveByteSerializer,
            PrimitiveCharSerializer,
            PrimitiveDoubleSerializer,
            PrimitiveFloatSerializer,
            PrimitiveBooleanSerializer,

            // boxed
            LongSerializer,
            IntegerSerializer,
            ShortSerializer,
            ByteSerializer,
            CharacterSerializer,
            DoubleSerializer,
            FloatSerializer,
            BooleanSerializer,
            NumberSerializer,

            // primitive arrays
            PrimitiveLongArraySerializer,
            PrimitiveIntArraySerializer,
            PrimitiveShortArraySerializer,
            PrimitiveByteArraySerializer,
            PrimitiveCharArraySerializer,
            PrimitiveDoubleArraySerializer,
            PrimitiveFloatArraySerializer,
            PrimitiveBooleanArraySerializer,

            // java types
            BigIntegerSerializer,
            BigDecimalSerializer,
            StringSerializer,
            BitSetSerializer,
            UUIDSerializer,

            // minecraft types
            BlockPosSerializer,
            Vec3dSerializer,
            Vec2fSerializer,
            ChunkPosSerializer,
            ColumnPosSerializer,
            ChunkSectionPosSerializer,
//            GlobalPosSerializer, // DimensionType serialization issues
            EulerAngleSerializer,
            BoxSerializer,
            BlockBoxSerializer,
            IdentifierSerializer,
            BlockStateSerializer,
//            GameProfileSerializer, // Need to figure out how to serialize/deserialize
            ItemStackSerializer,
//            StatusEffectInstanceSerializer, // The deserializer can return null, which we don't handle gracefully
            EnchantmentLevelEntrySerializer,
//            DimensionTypeSerializer, // DimensionType serialization issues

            // liblib types
            /*Vec2dSerializer,
            Vec2iSerializer,
            Rect2dSerializer,
            Matrix3dSerializer,
            MutableMatrix3dSerializer,
            Matrix4dSerializer,
            MutableMatrix4dSerializer,
            QuaternionSerializer*/
        )

        // Obtain DynamicRegistryManager
        val registryManager = MinecraftClient.getInstance().world?.registryManager
            ?: throw Exception("Couldn't get world instance.")

        // Get enchantment registry
        val enchantmentRegistry = registryManager.get(RegistryKeys.ENCHANTMENT)

        // registries
        prism.register(
            RegistryEntrySerializer(Registries.SOUND_EVENT, Mirror.reflect<SoundEvent>()),
            RegistryEntrySerializer(Registries.FLUID, Mirror.reflect<Fluid>()),
            RegistryEntrySerializer(Registries.STATUS_EFFECT, Mirror.reflect<StatusEffect>()),
            RegistryEntrySerializer(Registries.BLOCK, Mirror.reflect<Block>()),
            RegistryEntrySerializer(enchantmentRegistry, Mirror.reflect<Enchantment>()),
            RegistryEntrySerializer(Registries.ENTITY_TYPE, Mirror.reflect<EntityType<*>>()),
            RegistryEntrySerializer(Registries.ITEM, Mirror.reflect<Item>()),
            RegistryEntrySerializer(Registries.POTION, Mirror.reflect<Potion>()),
//            RegistryEntrySerializer(Registry.PARTICLE_TYPE, Mirror.reflect<ParticleType<?>>()),
//            RegistryEntrySerializer(Registry.BLOCK_ENTITY_TYPE, Mirror.reflect<BlockEntityType<?>>()),
            //RegistryEntrySerializer(Registries.PAINTING_MOTIVE, Mirror.reflect<PaintingMotive>()),
            RegistryEntrySerializer(Registries.CHUNK_STATUS, Mirror.reflect<ChunkStatus>()),
//            RegistryEntrySerializer(Registry.RULE_TEST, Mirror.reflect<RuleTestType<?>>()),
//            RegistryEntrySerializer(Registry.POS_RULE_TEST, Mirror.reflect<PosRuleTestType<?>>()),
//            RegistryEntrySerializer(Registry.SCREEN_HANDLER, Mirror.reflect<ScreenHandlerType<?>>()),
//            RegistryEntrySerializer(Registry.RECIPE_TYPE, Mirror.reflect<RecipeType<?>>()),
//            RegistryEntrySerializer(Registry.RECIPE_SERIALIZER, Mirror.reflect<RecipeSerializer<?>>()),
            RegistryEntrySerializer(Registries.ATTRIBUTE, Mirror.reflect<EntityAttribute>()),
            RegistryEntrySerializer(Registries.STAT_TYPE, Mirror.reflect<StatType<*>>()),
            RegistryEntrySerializer(Registries.VILLAGER_TYPE, Mirror.reflect<VillagerType>()),
            RegistryEntrySerializer(Registries.VILLAGER_PROFESSION, Mirror.reflect<VillagerProfession>()),
//            RegistryEntrySerializer(Registry.POINT_OF_INTEREST_TYPE, Mirror.reflect<PointOfInterestType>()),
//            RegistryEntrySerializer(Registry.MEMORY_MODULE_TYPE, Mirror.reflect<MemoryModuleType<?>>()),
//            RegistryEntrySerializer(Registry.SENSOR_TYPE, Mirror.reflect<SensorType<?>>()),
//            RegistryEntrySerializer(Registry.SCHEDULE, Mirror.reflect<Schedule>()),
//            RegistryEntrySerializer(Registry.ACTIVITY, Mirror.reflect<Activity>()),
//            RegistryEntrySerializer(Registry.LOOT_POOL_ENTRY_TYPE, Mirror.reflect<LootPoolEntryType>()),
//            RegistryEntrySerializer(Registry.LOOT_FUNCTION_TYPE, Mirror.reflect<LootFunctionType>()),
//            RegistryEntrySerializer(Registry.LOOT_CONDITION_TYPE, Mirror.reflect<LootConditionType>()),
//            RegistryEntrySerializer(Registry.SURFACE_BUILDER, Mirror.reflect<SurfaceBuilder<?>>()),
//            RegistryEntrySerializer(Registry.CARVER, Mirror.reflect<Carver<?>>()),
//            RegistryEntrySerializer(Registry.FEATURE, Mirror.reflect<Feature<?>>()),
//            RegistryEntrySerializer(Registry.STRUCTURE_FEATURE, Mirror.reflect<StructureFeature<?>>()),
//            RegistryEntrySerializer(Registry.STRUCTURE_PIECE, Mirror.reflect<StructurePieceType>()),
//            RegistryEntrySerializer(Registry.DECORATOR, Mirror.reflect<Decorator<?>>()),
//            RegistryEntrySerializer(Registry.BLOCK_STATE_PROVIDER_TYPE, Mirror.reflect<BlockStateProviderType<?>>()),
//            RegistryEntrySerializer(Registry.BLOCK_PLACER_TYPE, Mirror.reflect<BlockPlacerType<?>>()),
//            RegistryEntrySerializer(Registry.FOLIAGE_PLACER_TYPE, Mirror.reflect<FoliagePlacerType<?>>()),
//            RegistryEntrySerializer(Registry.TRUNK_PLACER_TYPE, Mirror.reflect<TrunkPlacerType<?>>()),
//            RegistryEntrySerializer(Registry.TREE_DECORATOR_TYPE, Mirror.reflect<TreeDecoratorType<?>>()),
//            RegistryEntrySerializer(Registry.FEATURE_SIZE_TYPE, Mirror.reflect<FeatureSizeType<?>>()),
//            RegistryEntrySerializer(Registry.BIOME_SOURCE, Mirror.reflect<Codec<? extends BiomeSource>>()),
//            RegistryEntrySerializer(Registry.CHUNK_GENERATOR, Mirror.reflect<Codec<? extends ChunkGenerator>>()),
//            RegistryEntrySerializer(Registry.STRUCTURE_PROCESSOR, Mirror.reflect<StructureProcessorType<?>>()),
//            RegistryEntrySerializer(Registry.STRUCTURE_POOL_ELEMENT, Mirror.reflect<StructurePoolElementType<?>>()),
        )
    }
}