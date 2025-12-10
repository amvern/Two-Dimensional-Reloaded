package github.mishkis.twodimensional.utils;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Codec;
import github.mishkis.twodimensional.TwoDimensional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class PlanePersistentState extends SavedData {
    private HashMap<UUID, Plane> players = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag nbt) {
        CompoundTag playersNbt = new CompoundTag();
        players.forEach(((uuid, plane) -> {
            CompoundTag playerNbt = new CompoundTag();

            // Really we don't need the y component of the offset for anything whatsoever
            playerNbt.putDouble("offset.x", plane.getOffset().x);
            playerNbt.putDouble("offset.z", plane.getOffset().z);

            playerNbt.putDouble("yaw", plane.getYaw());

            playersNbt.put(uuid.toString(), playerNbt);
        }));
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static PlanePersistentState createFromNbt(CompoundTag nbt) {
        PlanePersistentState state = new PlanePersistentState();

        CompoundTag playersNbt = nbt.getCompound("players");
        playersNbt.getAllKeys().forEach(key -> {
            Plane plane = new Plane(
                    new Vec3(playersNbt.getCompound(key).getDouble("offset.x"), 0, playersNbt.getCompound(key).getDouble("offset.z")),
                    playersNbt.getCompound(key).getDouble("yaw")
            );

            state.players.put(UUID.fromString(key), plane);
        });

        return state;
    }

    public static PlanePersistentState createNew() {
        PlanePersistentState state = new PlanePersistentState();
        state.players = new HashMap<>();
        return state;
    }

    public static PlanePersistentState getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.getLevel(Level.OVERWORLD).getDataStorage();

        PlanePersistentState state = persistentStateManager.computeIfAbsent(PlanePersistentState::createFromNbt, PlanePersistentState::createNew, TwoDimensional.MOD_ID);

        state.setDirty();

        return state;
    }

    @Nullable
    public static Plane getPlayerPlane(Player player) {
        PlanePersistentState serverState = getServerState(player.level().getServer());

        return serverState.players.get(player.getUUID());
    }

    public static void setPlayerPlane(Player player, double x, double z, double yaw) {
        PlanePersistentState serverState = getServerState(player.level().getServer());

        serverState.players.put(player.getUUID(), new Plane(new Vec3(x, 0, z), yaw));
    }

    public static void removePlayerPlane(Player player) {
        PlanePersistentState serverState = getServerState(player.level().getServer());

        serverState.players.remove(player.getUUID());
    }
}
