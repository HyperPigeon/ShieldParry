package net.hyper_pigeon.shield_parry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.shield_parry.enchantment.ParryEnchantment;
import net.hyper_pigeon.shield_parry.networking.ShieldParryNetworkingConstants;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;


public class ShieldParry implements ModInitializer {

    public final static int PARRYING_TICKS = 10;
    public static Enchantment PARRY;

    private static int freezeTicks;

    @Override
    public void onInitialize() {

        PARRY = Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("shield", "parry"),
                new ParryEnchantment(
                        Enchantment.Rarity.RARE,
                        EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)
        );

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ShieldParry.tickFrozenWorld();
        });
    }

    public static void freezeWorld(World world, int freezeTicks){
        if(!world.isClient()){
            ServerWorld serverWorld = (ServerWorld) world;
            for(ServerPlayerEntity player : serverWorld.getPlayers()) {
                PacketByteBuf packetByteBuf = PacketByteBufs.create();
                packetByteBuf.writeInt(freezeTicks);
                ServerPlayNetworking.send(player, ShieldParryNetworkingConstants.FREEZE, packetByteBuf);
            }
        }
        ShieldParry.freezeTicks+=freezeTicks;
    }

    public static boolean isWorldFrozen(){
        return ShieldParry.freezeTicks > 0;
    }

    public static void tickFrozenWorld(){
        if(isWorldFrozen()){
            ShieldParry.freezeTicks--;
        }
    }




}
