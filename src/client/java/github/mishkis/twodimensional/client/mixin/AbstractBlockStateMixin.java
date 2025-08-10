package github.mishkis.twodimensional.client.mixin;

import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true)
    private void getAmbientOcclusionLightLevel(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (TwoDimensionalClient.plane != null && TwoDimensionalClient.plane.sdf(pos.toCenterPos()) <= Plane.CULL_DIST){
            cir.setReturnValue(1f);
        }
    }

    // it has to always be false, which means that lighting will be broken when not in 2d
    // seems minecraft probably caches these values at some point during joining the world
    // possibly fixable by clearing that cache, but I don't have time rn
    @Inject(method = "isOpaque", at = @At("HEAD"), cancellable = true)
    private void isOpaque(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
