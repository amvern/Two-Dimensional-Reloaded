package github.amvern.twodimensionalreloaded;

import github.amvern.twodimensionalreloaded.access.EntityPlaneGetterSetter;
import github.amvern.twodimensionalreloaded.access.InteractionLayerGetterSetter;
import github.amvern.twodimensionalreloaded.network.InteractionLayerPayload;
import github.amvern.twodimensionalreloaded.utils.Plane;
import github.amvern.twodimensionalreloaded.utils.PlanePersistentState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import java.util.logging.Logger;


public class TwoDimensionalReloaded implements ModInitializer {
    public static final String MOD_ID = "two_dimensional_reloaded";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    public record PlaneSyncPayload(double z) implements CustomPacketPayload {
        public static final Type<PlaneSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "plane_sync"));

        public static final StreamCodec<RegistryFriendlyByteBuf, PlaneSyncPayload> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.DOUBLE, PlaneSyncPayload::z,
                        PlaneSyncPayload::new
                );

        @Override
        public Type<? extends  CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void setPlayerPlane(MinecraftServer server, ServerPlayer player) {
        double x = player.getBlockX() + 0.5;
        double z = player.getBlockZ() + 0.5;

        final Plane plane = new Plane();
        PlanePersistentState.setPlayerPlane(player);

        server.execute(() -> {
            ServerPlayNetworking.send(player, new PlaneSyncPayload(z));

            ((EntityPlaneGetterSetter) player).twoDimensional$setPlane(plane);
            player.setPosRaw(x, player.position().y, z);
        });
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(
                PlaneSyncPayload.TYPE,
                PlaneSyncPayload.CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                InteractionLayerPayload.TYPE,
                InteractionLayerPayload.CODEC
        );

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            setPlayerPlane(server, handler.getPlayer());
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(
                (ServerPlayer player, ServerLevel origin, ServerLevel destination) -> {
                    TwoDimensionalReloaded.setPlayerPlane(player.getServer(), player);
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
                        TwoDimensionalReloaded.LOGGER.info(err.getMessage());
                    }
                }
        );
    }
}
