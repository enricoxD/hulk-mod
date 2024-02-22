package gg.norisk.hulk

import gg.norisk.heroes.common.config.trackeddata.trackedData
import gg.norisk.heroes.common.hero.Hero
import gg.norisk.heroes.common.hero.HeroConfig
import gg.norisk.hulk.abilties.*
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.player.PlayerEntity

class HulkConfig: HeroConfig("Hulk") {
    var size by float(1.3f)
    val stepHeight by float(1.6f)
}

val Hulk by Hero("Hulk", ::HulkConfig) {
    color = 0x47CD45
    ability(ThunderClap)
    ability(Punch)
    ability(Jump)
    ability(ThrowPlayers)

    getSkin {"textures/hulk_skin.png".toId() }
    onEnable { player ->
        trackedStepHeight.set(player, config.stepHeight)
    }

    onDisable { player ->
        trackedStepHeight.set(player, trackedStepHeight.default)
    }

    dynamicEntityDimensions {
        getEyeHeight { player, entityPose, entityDimensions ->
            player.getDimensions(entityPose).height
        }

        getDimensions { player, entityPose ->
            EntityDimensions.fixed(1.2f, 2.5f)
        }
    }
}

val trackedStepHeight = trackedData(0.6f).onSet { player, newValue ->
    player.stepHeight = newValue
}