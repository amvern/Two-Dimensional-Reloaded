package github.mishkis.twodimensional;

import github.mishkis.twodimensional.utils.Plane;
import github.mishkis.twodimensional.utils.PlanePersistentState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.logging.Logger;

public class TwoDimensional implements ModInitializer {
    public static final String MOD_ID = "two_dimensional";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    public static final Identifier INITIAL_SYNC = new Identifier(MOD_ID, "initial_sync");

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            LOGGER.info("hey");
            Plane plane = PlanePersistentState.getPlayerPlane(serverPlayNetworkHandler.getPlayer());
            PacketByteBuf data = PacketByteBufs.create();
            data.writeDouble(plane.getOffset().x);
            data.writeDouble(plane.getOffset().z);
            data.writeDouble(plane.getYaw());
            minecraftServer.execute(() -> {
                ServerPlayNetworking.send(serverPlayNetworkHandler.getPlayer(), INITIAL_SYNC, data);
            });
        }));
    }
}
