package net.hyper_pigeon.shield_parry.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.shield_parry.ShieldParry;
import net.hyper_pigeon.shield_parry.interfaces.Parry;
import net.hyper_pigeon.shield_parry.interfaces.ParryableProjectile;
import net.hyper_pigeon.shield_parry.interfaces.ParryingEntity;
import net.hyper_pigeon.shield_parry.networking.ShieldParryNetworkingConstants;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShieldItem.class)
public abstract class ShieldItemMixin extends Item implements Parry {

    private int parryTicks = 0;

    public ShieldItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.setCurrentHand (Lnet/minecraft/util/Hand;)V", shift = At.Shift.AFTER))
    public void startParryTicks(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir){
        ItemStack itemStack = user.getStackInHand(hand);
        if(EnchantmentHelper.getLevel(ShieldParry.PARRY,itemStack) > 0) {
            parryTicks = 50;
        }

    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks){
        int level = EnchantmentHelper.getLevel(ShieldParry.PARRY,stack);
        if(level > 0 && parryTicks > 0) {

            Vec3d shieldPos = user.getHandPosOffset(Items.SHIELD);

            for(int i = 0; i < 9; i++){

            }

            /*
                Spawns yellow particles around shield during parry window
            */



            Vec3d rotationVector = user.getRotationVector().normalize();
            Vec3d eyePos = user.getEyePos();
            Box parryBox = new Box(eyePos.x - 0.45f, eyePos.y - 0.45f, eyePos.z - 0.45f,
                    eyePos.x + 0.45f, eyePos.y + 0.45f, eyePos.z + 0.45f)
                    .stretch(rotationVector.multiply(0.9));
            List<PersistentProjectileEntity> projectiles = user.getEntityWorld().getEntitiesByClass(PersistentProjectileEntity.class, parryBox,
                    (projectile) -> true);
            if(!projectiles.isEmpty()) {
                if(user instanceof ServerPlayerEntity serverPlayerEntity){

                    serverPlayerEntity.disableShield(true);

                    ParryingEntity parryingEntity = (ParryingEntity)serverPlayerEntity;
                    parryingEntity.startParrying();

                    ServerPlayNetworking.send(serverPlayerEntity, ShieldParryNetworkingConstants.PARRYING, PacketByteBufs.empty());

                    serverPlayerEntity.getServerWorld().playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.NEUTRAL
                            , 3.0f, 1f);

                    for(PersistentProjectileEntity projectileEntity : projectiles){
                        projectileEntity.setOwner(user);
                        ParryableProjectile parryableProjectile = (ParryableProjectile) projectileEntity;
                        parryableProjectile.setParried(level);
                        projectileEntity.setVelocity(rotationVector.getX(),rotationVector.getY(),rotationVector.getZ(),2.25F,0);
                    };


                    ShieldParry.freezeWorld((ServerWorld)world,50);

                }
            }
            parryTicks--;
        }


    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if(EnchantmentHelper.getLevel(ShieldParry.PARRY,stack) > 0){
            parryTicks=0;
        }
    }

    public int getParryTicks(){
        return parryTicks;
    }



}
