package net.hyper_pigeon.shield_parry.mixin;

import net.hyper_pigeon.shield_parry.ShieldParry;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        ShieldParry.tickFrozenWorld();
        if(ShieldParry.isWorldFrozen()){
            ci.cancel();
        }
    }
}
