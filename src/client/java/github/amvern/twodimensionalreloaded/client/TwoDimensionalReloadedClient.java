package github.amvern.twodimensionalreloaded.client;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import github.amvern.twodimensionalreloaded.client.config.ClientConfig;
import github.amvern.twodimensionalreloaded.network.InteractionLayerPayload;
import github.amvern.twodimensionalreloaded.utils.LayerMode;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import static github.amvern.twodimensionalreloaded.utils.Plane.PLANE_ENTITY_FLAG;

public class TwoDimensionalReloadedClient implements ClientModInitializer {
    private LayerMode lastMode = LayerMode.BASE;

    public static KeyMapping faceAway = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.twodimensionalreloaded.face_away",
            GLFW.GLFW_KEY_B,
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "utility"))
    ));

    public static ClientConfig CONFIG;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ClientConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();


        ClientPlayConnectionEvents.JOIN.register((handler, sender, client)-> {
            Minecraft.getInstance().player.setAttached(PLANE_ENTITY_FLAG, true);
            client.levelRenderer.allChanged();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null || client.getConnection() == null) return;

            LayerMode mode = faceAway.isDown() ? LayerMode.FACE_AWAY : LayerMode.BASE;

            if (mode != lastMode) {
                lastMode = mode;
                ClientPlayNetworking.send(new InteractionLayerPayload(mode));
            }
        });
    }
}