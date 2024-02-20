package gg.norisk.hulk.renderer

import gg.norisk.hulk.entity.HulkEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class HulkRenderer(renderManager: EntityRendererFactory.Context) : GeoEntityRenderer<HulkEntity>(renderManager, HulkModel())
