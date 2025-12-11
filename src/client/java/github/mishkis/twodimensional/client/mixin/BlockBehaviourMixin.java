package github.mishkis.twodimensional.client.mixin;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviourMixin {
    @Shadow public abstract boolean canOcclude();

    @Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
    private void getShadeBrightness(BlockGetter world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (TwoDimensionalClient.plane != null && TwoDimensionalClient.plane.sdf(pos.getCenter()) <= Plane.CULL_DIST){
            cir.setReturnValue(1f);
        }
    }

    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void getOpacity(BlockGetter world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        Plane plane = TwoDimensionalClient.plane;
        if (Plane.shouldCull(pos, plane)) {
            if (this.canOcclude()) {
                cir.setReturnValue(1);
            } else {
                cir.setReturnValue(0);
            }
        }
    }
}
