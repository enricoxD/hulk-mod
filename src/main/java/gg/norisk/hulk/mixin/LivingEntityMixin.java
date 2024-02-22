package gg.norisk.hulk.mixin;

import gg.norisk.heroes.common.hero.IHeroManagerKt;
import gg.norisk.hulk.HulkKt;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    public LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "computeFallDamage", constant = @Constant(floatValue = 3.0F))
    private float computeFallDamageInjection(float constant) {
        if ((LivingEntity) ((Object) this) instanceof PlayerEntity player && IHeroManagerKt.isHero(player, HulkKt.getHulk())) {
            return 9.0f;
        } else {
            return constant;
        }
    }

    @ModifyArgs(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void handleFallDamageInjection(Args args) {
        if ((LivingEntity) ((Object) this) instanceof PlayerEntity player && IHeroManagerKt.isHero(player, HulkKt.getHulk())) {
            args.set(1, (float) args.get(1) / 3f);
        }
    }
}
