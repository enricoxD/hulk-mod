package gg.norisk.hulk.abilties

import gg.norisk.heroes.common.Manager
import gg.norisk.heroes.common.hero.ability.AbilityPacketDescription
import gg.norisk.heroes.common.hero.ability.implementation.Ability
import gg.norisk.hulk.registry.SoundRegistry
import gg.norisk.hulk.utils.HulkUtils
import gg.norisk.hulk.events.AttackBlockEvent
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import kotlin.random.Random

/*@Serializable
data class PunchUsePacket(
    val startX: Int,
    val startY: Int,
    val startZ: Int
) : AbilityPacketDescription.Use()*/

val Punch by Ability("Punch", 10) {

    clientListen<AttackBlockEvent> {
        val player = MinecraftClient.getInstance().player ?: return@clientListen
        val pos = it.blockPos
        Manager.abilityManager.useAbility(player, ability, AbilityPacketDescription.Use())
    }

    handle {
        server { player, description ->
/*
            if (description !is PunchUsePacket) return@server
            val world = player.world
            val pos = BlockPos(description.startX, description.startY, description.startZ)
            world.playSoundAtBlockCenter(
                pos,
                SoundRegistry.getRandomGrowlSound(),
                SoundCategory.PLAYERS,
                1f,
                1f,
                false
            )

            for (blockPos in HulkUtils.generateSphere(pos, 4, false)) {
                val state = player.world.getBlockState(blockPos)
                if (state.isAir) continue
                if (Direction.values().all { state.isSideInvisible(state, it) }) continue

                val damage = Random.nextInt(1, 10)

                if (Random.nextInt(100) > 60) continue
                if (Random.nextInt(100) > 90) {
                    world.playSoundAtBlockCenter(
                        blockPos,
                        SoundRegistry.CRACK,
                        SoundCategory.BLOCKS,
                        1f,
                        Random.nextDouble(0.6, 1.0).toFloat(),
                        false
                    )
                }

                player.world.setBlockBreakingInfo(Random.nextInt(), blockPos, damage)
            }
*/
        }
    }
}