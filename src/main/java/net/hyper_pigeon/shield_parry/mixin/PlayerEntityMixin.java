package net.hyper_pigeon.shield_parry.mixin;

import net.hyper_pigeon.shield_parry.ShieldParry;
import net.hyper_pigeon.shield_parry.interfaces.ParryingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ParryingEntity {

    @Shadow public abstract Arm getMainArm();

    @Unique
    private int parryingTicks;

    @Override
    public void startParrying() {
        this.parryingTicks = ShieldParry.PARRYING_TICKS;
    }

    @Override
    public boolean isParrying() {
        return parryingTicks > 0;
    }

    @Override
    public Arm getParryingArm(){
        return getMainArm().getOpposite();
    }

    public float parryDelta(float tickDelta) {
        float progress = (ShieldParry.PARRYING_TICKS - this.parryingTicks) + tickDelta;
        return Math.min(progress / ShieldParry.PARRYING_TICKS, 1f);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.isParrying()) {
            parryingTicks--;
        }
    }



}
