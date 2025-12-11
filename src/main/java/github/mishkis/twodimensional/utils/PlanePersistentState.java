package github.mishkis.twodimensional.utils;

import github.mishkis.twodimensional.TwoDimensional;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class PlanePersistentState extends SavedData {
    private HashMap<UUID, Plane> players = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        CompoundTag playersNbt = new CompoundTag();
        players.forEach(((uuid, plane) -> {
            CompoundTag playerNbt = new CompoundTag();

            playerNbt.putDouble("planeZ", plane.getZ());

            playersNbt.put(uuid.toString(), playerNbt);
        }));
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static PlanePersistentState createFromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        PlanePersistentState state = new PlanePersistentState();
        CompoundTag playersNbt = nbt.getCompound("players");
        playersNbt.getAllKeys().forEach(key -> {
            Plane plane = new Plane();
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

        PlanePersistentState state = persistentStateManager.computeIfAbsent(
                new SavedData.Factory<>(
                        PlanePersistentState::createNew,
                        PlanePersistentState::createFromNbt,
                        DataFixTypes.SAVED_DATA_FORCED_CHUNKS
                ),
                TwoDimensional.MOD_ID
        );

        state.setDirty();

        return state;
    }

    @Nullable
    public static Plane getPlayerPlane(Player player) {
        PlanePersistentState serverState = getServerState(player.level().getServer());

        return serverState.players.get(player.getUUID());
    }

    public static void setPlayerPlane(Player player) {
        PlanePersistentState serverState = getServerState(player.level().getServer());

        serverState.players.put(player.getUUID(), new Plane());
    }
}
