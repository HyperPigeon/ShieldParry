package net.hyper_pigeon.shield_parry.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hyper_pigeon.shield_parry.ShieldParry;
import net.hyper_pigeon.shield_parry.interfaces.ParryingEntity;
import net.hyper_pigeon.shield_parry.networking.ShieldParryNetworkingConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import java.awt.*;


@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ShieldParryClient implements ClientModInitializer {
//    public static KeyBinding PARRY = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.shield_parry.parry", InputUtil.Type.KEYSYM,
//            GLFW.GLFW_KEY_R, "Shield Parry"));

//    private float animationProgress = 0;


    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ShieldParryNetworkingConstants.PARRYING, (client, handler, buf, responseSender) -> {
            ParryingEntity parryingPlayer = (ParryingEntity) client.player;
            parryingPlayer.startParrying();
        });

        ClientPlayNetworking.registerGlobalReceiver(ShieldParryNetworkingConstants.FREEZE, (client, handler, buf, responseSender) -> {
            if(MinecraftClient.getInstance().isInSingleplayer()) {
                ShieldParry.freezeWorld(client.player.getWorld(), buf.readInt());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
                    ShieldParry.tickFrozenWorld();
        });

        HudRenderCallback.EVENT.register((drawContext, delta) -> {
            if(ShieldParry.isWorldFrozen()) {
                drawContext.fill(RenderLayer.getGuiOverlay(),0,0,drawContext.getScaledWindowWidth(),drawContext.getScaledWindowHeight(),-1275068417);
            }
        });

    }

    public static <E extends LivingEntity> void updatePlayerModel(BipedEntityModel<E> model, ParryingEntity player, float tickDelta) {
        if (player.isParrying()) {
            float delta = player.parryDelta(tickDelta);

            final float targetPitch = (float) Math.toRadians(-125f);
            final float targetYaw = (float) Math.toRadians(-50f);

            ModelPart parryingArm = player.getParryingArm() == Arm.LEFT? model.leftArm : model.rightArm;
            parryingArm.pitch = MathHelper.lerp(delta,parryingArm.pitch, targetPitch);
            parryingArm.yaw = MathHelper.lerp(delta, parryingArm.yaw, targetYaw);
//            model.leftArm.pitch = MathHelper.lerp(delta, model.leftArm.pitch, targetPitch);

//            final float targetYaw = (float) Math.toRadians(-75f);
//            model.rightArm.yaw = MathHelper.lerp(delta, model.rightArm.yaw, -targetYaw);
//            model.leftArm.yaw = MathHelper.lerp(delta, model.leftArm.yaw, targetYaw);
        }
    }


}
