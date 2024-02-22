package gg.norisk.hulk.mixin;

import gg.norisk.heroes.common.hero.IHeroManagerKt;
import gg.norisk.hulk.HulkKt;
import gg.norisk.hulk.player.IHulkPlayer;
import gg.norisk.hulk.events.AttackBlockEvent;
import me.obsilabor.alert.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private GameMode gameMode;

    @Inject(method = "attackBlock", at = @At("HEAD"))
    private void doAttackInjection(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;
        var player = MinecraftClient.getInstance().player;
        var event = new AttackBlockEvent(player, blockPos, direction);
        EventManager.callEvent(event);
    }

    //Für Blöcke -> Blöcke anvisieren hat höhere / andere Range
    @Inject(at = {@At("HEAD")}, method = {"getReachDistance()F"}, cancellable = true)
    private void onGetReachDistance(CallbackInfoReturnable<Float> cir) {
        if (this.client.player instanceof IHulkPlayer hulkPlayer && IHeroManagerKt.isHero(client.player, HulkKt.getHulk())) {
            cir.setReturnValue(this.gameMode.isCreative() ? hulkPlayer.getGetCustomCreativeBlockReachDistance() : hulkPlayer.getGetCustomBlockReachDistance());
        }
    }
}
