package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.server.level.PlayerSpawnFinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerSpawnFinder.class)
public abstract class PlayerSpawnFinderMixin {

    @ModifyVariable(method = "scheduleNext", at = @At("STORE"), ordinal = 1)
    private int clampSpawnZ(int z) {
        return 0;
    }
}

