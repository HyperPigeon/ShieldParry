package net.hyper_pigeon.shield_parry.mixin;

import net.hyper_pigeon.shield_parry.ShieldParry;
import net.hyper_pigeon.shield_parry.interfaces.ParryableProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity implements ParryableProjectile {
    @Shadow @Nullable public abstract Entity getOwner();

    private boolean frozen = false;
    private Vec3d preFrozenVec;
    private int parryLevel = 0;

    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void onTick(CallbackInfo ci){
        if(ShieldParry.isWorldFrozen()){
            if(!getFrozen()) {
                preFrozenVec = this.getVelocity();
                setFrozen(true);
            }
            this.setVelocity(0,0,0);
            ci.cancel();
        }
        else if(getFrozen()){
            this.setVelocity(preFrozenVec);
            setFrozen(false);
        }
    }

    @Inject(method = "updateRotation()V", at = @At("HEAD"), cancellable = true)
    void OnUpdateRotation(CallbackInfo ci) {
        if(frozen)
            ci.cancel();
    }

    @Inject(method = "onEntityHit", at = @At("TAIL"))
    void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci){
        if(!entityHitResult.getEntity().equals(getOwner())){
            handleParriedCollision(getParryLevel(),entityHitResult);
        }
    }

    @Inject(method = "onBlockHit", at = @At("TAIL"))
    void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci){
        handleParriedCollision(getParryLevel(), blockHitResult);
    }


    public boolean isParried(){
        return parryLevel > 0;
    }

    public void setParried(int parryLevel){
        this.parryLevel = parryLevel;
    }

    public int getParryLevel(){
        return this.parryLevel;
    }

    public void setFrozen(boolean frozen){
        this.frozen = frozen;
    }

    public boolean getFrozen(){
        return this.frozen;
    }

    void handleParriedCollision(int level, HitResult hitResult){
        if(level == 3){
            getEntityWorld().createExplosion(getOwner(), hitResult.getPos().getX(),hitResult.getPos().getY(),hitResult.getPos().getZ(),5.0F, false, World.ExplosionSourceType.NONE);
            remove(RemovalReason.DISCARDED);
        }
    }



}
