package github.mishkis.twodimensional.client;

import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.client.rendering.TwoDimensionalCrosshairRenderer;
import github.mishkis.twodimensional.client.rendering.TwoDimensionalShaders;
import github.mishkis.twodimensional.duck_interface.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;

public class TwoDimensionalClient implements ClientModInitializer {
    public static Plane plane = null;

    private boolean shouldUpdatePlane = true;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TwoDimensional.PLANE_SYNC, ((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
            plane = new Plane(new Vec3d(packetByteBuf.readDouble(), 0, packetByteBuf.readDouble()), packetByteBuf.readDouble());
            shouldUpdatePlane = true;
        }));
        ClientPlayNetworking.registerGlobalReceiver(TwoDimensional.PLANE_REMOVE, ((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
            plane =  null;
            shouldUpdatePlane = true;
        }));

        ClientTickEvents.START_CLIENT_TICK.register((client -> {
            if (shouldUpdatePlane && client.player != null) {
                ((EntityPlaneGetterSetter) client.player).twoDimensional$setPlane(plane);
                client.worldRenderer.reload();
                shouldUpdatePlane = false;
            }
        }));

        PostWorldRenderCallback.EVENT.register(TwoDimensionalShaders.INSTANCE);
        ShaderEffectRenderCallback.EVENT.register(TwoDimensionalShaders.INSTANCE);
        TwoDimensionalCrosshairRenderer.intialize();
    }
}
