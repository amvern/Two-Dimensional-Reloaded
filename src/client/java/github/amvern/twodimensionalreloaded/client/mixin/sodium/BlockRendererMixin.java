package github.amvern.twodimensionalreloaded.client.mixin.sodium;

import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin {
    /**
     * Culls remaining non-full blocks from Plane cull
     *  */
    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    private void cullBlocks(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (Plane.shouldCull(pos, TwoDimensionalReloadedClient.plane)) {
            ci.cancel();
        }
    }

    @Inject(method = "processQuad", at = @At("HEAD"), cancellable = true)
    private void cullQuads(MutableQuadViewImpl quad, CallbackInfo ci) {
        BlockPos pos = ((AbstractBlockRenderContextAccessor) this).getPos();
        if (Plane.shouldCull(pos, TwoDimensionalReloadedClient.plane)) {
            ci.cancel();
        }
    }
}
