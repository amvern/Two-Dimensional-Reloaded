package github.amvern.twodimensionalreloaded.client.mixin;

import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sets opacity for culled blocks, allowing light propagation at plane
 * */
@Mixin(LightEngine.class)
public abstract class LightEngineMixin {

    @Unique
    private static final ThreadLocal<BlockPos> twoDimensionalReloaded$LIGHT_POS =  new ThreadLocal<>();

    @Inject(method = "getState", at = @At("HEAD"))
    private void twoDimensionalReloaded$capturePos(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        twoDimensionalReloaded$LIGHT_POS.set(pos);
    }

    @Inject(method = "getOpacity", at = @At("RETURN"))
    private void twoDimensionalReloaded$clearCapturedPos(CallbackInfoReturnable<Integer> cir) {
        twoDimensionalReloaded$LIGHT_POS.remove();
    }

    @Unique
    private static BlockPos twoDimensionalReloaded$getLightPos() {
        return twoDimensionalReloaded$LIGHT_POS.get();
    }

    @Inject(method = "getOpacity", at = @At("HEAD"), cancellable = true)
    private void setCulledOpacity(BlockState state, CallbackInfoReturnable<Integer> cir) {
        Plane plane = TwoDimensionalReloadedClient.plane;
        if (plane == null) return;

        BlockPos pos = twoDimensionalReloaded$getLightPos();
        if (pos == null) return;

        int blockLight = state.canOcclude() ? 1 : 0;

        if (Plane.shouldCull(pos, plane)) {
            cir.setReturnValue(Math.max(1, blockLight));
        }
    }
}