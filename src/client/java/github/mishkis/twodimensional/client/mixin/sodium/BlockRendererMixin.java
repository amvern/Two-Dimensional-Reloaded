package github.mishkis.twodimensional.client.mixin.sodium;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
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
    private void cullBlocks(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (Plane.shouldCull(pos, TwoDimensionalClient.plane)) {
            ci.cancel();
        }
    }
}
