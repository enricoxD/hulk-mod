package gg.norisk.hulk

import gg.norisk.heroes.common.HeroInitializer
import gg.norisk.hulk.registry.EntityRegistry
import gg.norisk.hulk.registry.SoundRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object HulkManager : HeroInitializer(), ModInitializer, DedicatedServerModInitializer, ClientModInitializer {
    override val hero = Hulk

    override fun onInitialize() {
        EntityRegistry.init()
        SoundRegistry.init()
    }

    override fun onInitializeServer() {}

    override fun onInitializeClient() {}
}

fun String.toId() = Identifier("hulk", this)