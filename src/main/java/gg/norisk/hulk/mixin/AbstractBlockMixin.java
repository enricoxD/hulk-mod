package gg.norisk.hulk.mixin;

import gg.norisk.hulk.events.PlayerStartBlockBreakEvent;
import me.obsilabor.alert.EventManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    @Inject(method = "onBlockBreakStart", at = @At("HEAD"))
    public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, CallbackInfo ci) {
        var event = new PlayerStartBlockBreakEvent(playerEntity, blockState, blockPos);
        EventManager.callEvent(event);
    }
}
