package github.mishkis.twodimensional.client;

import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.access.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class TwoDimensionalClient implements ClientModInitializer {
    public static Plane plane = null;
    public static KeyMapping faceAway = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.twodimensional.face_away",
            GLFW.GLFW_KEY_B,
            "keyGroup.twodimensional"
    ));

    private boolean shouldUpdatePlane = true;

    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(
                TwoDimensional.PlaneSyncPayload.TYPE,
                (payload, ctx) -> {
                    Minecraft.getInstance().execute(() -> {
                        TwoDimensionalClient.plane = new Plane();
                    });
                    shouldUpdatePlane = true;
                    Minecraft.getInstance().mouseHandler.releaseMouse();
                }
        );

        ClientTickEvents.START_CLIENT_TICK.register((client -> {
            if (shouldUpdatePlane && client.player != null) {
                ((EntityPlaneGetterSetter) client.player).twoDimensional$setPlane(plane);
                client.levelRenderer.allChanged();
                shouldUpdatePlane = false;

                Minecraft.getInstance().mouseHandler.grabMouse();;
            }
        }));
    }
}
