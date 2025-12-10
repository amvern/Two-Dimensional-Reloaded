package github.mishkis.twodimensional;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import github.mishkis.twodimensional.access.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
import github.mishkis.twodimensional.utils.PlanePersistentState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import java.util.logging.Logger;

public class TwoDimensional implements ModInitializer {
    public static final String MOD_ID = "two_dimensional";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    public static final ResourceLocation PLANE_SYNC = new ResourceLocation(MOD_ID, "plane_sync");
    public static final ResourceLocation PLANE_REMOVE = new ResourceLocation(MOD_ID, "plane_remove");

    public static Component updatePlane(MinecraftServer minecraftServer, ServerPlayer player, double x, double z, double yaw) {
        final double radYaw = yaw * Mth.DEG_TO_RAD;

        PlanePersistentState.setPlayerPlane(player, x, z, radYaw);
        FriendlyByteBuf data = PacketByteBufs.create();
        data.writeDouble(x);
        data.writeDouble(z);
        data.writeDouble(radYaw);
        minecraftServer.execute(() -> {
            ServerPlayNetworking.send(player, PLANE_SYNC, data);

            Plane newPlane = new Plane(new Vec3(x, 0., z), radYaw);
            Plane oldPlane = ((EntityPlaneGetterSetter) player).twoDimensional$getPlane();
            if (oldPlane != null) {
                newPlane.containedEntities = oldPlane.containedEntities;
                newPlane.containedEntities.forEach(entity -> {
                    ((EntityPlaneGetterSetter) entity).twoDimensional$setPlane(newPlane);
                });
            }

            ((EntityPlaneGetterSetter) player).twoDimensional$setPlane(newPlane);
            player.setPosRaw(x, player.position().y, z);
        });

        return Component.literal("Active plane of %s set to an offset of [%f, %f], and a yaw of %f.".formatted(player.getName().getString(), x, z, yaw));
    }

    private Component removePlane(MinecraftServer minecraftServer, ServerPlayer player) {
        PlanePersistentState.removePlayerPlane(player);
        minecraftServer.execute(() -> {
            ServerPlayNetworking.send(player, PLANE_REMOVE, PacketByteBufs.empty());

            Plane oldPlane = ((EntityPlaneGetterSetter) player).twoDimensional$getPlane();
            if (oldPlane != null) {
                oldPlane.containedEntities.forEach(entity -> {
                    ((EntityPlaneGetterSetter) entity).twoDimensional$setPlane(null);
                });
            }

            PlanePersistentState.removePlayerPlane(player);
            ((EntityPlaneGetterSetter) player).twoDimensional$setPlane(null);
        });
        return Component.literal("Active plane set to none.");
    }

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            Plane plane = PlanePersistentState.getPlayerPlane(serverPlayNetworkHandler.getPlayer());
            if (plane != null) {
                updatePlane(minecraftServer, serverPlayNetworkHandler.getPlayer(), plane.getOffset().x, plane.getOffset().z, plane.getYaw());
            }
        }));

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            if (registrationEnvironment.includeIntegrated) {
                commandDispatcher.register(Commands.literal("twoDimensional").
                        then(Commands.literal("default").executes(commandContext -> {
                            ServerPlayer player = commandContext.getSource().getPlayer();
                            if (player != null) {
                                commandContext.getSource().sendSuccess(() -> updatePlane(commandContext.getSource().getServer(), player,
                                        player.getBlockX() + 0.5, player.getBlockZ() + 0.5, 0.
                                ), false);
                                return 1;
                            }

                            commandContext.getSource().sendFailure(Component.literal("This command can only be called from a player!"));
                            return 0;
                        }))
                        .then(Commands.literal("from_yaw").then(Commands.argument("yaw", DoubleArgumentType.doubleArg()).executes(commandContext -> {
                            ServerPlayer player = commandContext.getSource().getPlayer();
                            if (player != null) {
                                commandContext.getSource().sendSuccess(() -> updatePlane(commandContext.getSource().getServer(), player,
                                        player.getBlockX() + 0.5, player.getBlockZ() + 0.5, DoubleArgumentType.getDouble(commandContext, "yaw")
                                ), false);
                                return 1;
                            }

                            commandContext.getSource().sendFailure(Component.literal("This command can only be called from a player!"));
                            return 0;
                        })))
                        .then(Commands.literal("custom").requires(serverCommandSource -> serverCommandSource.hasPermission(1)).then(
                                Commands.argument("offset_x", DoubleArgumentType.doubleArg()).then(
                                        Commands.argument("offset_z", DoubleArgumentType.doubleArg()).then(
                                                Commands.argument("yaw", DoubleArgumentType.doubleArg()).executes(commandContext -> {
                                                    ServerPlayer player = commandContext.getSource().getPlayer();
                                                    if (player != null) {
                                                        commandContext.getSource().sendSuccess(() -> updatePlane(commandContext.getSource().getServer(), player,
                                                                DoubleArgumentType.getDouble(commandContext, "offset_x"), DoubleArgumentType.getDouble(commandContext, "offset_z"), DoubleArgumentType.getDouble(commandContext, "yaw")
                                                        ), true);
                                                        return 1;
                                                    }

                                                    commandContext.getSource().sendFailure(Component.literal("This command can only be called from a player!"));
                                                    return 0;
                                                })))))
                        .then(Commands.literal("disable").executes(commandContext -> {
                            ServerPlayer player = commandContext.getSource().getPlayer();
                            if (player != null) {
                                commandContext.getSource().sendSuccess(() -> removePlane(commandContext.getSource().getServer(), commandContext.getSource().getPlayer()), false);
                                return 1;
                            }

                            commandContext.getSource().sendFailure(Component.literal("This command can only be called from a player!"));
                            return 0;
                        }))
                );
            }
        }));
    }

}
