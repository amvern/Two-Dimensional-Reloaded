package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerSpawnFinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerSpawnFinder.class)
public abstract class PlayerSpawnFinderMixin {

    @ModifyArgs(
            method = "getOverworldRespawnPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;"
            )
    )
    private static void clampGetOverworldRespawnPosZ(Args args) {
        args.set(1, 0);
    }

    @ModifyVariable(method = "fixupSpawnHeight", at = @At("HEAD"), argsOnly = true)
    private static BlockPos clampFixupSpawnHeight(BlockPos original) {
        return new BlockPos(original.getX(), original.getY(), 0);
    }
}

