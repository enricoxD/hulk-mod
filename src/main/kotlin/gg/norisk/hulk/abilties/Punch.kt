package gg.norisk.hulk.abilties

import de.hglabor.notify.events.player.PlayerAttackEntityEvent
import gg.norisk.heroes.common.Manager
import gg.norisk.heroes.common.hero.ability.AbilityPacketDescription
import gg.norisk.heroes.common.hero.ability.implementation.Ability
import gg.norisk.hulk.events.PlayerStartBlockBreakEvent
import gg.norisk.hulk.registry.SoundRegistry
import gg.norisk.hulk.utils.HulkUtils
import gg.norisk.hulk.utils.HulkUtils.smashEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.silkmc.silk.core.Silk
import net.silkmc.silk.core.text.broadcastText
import net.silkmc.silk.core.text.literal
import kotlin.random.Random

val Punch by Ability("Punch", 10) {

    serverListen<PlayerStartBlockBreakEvent>(playerGetter = { it.player }) {
        println("start block break")
        Manager.abilityManager.useAbility(it.player, ability, AbilityPacketDescription.Use())
    }

    serverListen<PlayerAttackEntityEvent>(playerGetter = { it.player }) {
        smashEntity(it.player, it.target)
    }

    handle {
        server { player, description ->
            Silk.server?.broadcastText("received Punch".literal)
            val world = player.world
            val pos = BlockPos(3,3,3)
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
        }
    }
}