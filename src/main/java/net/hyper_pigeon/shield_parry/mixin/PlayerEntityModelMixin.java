package net.hyper_pigeon.shield_parry.mixin;

import net.hyper_pigeon.shield_parry.client.ShieldParryClient;
import net.hyper_pigeon.shield_parry.interfaces.ParryingEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin  {

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", shift = At.Shift.AFTER))
    private void onSetAngles(LivingEntity livingEntity,  float f, float limbDistance, float animationProgress, float i, float pitch, CallbackInfo ci) {
        if (livingEntity instanceof ParryingEntity player) {
            ShieldParryClient.updatePlayerModel((PlayerEntityModel<LivingEntity>) (Object) this, player, animationProgress - livingEntity.age);
        }
    }

}

