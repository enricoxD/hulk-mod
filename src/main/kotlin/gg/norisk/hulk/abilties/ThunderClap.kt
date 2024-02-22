package gg.norisk.hulk.abilties

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import gg.norisk.heroes.common.Manager
import gg.norisk.heroes.common.config.trackeddata.trackedData
import gg.norisk.heroes.common.hero.ability.implementation.PressAbility
import gg.norisk.heroes.common.registry.SoundRegistry
import gg.norisk.heroes.common.utils.SphereUtils
import gg.norisk.hulk.toId
import gg.norisk.hulk.utils.CameraShaker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.entity.damage.DamageSources
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.GameMode
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
import org.lwjgl.glfw.GLFW
import java.time.Duration
import kotlin.math.min
import kotlin.random.Random

val ThunderClap by PressAbility("Thunderclap", 10000, 3) {
    keyBindCode = GLFW.GLFW_KEY_G
    val damage by float(5.0f)

    val blockBreakingInfos: Cache<BlockPos, Pair<Int, Int>> =
        CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build()

    handle {
        ownClient { player, description ->
            CoroutineScope(Dispatchers.IO).launch {
                delay(7.ticks)
                CameraShaker.addEvent(CameraShaker.BoomShake(1.0, 0.1, 0.3))
            }
        }

        server { player, _ ->
            Manager.animationManager.playAnimation(player, "thunderclap".toId())
            mcCoroutineTask(delay = 7.ticks) {
                val world = player.world as ServerWorld
                val startEyePos = player.eyePos
                val direction = player.directionVector.normalize()
                val startPos = player.pos.add(0.0, 0.3, 0.0).add(direction.multiply(2.0))

                player.world.playSoundAtBlockCenter(
                    player.blockPos,
                    SoundRegistry.getRandomGrowlSound(),
                    SoundCategory.PLAYERS,
                    1f,
                    1f,
                    true
                )
                player.world.playSoundAtBlockCenter(
                    player.blockPos,
                    SoundRegistry.BOOM,
                    SoundCategory.AMBIENT,
                    1f,
                    1f,
                    true
                )
                player.world.playSoundAtBlockCenter(
                    player.blockPos,
                    SoundRegistry.CLAP,
                    SoundCategory.PLAYERS,
                    1f,
                    1f,
                    true
                )

                mcCoroutineTask(howOften = 20) { task ->
                    val center = startPos.add(direction.multiply(task.round.toDouble()))
                    val centerEyePos = startEyePos.add(direction.multiply(task.round.toDouble()))
                    val centerBlockPos = BlockPos(center.x.toInt(), center.y.toInt(), center.z.toInt())
                    world.spawnParticles(
                        ParticleTypes.EXPLOSION, centerEyePos.x, centerEyePos.y, centerEyePos.z, 1, 0.0, 0.0, 0.0, 1.0
                    )

                    for (blockPos in SphereUtils.generateSphere(centerBlockPos, 3, false)) {
                        val state = world.getBlockState(blockPos)
                        world.getOtherEntities(player, Box(blockPos.add(-1, -1, -1), blockPos.add(1, 1, 1))).onEach { entity ->
                            if (!entity.isAlive) return@onEach
                            if (entity is ServerPlayerEntity && !entity.interactionManager.gameMode.isSurvivalLike) return@onEach
                            entity.damage(player.world.damageSources.playerAttack(player), damage)

                        }
                        if (state.isAir) continue
                        if (Direction.values().all { state.isSideInvisible(state, it) }) continue

                        val cachedDamage = blockBreakingInfos.getIfPresent(blockPos) ?: Pair(Random.nextInt(), 0)
                        val blockDamage = if (cachedDamage.second == 0) {
                            Random.nextInt(1, 5)
                        } else {
                            min(10, cachedDamage.second + Random.nextInt(1, 3))
                        }

                        if (blockDamage == 10) {
                            world.setBlockState(blockPos, Blocks.AIR.defaultState)
                        } else {
                            if (Random.nextInt(100) > 95) {
                                world.playSoundAtBlockCenter(
                                    blockPos,
                                    SoundRegistry.CRACK,
                                    SoundCategory.BLOCKS,
                                    1f,
                                    Random.nextDouble(0.6, 1.0).toFloat(),
                                    false
                                )
                            }
                        }

                        blockBreakingInfos.put(blockPos, Pair(cachedDamage.first, blockDamage))
                        world.setBlockBreakingInfo(cachedDamage.first, blockPos, blockDamage)
                    }
                }
            }
        }
    }
}