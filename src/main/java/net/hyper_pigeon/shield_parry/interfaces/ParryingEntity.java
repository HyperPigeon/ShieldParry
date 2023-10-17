package net.hyper_pigeon.shield_parry.interfaces;

import net.minecraft.util.Arm;

public interface ParryingEntity {
    void startParrying();
    boolean isParrying();
    float parryDelta(float tickDelta);
    Arm getParryingArm();


}
