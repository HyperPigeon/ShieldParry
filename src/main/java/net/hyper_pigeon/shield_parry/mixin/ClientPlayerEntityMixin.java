package net.hyper_pigeon.shield_parry.mixin;

import net.hyper_pigeon.shield_parry.ShieldParry;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "sendMovementPackets", at = @At(value = "HEAD"), cancellable = true)
    public void onSendMovementPackets(CallbackInfo ci){
        if(ShieldParry.isWorldFrozen()){
            ci.cancel();
        }
    }

}
