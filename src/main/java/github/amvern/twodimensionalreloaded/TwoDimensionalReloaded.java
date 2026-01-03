package github.amvern.twodimensionalreloaded;

import github.amvern.twodimensionalreloaded.access.EntityPlaneGetterSetter;
import github.amvern.twodimensionalreloaded.access.InteractionLayerGetterSetter;
import github.amvern.twodimensionalreloaded.network.InteractionLayerPayload;
import github.amvern.twodimensionalreloaded.utils.Plane;
import github.amvern.twodimensionalreloaded.utils.PlaneAttachment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import java.util.logging.Logger;

public class TwoDimensionalReloaded implements ModInitializer {
    public static final String MOD_ID = "twodimensionalreloaded";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    public static void setPlayerPlane(MinecraftServer server, ServerPlayer player) {
        double x = player.getBlockX() + 0.5;
        double z = player.getBlockZ() + 0.5;

        final Plane plane = new Plane();

        server.execute(() -> {
            PlaneAttachment.set(player, plane);

            ((EntityPlaneGetterSetter) player).twoDimensional$setPlane(plane);

            player.setPosRaw(x, player.position().y, z);
        });
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(
                InteractionLayerPayload.TYPE,
                InteractionLayerPayload.CODEC
        );

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            setPlayerPlane(server, handler.getPlayer());
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(
                (ServerPlayer player, ServerLevel origin, ServerLevel destination) -> {
                    setPlayerPlane(origin.getServer(), player);
                }
        );

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
    }
}