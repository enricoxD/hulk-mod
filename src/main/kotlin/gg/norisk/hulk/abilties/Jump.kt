package gg.norisk.hulk.abilties

import gg.norisk.heroes.common.hero.ability.implementation.HoldAbility
import gg.norisk.heroes.common.isServer
import gg.norisk.hulk.registry.SoundRegistry
import gg.norisk.hulk.utils.CameraShaker
import gg.norisk.hulk.utils.HulkUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.entity.posUnder
import org.lwjgl.glfw.GLFW
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

val Jump by HoldAbility("Jump", 10, 15000) {
    keyBindCode = GLFW.GLFW_KEY_V
    val maxJumpStrength by double(10.0)
    val jumpStart = if (isServer) hashMapOf<UUID, Long>() else null
    val jumpingPlayers = hashMapOf<PlayerEntity, Long>()
    val ICONS = Identifier("textures/gui/icons.png")

    fun createCracks(player: PlayerEntity, startPos: BlockPos, radius: Int) {
        for (blockPos in HulkUtils.generateSphere(startPos, radius, false)) {
            val state = player.world.getBlockState(blockPos)
            if (state.isAir) continue
            if (Direction.values().all { state.isSideInvisible(state, it) }) continue
            if (Random.nextInt(100) > 70) continue

            // Berechne die Distanz zwischen startPos und blockPos
            val distance = startPos.getSquaredDistance(blockPos)
            // Berechne den Prozentsatz der maximalen Distanz
            val distancePercentage = 1.0 - (distance / radius)
            // Skaliere die progressStrength basierend auf dem Prozentsatz
            var progressStrength = (distancePercentage * 10).toInt()
            // Füge einen zufälligen Wert zwischen -2 und 2 hinzu
            val randomBonus = Random.nextInt(-3, 4)
            progressStrength = min(9, max(-1, progressStrength + randomBonus))

            if (Random.nextBoolean()) {
                player.world.playSoundAtBlockCenter(
                    blockPos,
                    state.soundGroup.breakSound,
                    SoundCategory.BLOCKS,
                    0.3f,
                    Random.nextDouble(0.8, 1.0).toFloat(),
                    true
                )
            }
            //player.world.addBlockBreakParticles(blockPos,state)
            player.world.setBlockBreakingInfo(Random.nextInt(), blockPos, progressStrength)
        }
    }

    fun handleCracks(player: PlayerEntity, jumpStrength: Double) {
        CameraShaker.addEvent(CameraShaker.BoomShake(0.1 * jumpStrength, 0.1, 0.3))

        val startPos = player.posUnder
        MinecraftClient.getInstance().soundManager.play(
            PositionedSoundInstance.master(
                SoundRegistry.JUMP,
                1.0f,
                Random.nextDouble(0.7, 1.3).toFloat()
            )
        )
        val radius = jumpStrength.toInt() * 4
        createCracks(player, startPos, radius)
    }

    fun handleLandingClient() {
        CameraShaker.addEvent(CameraShaker.BoomShake(Random.nextDouble(0.3, 0.5), 0.1, 0.3))
    }

    fun handleLandingServer(player: PlayerEntity) {
        player.world.playSoundAtBlockCenter(
            player.blockPos,
            SoundRegistry.JUMP,
            SoundCategory.BLOCKS,
            0.3f,
            Random.nextDouble(0.8, 1.0).toFloat(),
            true
        )
        createCracks(player, player.posUnder, Random.nextInt(6, 14))
    }

    fun renderJumpStrengthBar(drawContext: DrawContext, jumpStrength: Double) {
        val client = MinecraftClient.getInstance()
        val i = client.window.scaledWidth / 2 - 91
        var l: Int
        var m: Int
        if (jumpStrength > 0.0) {
            l = (jumpStrength / maxJumpStrength * 183.0f).toInt()
            m = client.window.scaledHeight - 32 + 3
            drawContext.drawTexture(ICONS, i, m, 0, 64, 182, 5)
            if (l > 0) {
                drawContext.drawTexture(ICONS, i, m, 0, 69, l, 5)
            }
        }
        if (jumpStrength > 0.0) {
            val percentage = (jumpStrength * maxJumpStrength.toInt()).toInt()
            val string = "" + percentage
            l = (client.window.scaledWidth - client.textRenderer.getWidth(string)) / 2
            m = client.window.scaledHeight - 31 - 4
            drawContext.drawText(client.textRenderer, string, l + 1, m, 0, false)
            drawContext.drawText(client.textRenderer, string, l - 1, m, 0, false)
            drawContext.drawText(client.textRenderer, string, l, m + 1, 0, false)
            drawContext.drawText(client.textRenderer, string, l, m - 1, 0, false)
            drawContext.drawText(client.textRenderer, string, l, m, 8453920, false)
        }
    }

    handle {
        start {
            server { player, description ->
                jumpStart?.set(player.uuid, System.currentTimeMillis())
            }
        }

        end {
            server { player, description ->
                val jumpStartTime = jumpStart?.get(player.uuid) ?: return@server
                val chargingTicks = System.currentTimeMillis() - jumpStartTime / 50
                val jumpStrength = min(0.2 * chargingTicks, maxJumpStrength)
                val direction = player.directionVector.normalize().multiply(jumpStrength)
                jumpingPlayers[player] = System.currentTimeMillis()
                handleCracks(player, jumpStrength)
                player.modifyVelocity(direction)
            }

            ownClient { player, description ->
                jumpingPlayers[player] = System.currentTimeMillis()
            }
        }
    }

    onTick {
        jumpingPlayers.forEach { player, jumpStartTime ->
            if (!player.isOnGround) return@forEach
            val jumpTicks = System.currentTimeMillis() - jumpStartTime / 50

            if (jumpTicks < 5) return@forEach
            if (isServer) handleLandingServer(player)
            else handleLandingClient()
        }
    }
}