package github.mishkis.twodimensional.client;

import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.utils.Plane;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TwoDimensionalClient implements ClientModInitializer {
    public static Plane plane = null;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TwoDimensional.INITIAL_SYNC, ((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
            plane = new Plane(new Vec3d(packetByteBuf.readDouble(), 0, packetByteBuf.readDouble()), packetByteBuf.readDouble());
        }));
    }
}
