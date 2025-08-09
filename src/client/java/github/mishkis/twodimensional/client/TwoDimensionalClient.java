package github.mishkis.twodimensional.client;

import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.client.rendering.TwoDimensionalShaders;
import github.mishkis.twodimensional.utils.Plane;
import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.Vec3d;

public class TwoDimensionalClient implements ClientModInitializer {
    public static Plane plane = new Plane(new Vec3d(0.5, 0, 0.5), 0);

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TwoDimensional.INITIAL_SYNC, ((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
            plane = new Plane(new Vec3d(packetByteBuf.readDouble(), 0, packetByteBuf.readDouble()), packetByteBuf.readDouble());
        }));

        PostWorldRenderCallback.EVENT.register(TwoDimensionalShaders.INSTANCE);
        ShaderEffectRenderCallback.EVENT.register(TwoDimensionalShaders.INSTANCE);
    }
}
