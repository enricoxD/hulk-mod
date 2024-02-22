package gg.norisk.hulk.mixin;

import gg.norisk.heroes.common.hero.IHeroManagerKt;
import gg.norisk.hulk.HulkKt;
import gg.norisk.hulk.HulkManagerKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique
    private static final Identifier FULL = HulkManagerKt.toId("textures/green_heart_full.png");
    @Unique
    private static final Identifier HALF = HulkManagerKt.toId("textures/green_heart_half.png");

    @Inject(method = "drawHeart", at = @At("HEAD"), cancellable = true)
    private void drawHeartInjection(DrawContext drawContext, InGameHud.HeartType heartType, int i, int j, int k, boolean bl, boolean bl2, CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        if (heartType == InGameHud.HeartType.CONTAINER) return;
        if (player == null) return;
        if (IHeroManagerKt.isHero(player, HulkKt.getHulk())) {
            if (bl) {
                ci.cancel();
                return;
            }
            if (bl2) {
                drawContext.drawTexture(HALF, i, j, 9, 9, 9, 9, 9, 9);
            } else {
                drawContext.drawTexture(FULL, i, j, 9, 9, 9, 9, 9, 9);
            }
            ci.cancel();
        }
    }
}
