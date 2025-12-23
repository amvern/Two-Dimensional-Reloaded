package github.amvern.twodimensionalreloaded.client.mixin;

import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

    @Redirect(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/lighting/LightEngine;hasDifferentLightProperties(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"
            )
    )
    private boolean twoDimensionalReloaded$hasDifferentLightPropertiesWithPos(
            BlockState oldState,
            BlockState newState,
            BlockPos pos,
            BlockState state,
            int flags
    ) {
        Plane plane = TwoDimensionalReloadedClient.plane;
        if (Plane.shouldCull(pos, plane)) {
            return false;
        }

        return LightEngine.hasDifferentLightProperties(oldState, newState);
    }
}

