package gg.norisk.hulk.mixin;

import gg.norisk.heroes.common.hero.IHeroManagerKt;
import gg.norisk.hulk.HulkKt;
import gg.norisk.hulk.player.IHulkPlayer;
import gg.norisk.hulk.registry.SoundRegistry;
import gg.norisk.hulk.utils.HulkUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IHulkPlayer {
    @Shadow
    protected HungerManager hungerManager;

    @Shadow
    protected abstract boolean clipAtLedge();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public float getJumpBoostVelocityModifier() {
        if (IHeroManagerKt.isHero((PlayerEntity) (Object) this, HulkKt.getHulk())) {
            return 0.5f;
        } else {
            return super.getJumpBoostVelocityModifier();
        }
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void regenerationInjection(CallbackInfo ci) {
        if (IHeroManagerKt.isHero((PlayerEntity) (Object) this, HulkKt.getHulk())) {
            if (this.getHealth() < this.getMaxHealth() && this.age % 3 == 0) {
                this.heal(1.0F);
            }

            if (this.hungerManager.isNotFull() && this.age % 5 == 0) {
                this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
            }
        }
    }

    @Inject(method = "getHurtSound", at = @At("RETURN"), cancellable = true)
    private void getHurtSoundInjection(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> cir) {
        if (IHeroManagerKt.isHero((PlayerEntity) (Object) this, HulkKt.getHulk())) {
            cir.setReturnValue(SoundRegistry.INSTANCE.getRandomGrowlSound());
        }
    }

    @Inject(method = "getFallSounds", at = @At("RETURN"), cancellable = true)
    private void getFallSoundsInjection(CallbackInfoReturnable<FallSounds> cir) {
        if (IHeroManagerKt.isHero((PlayerEntity) (Object) this, HulkKt.getHulk())) {
            cir.setReturnValue(new FallSounds(SoundRegistry.INSTANCE.getRandomGrowlSound(), SoundRegistry.INSTANCE.getRandomGrowlSound()));
        }
    }

    @Override
    public void setGetCustomAttackReachDistance(double v) {

    }

    @Override
    public double getGetCustomAttackReachDistance() {
        return 6.0;
    }

    @Override
    public void setGetCustomCreativeAttackReachDistance(double v) {

    }

    @Override
    public double getGetCustomCreativeAttackReachDistance() {
        return 6.0;
    }

    @Override
    public void setGetCustomBlockReachDistance(float v) {

    }

    @Override
    public float getGetCustomBlockReachDistance() {
        return 8.0f;
    }

    @Override
    public void setGetCustomCreativeBlockReachDistance(float v) {

    }

    @Override
    public double getMountedHeightOffset() {
        if (IHeroManagerKt.isHero((PlayerEntity) (Object) this, HulkKt.getHulk())) {
            return (double) this.getDimensions(this.getPose()).height;
        } else {
            return super.getMountedHeightOffset();
        }
    }

    @Override
    public float getGetCustomCreativeBlockReachDistance() {
        return 8.0f;
    }
}
