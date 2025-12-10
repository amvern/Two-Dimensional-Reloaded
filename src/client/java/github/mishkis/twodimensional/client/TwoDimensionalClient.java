package github.mishkis.twodimensional.client;

import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.client.rendering.TwoDimensionalCrosshairRenderer;
import github.mishkis.twodimensional.access.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
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
        ClientPlayNetworking.registerGlobalReceiver(TwoDimensional.PlaneSyncPayload.TYPE, (payload, ctx) -> {
            plane = new Plane(new Vec3(payload.x(), 0, payload.z()), payload.radYaw());
            shouldUpdatePlane = true;

            Minecraft.getInstance().mouseHandler.releaseMouse();
        });

        ClientPlayNetworking.registerGlobalReceiver(TwoDimensional.PlaneRemovePayload.TYPE, ((payload, ctx) -> {
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

        TwoDimensionalCrosshairRenderer.intialize();
    }
}
