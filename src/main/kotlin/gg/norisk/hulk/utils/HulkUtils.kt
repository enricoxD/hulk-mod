package gg.norisk.hulk.utils

import gg.norisk.heroes.common.hero.isHero
import gg.norisk.hulk.Hulk
import gg.norisk.hulk.registry.SoundRegistry
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.task.mcCoroutineTask

object HulkUtils {
    fun generateSphere(centerBlock: BlockPos, radius: Int, hollow: Boolean): List<BlockPos> {
        val circleBlocks: MutableList<BlockPos> = ArrayList()
        val bx: Int = centerBlock.x
        val by: Int = centerBlock.y
        val bz: Int = centerBlock.z
        for (x in bx - radius..bx + radius) {
            for (y in by - radius..by + radius) {
                for (z in bz - radius..bz + radius) {
                    val distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y)).toDouble()
                    if (distance < radius * radius && !(hollow && distance < (radius - 1) * (radius - 1))) {
                        circleBlocks.add(BlockPos(x, y, z))
                    }
                }
            }
        }
        return circleBlocks
    }

    fun smashEntity(player: PlayerEntity, entity: Entity) {
        if (player.isHero(Hulk)) {
            player.world.playSoundAtBlockCenter(
                entity.blockPos,
                SoundRegistry.getRandomGrowlSound(),
                SoundCategory.PLAYERS,
                1f,
                1f,
                true
            )

            player.world.playSoundAtBlockCenter(
                entity.blockPos,
                SoundRegistry.BOOM,
                SoundCategory.PLAYERS,
                1f,
                5f,
                true
            )

            entity.modifyVelocity(player.directionVector.normalize().multiply(5.0))
            mcCoroutineTask(howOften = 10, client = false) {
                for (blockPos in generateSphere(entity.blockPos, 3, false)) {
                    val blockState = player.world.getBlockState(blockPos)
                    if (blockState.isAir || blockState.block == Blocks.BEDROCK) continue
                    player.world.breakBlock(blockPos, false, player)
                }
            }
        }
    }
}
