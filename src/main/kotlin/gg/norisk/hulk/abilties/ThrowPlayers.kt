package gg.norisk.hulk.abilties

import gg.norisk.heroes.common.events.PlayerInteractAtEntityEvent
import gg.norisk.heroes.common.events.PlayerSwingHandEvent
import gg.norisk.heroes.common.hero.ability.implementation.Ability
import gg.norisk.hulk.registry.SoundRegistry
import gg.norisk.hulk.player.isHulk
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.entity.modifyVelocity

val ThrowPlayers by Ability("Throw Players", 0) {
    showInKeybindHud = false

    fun ServerPlayerEntity.throwPassengers() {
        if (isHulk && hasPassengers()) {
            world.playSoundAtBlockCenter(
                blockPos,
                SoundRegistry.getRandomGrowlSound(),
                SoundCategory.PLAYERS,
                1f,
                1f,
                false
            )
            val passengers = passengerList.toList()
            removeAllPassengers()
            for (passenger in passengers) {
                passenger as PlayerEntity
                passenger.modifyVelocity(directionVector.normalize().multiply(3.5))
            }
        }
    }

    serverListen<PlayerInteractAtEntityEvent>(playerGetter = { it.player}) { event ->
        val player = event.player as? ServerPlayerEntity ?: return@serverListen
        player.passengerList.add(event.rightClicked)
    }

    serverListen<PlayerSwingHandEvent>(playerGetter = { it.player }) { event ->
        val player = event.player as? ServerPlayerEntity ?: return@serverListen
        player.throwPassengers()
    }
}