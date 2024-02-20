package gg.norisk.hulk

import gg.norisk.heroes.common.hero.Hero
import gg.norisk.heroes.common.hero.HeroConfig
import gg.norisk.hulk.abilties.*
import net.minecraft.entity.EntityDimensions

class HulkConfig: HeroConfig("Hulk") {
    var size by float(1.3f)
}

val Hulk = Hero("Hulk", ::HulkConfig) {
    color = 0x47CD45
    ability(ThunderClap)
    ability(Punch)
    ability(Jump)
    ability(ThrowPlayers)

    getSkin {"textures/hulk_skin.png".toId() }
    onEnable {

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