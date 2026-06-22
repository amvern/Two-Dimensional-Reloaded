package github.amvern.twodimensionalreloaded;

import github.amvern.twodimensionalreloaded.access.InteractionLayerGetterSetter;
import github.amvern.twodimensionalreloaded.network.EndermanLookPayload;
import github.amvern.twodimensionalreloaded.network.InteractionLayerPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.logging.Logger;

import static github.amvern.twodimensionalreloaded.utils.Plane.PLANE_ENTITY_FLAG;

public class TwoDimensionalReloaded implements ModInitializer {
    public static final String MOD_ID = "twodimensionalreloaded";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    public static void setPlayerPlane(MinecraftServer server, ServerPlayer player) {
        BlockPos originalPos = player.blockPosition();
        int adjustedY = player.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, originalPos.getX(), 0);

        server.execute(() -> {
            if(!player.hasAttached(PLANE_ENTITY_FLAG)) {
                player.setAttached(PLANE_ENTITY_FLAG, true);
                player.setPosRaw(originalPos.getX() + 0.5, adjustedY, 0.5);
            }
        });
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(
                InteractionLayerPayload.TYPE,
                InteractionLayerPayload.CODEC
        );

        PayloadTypeRegistry.serverboundPlay().register(
                EndermanLookPayload.TYPE,
                EndermanLookPayload.CODEC
        );

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            setPlayerPlane(server, handler.getPlayer());
        });

        ServerPlayNetworking.registerGlobalReceiver(
                InteractionLayerPayload.TYPE,
                (payload, ctx) -> {
                    try {
                        ctx.server().execute(() -> {
                            ((InteractionLayerGetterSetter) ctx.player()).setInteractionLayer(payload.mode());
                        });
                    } catch (Exception err) {
                        LOGGER.info(err.getMessage());
                    }
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                EndermanLookPayload.TYPE,
                (payload, ctx) -> {
                    ctx.server().execute(() -> {

                        if (!payload.looking()) return;

                        Entity entity = ctx.player().level().getEntity(payload.entityId());
                        if (!(entity instanceof EnderMan enderman)) return;

                        // 🔒 basic validation
                        if (ctx.player().distanceTo(enderman) > 32) return;

                        // 👉 do whatever you want here
                        enderman.setTarget(ctx.player());
                        TwoDimensionalReloaded.LOGGER.info(enderman.toString());
                    });
                }
        );
    }
}