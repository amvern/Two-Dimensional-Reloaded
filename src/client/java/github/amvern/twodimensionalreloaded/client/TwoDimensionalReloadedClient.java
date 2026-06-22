package github.amvern.twodimensionalreloaded.client;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import github.amvern.twodimensionalreloaded.client.config.ClientConfig;
import github.amvern.twodimensionalreloaded.network.EndermanLookPayload;
import github.amvern.twodimensionalreloaded.network.InteractionLayerPayload;
import github.amvern.twodimensionalreloaded.util.BlockPlacementGuide;
import github.amvern.twodimensionalreloaded.utils.LayerMode;

import static github.amvern.twodimensionalreloaded.util.RandomHelpers.isLookingAtEndermanHead;
import static github.amvern.twodimensionalreloaded.utils.Plane.PLANE_ENTITY_FLAG;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class TwoDimensionalReloadedClient implements ClientModInitializer {
    private LayerMode lastMode = LayerMode.BASE;
    public static boolean LOOKING_AT_ENDERMAN = false;
    public static @Nullable EnderMan CURRENT_ENDERMAN = null;
    private static int lastSentId = -1;
    private static boolean lastSentLooking = false;

    public static final KeyMapping.Category UTILITY_CATEGORY =
        new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "utility")
        );

    public static KeyMapping faceAway = KeyMappingHelper.registerKeyMapping(new KeyMapping(
    "key.twodimensionalreloaded.face_away",
        GLFW.GLFW_KEY_B,
        UTILITY_CATEGORY
    ));

    public static KeyMapping enablePlacementGuide = KeyMappingHelper.registerKeyMapping(new KeyMapping(
    "key.twodimensionalreloaded.enable_placement_guide",
        GLFW.GLFW_KEY_Y,
        UTILITY_CATEGORY
    ));

//    public static KeyMapping blockRotationKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
//            "key.twodimensionalreloaded.block_rotation_key",
//            GLFW.GLFW_KEY_Y,
//            UTILITY_CATEGORY
//    ));

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

            if(enablePlacementGuide.consumeClick()) {
                TwoDimensionalReloadedClient.CONFIG.renderBlockPlacementGuide = !TwoDimensionalReloadedClient.CONFIG.renderBlockPlacementGuide;
            }

            if (mode != lastMode) {
                lastMode = mode;
                ClientPlayNetworking.send(new InteractionLayerPayload(mode));
            }

            HitResult hit = client.player.raycastHitResult(0.0f, Minecraft.getInstance().getCameraEntity());

            int newId = -1;
            boolean newLooking = false;

            if (hit.getType() == HitResult.Type.ENTITY) {
                Entity e = ((EntityHitResult) hit).getEntity();
                if (e instanceof EnderMan enderman) {
                    CURRENT_ENDERMAN = enderman;
                    LOOKING_AT_ENDERMAN = isLookingAtEndermanHead(client.player, enderman);

                    newId = enderman.getId();
                    newLooking = LOOKING_AT_ENDERMAN;
                } else {
                    CURRENT_ENDERMAN = null;
                    LOOKING_AT_ENDERMAN = false;
                }
            } else {
                CURRENT_ENDERMAN = null;
                LOOKING_AT_ENDERMAN = false;
            }

            if (newId != lastSentId || newLooking != lastSentLooking) {
                lastSentId = newId;
                lastSentLooking = newLooking;

                ClientPlayNetworking.send(
                        new EndermanLookPayload(newId, newLooking)
                );
            }

        });

        LevelRenderEvents.END_MAIN.register(context -> {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player != null && TwoDimensionalReloadedClient.CONFIG.renderBlockPlacementGuide && player.getMainHandItem().getItem() instanceof BlockItem blockItem) {
                BlockPlacementGuide.renderPlacementGuide(context.poseStack());
            }
        });
    }
}