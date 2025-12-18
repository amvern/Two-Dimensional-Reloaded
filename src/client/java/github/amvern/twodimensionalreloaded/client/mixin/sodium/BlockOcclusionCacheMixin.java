package github.amvern.twodimensionalreloaded.client.mixin.sodium;

import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockOcclusionCache.class)
public class BlockOcclusionCacheMixin {

    /**
     * Culls full blocks at the Plane
     * */
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private void cullPlane(BlockState selfState, BlockGetter view, BlockPos selfPos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        Plane plane = TwoDimensionalReloadedClient.plane;
        if (plane != null) {
            double dist = plane.sdf(selfPos.getCenter());

            if (dist <= Plane.CULL_DIST) {
                cir.setReturnValue(false);
            } else if (dist <= 0.5) {
                if (facing.getStepY() == 0 && plane.sdf(selfPos.relative(facing).getCenter()) <= Plane.CULL_DIST) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
