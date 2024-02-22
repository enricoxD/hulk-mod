package gg.norisk.hulk.mixin;

import gg.norisk.heroes.common.hero.IHeroManagerKt;
import gg.norisk.hulk.HulkKt;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    @Shadow
    protected abstract void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity);

    public PlayerEntityRendererMixin(EntityRendererFactory.Context context, PlayerEntityModel<AbstractClientPlayerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true)
    private void renderArmInjection(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart modelPart, ModelPart modelPart2, CallbackInfo ci) {
        if (!player.getPassengerList().isEmpty() && IHeroManagerKt.isHero(player, HulkKt.getHulk())) {
            ci.cancel();
            PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
            this.setModelPose(player);
            playerEntityModel.handSwingProgress = 0.0F;
            playerEntityModel.sneaking = false;
            playerEntityModel.leaningPitch = 0.0F;
            playerEntityModel.setAngles(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            modelPart.pitch = 0.0F;
            modelPart2.pitch = 0.0F;
            modelPart.pivotY = 6.0F;
            modelPart2.pivotY = 6.0F;
            modelPart.pitch -= 0.5235988F;
            modelPart.roll += (modelPart == playerEntityModel.rightArm ? 30.0F : -30.0F) * 0.017453292F;
            modelPart2.pitch -= 0.5235988F;
            modelPart2.roll += (modelPart2 == playerEntityModel.rightSleeve ? 30.0F : -30.0F) * 0.017453292F;

            modelPart.render(matrixStack, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
            modelPart2.render(matrixStack, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
        }
    }
}
