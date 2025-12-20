package github.amvern.twodimensionalreloaded.client;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import github.amvern.twodimensionalreloaded.access.EntityPlaneGetterSetter;
import github.amvern.twodimensionalreloaded.network.InteractionLayerPayload;
import github.amvern.twodimensionalreloaded.utils.LayerMode;
import github.amvern.twodimensionalreloaded.utils.Plane;
import github.amvern.twodimensionalreloaded.utils.PlaneAttachment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class TwoDimensionalReloadedClient implements ClientModInitializer {
    public static Plane plane = null;
    private LayerMode lastMode = LayerMode.BASE;

    public static KeyMapping faceAway = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.twodimensionalreloaded.face_away",
            GLFW.GLFW_KEY_B,
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "twodimensionalreloaded"))
    ));

    @Override
    public void onInitializeClient() {
        // Start-of-tick: ensure the player has a plane attached
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                // Attach a plane if it doesn't exist yet
                PlaneAttachment.get(client.player);
                plane = PlaneAttachment.get(client.player);
            }
        });

        // End-of-tick: handle input for layer mode
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null || client.getConnection() == null) return;

            LayerMode mode = faceAway.isDown() ? LayerMode.FACE_AWAY : LayerMode.BASE;

            if (mode != lastMode) {
                lastMode = mode;
                // Send to server
                ClientPlayNetworking.send(new InteractionLayerPayload(mode));
            }
        });
    }
}
