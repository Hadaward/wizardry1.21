package com.teamsorcerers.wizardry.capability.network

import com.teamsorcerers.wizardry.Wizardry
import com.teamsorcerers.wizardry.common.block.IManaNode
import com.teamsorcerers.wizardry.configs.ServerConfigs
import net.minecraft.block.Block
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.PersistentState
import java.util.*
import kotlin.math.ceil

class ManaNetwork private constructor() : PersistentState() {
    private val positions: MutableMap<ChunkPos, MutableSet<BlockPos>> = HashMap()
    private val paths: MutableList<ManaPath> = LinkedList()


    override fun writeNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup): NbtCompound {
        return nbt //fiz pra ele parar de reclamar
    }


    fun addBlock(pos: BlockPos) {
        positions.computeIfAbsent(ChunkPos(pos)) {HashSet<BlockPos>()}.add(pos)
    }

    fun removeBlock(pos: BlockPos) {
        val blocks = positions[ChunkPos(pos)]
        blocks?.remove(pos)
    }

    fun tick(world: ServerWorld) {
        paths.removeIf { path: ManaPath? -> !path!!.transfer(world) }
    }

    fun findPath(world: ServerWorld, pos: BlockPos): Boolean {
        val paths: MutableMap<BlockPos, ManaPath> = HashMap()
        val nodesToSearch: Queue<BlockPos> = LinkedList()
        for (node in nodesNear(pos)) {
            paths[node] = ManaPath(pos, node)
            nodesToSearch.add(node)
        }
        while (!nodesToSearch.isEmpty()) {
            val nodePos = nodesToSearch.remove()
            val nodeBlock: Block = world.getBlockState(nodePos).block
            if (nodeBlock !is IManaNode) {
                positions[ChunkPos(nodePos)]?.remove(nodePos)
                continue
            }
            val node: IManaNode = world.getBlockState(nodePos).block as IManaNode
            val path = paths[nodePos]
            when (node.manaNodeType) {
                IManaNode.ManaNodeType.SINK -> continue
                IManaNode.ManaNodeType.SOURCE -> {
                    if (path != null) { this.paths.add(path) }
                    return true
                }
                IManaNode.ManaNodeType.ROUTER -> for (nearby in nodesNear(nodePos)) {
                    paths[nearby] = ManaPath(path, nearby)
                    nodesToSearch.add(nearby)
                }
            }
        }
        return false
    }

    private fun nodesNear(pos: BlockPos): List<BlockPos> {
        val maxDist = ServerConfigs.pathRange
        val chunkDist = ceil(maxDist / 16).toInt()
        val nodes: MutableList<BlockPos> = LinkedList<BlockPos>()
        val centerX = pos.x shr 4
        val centerZ = pos.z shr 4
        for (x in -chunkDist..chunkDist)
            for (z in -chunkDist..chunkDist)
                positions.getOrDefault(ChunkPos(centerX + x, centerZ + z), emptySet())
                    .stream()
                    .filter { node: BlockPos -> node.getSquaredDistance(pos) <= maxDist * maxDist }
                    .forEach { e: BlockPos -> nodes.add(e) }
        return nodes
    }

/*
    override fun writeNbt(compound: NbtCompound): NbtCompound {
        compound.putLongArray(POSITIONS, positions.values.stream().flatMap {obj: Set<BlockPos> -> obj.stream() }.mapToLong {obj: BlockPos -> obj.asLong()}.toArray())
        return compound
    }
*/

   /* companion object {
        private const val DATA_NAME: String = Wizardry.MODID + "_ManaNetwork"
        private const val POSITIONS = "positions"
        operator fun get(world: ServerWorld): ManaNetwork {
            return world.persistentStateManager.getOrCreate({ nbt: NbtCompound -> readNbt(nbt) }, { ManaNetwork() }, DATA_NAME)
        }

        private fun readNbt(nbt: NbtCompound): ManaNetwork {
            val network = ManaNetwork()
            for (pos in nbt.getLongArray(POSITIONS)) {
                val block = BlockPos.fromLong(pos)
                val chunk = ChunkPos(block)
                network.positions.computeIfAbsent(chunk) {HashSet<BlockPos>()}.add(block)
            }
            return network
        }
    }*/
}