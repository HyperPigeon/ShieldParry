package net.hyper_pigeon.shield_parry.interfaces;

public interface ParryableProjectile {
    void setParried(int level);
    boolean isParried();
    int getParryLevel();
    void setFrozen(boolean frozen);
    boolean getFrozen();
}
