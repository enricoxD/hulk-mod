package gg.norisk.hulk.renderer

import gg.norisk.hulk.entity.HulkEntity
import gg.norisk.hulk.toId
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.DefaultedEntityGeoModel

class HulkModel : DefaultedEntityGeoModel<HulkEntity>("hulk".toId()) {
    // We want our model to render using the translucent render type
    override fun getRenderType(animatable: HulkEntity, texture: Identifier): RenderLayer {
        return RenderLayer.getEntityTranslucent(getTextureResource(animatable))
    }
}
