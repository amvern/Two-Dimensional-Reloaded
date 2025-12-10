package github.mishkis.twodimensional.client;

import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.client.rendering.TwoDimensionalCrosshairRenderer;
import github.mishkis.twodimensional.client.rendering.TwoDimensionalShaders;
import github.mishkis.twodimensional.access.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class TwoDimensionalClient implements ClientModInitializer {
    public static Plane plane = null;
    public static KeyMapping turnedAround = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.twodimensional.turn_around",
            GLFW.GLFW_KEY_B,
            "keyGroup.twodimensional"
    ));

    private boolean shouldUpdatePlane = true;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TwoDimensional.PLANE_SYNC, ((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
            plane = new Plane(new Vec3(packetByteBuf.readDouble(), 0, packetByteBuf.readDouble()), packetByteBuf.readDouble());
            shouldUpdatePlane = true;

            Minecraft.getInstance().mouseHandler.releaseMouse();
        }));
        ClientPlayNetworking.registerGlobalReceiver(TwoDimensional.PLANE_REMOVE, ((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
            plane =  null;
            shouldUpdatePlane = true;

            Minecraft.getInstance().mouseHandler.releaseMouse();
        }));

        ClientTickEvents.START_CLIENT_TICK.register((client -> {
            if (shouldUpdatePlane && client.player != null) {
                ((EntityPlaneGetterSetter) client.player).twoDimensional$setPlane(plane);
                client.levelRenderer.allChanged();
                shouldUpdatePlane = false;

                Minecraft.getInstance().mouseHandler.grabMouse();
            }
        }));

        PostWorldRenderCallback.EVENT.register(TwoDimensionalShaders.INSTANCE);
        ShaderEffectRenderCallback.EVENT.register(TwoDimensionalShaders.INSTANCE);
        TwoDimensionalCrosshairRenderer.intialize();
    }
}
