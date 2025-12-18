package github.amvern.twodimensionalreloaded.client;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import github.amvern.twodimensionalreloaded.access.EntityPlaneGetterSetter;
import github.amvern.twodimensionalreloaded.network.InteractionLayerPayload;
import github.amvern.twodimensionalreloaded.utils.LayerMode;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class TwoDimensionalReloadedClient implements ClientModInitializer {
    public static Plane plane = null;
    private LayerMode lastMode = LayerMode.BASE;
    public static KeyMapping faceAway = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.twodimensionalreloaded.face_away",
            GLFW.GLFW_KEY_B,
            "keyGroup.twodimensionalreloaded"
    ));

    private boolean shouldUpdatePlane = true;

    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(
                TwoDimensionalReloaded.PlaneSyncPayload.TYPE,
                (payload, ctx) -> {
                    Minecraft.getInstance().execute(() -> {
                        TwoDimensionalReloadedClient.plane = new Plane();
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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null || client.getConnection() == null) return;

            LayerMode mode = TwoDimensionalReloadedClient.faceAway.isDown() ? LayerMode.FACE_AWAY
                    : LayerMode.BASE;

            if (mode != lastMode) {
                lastMode = mode;
                ClientPlayNetworking.send(new InteractionLayerPayload(mode));
            }
        });
    }
}
