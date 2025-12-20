package github.amvern.twodimensionalreloaded.client.mixin;

import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
    private void getShadeBrightness(BlockGetter world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (TwoDimensionalReloadedClient.plane != null && TwoDimensionalReloadedClient.plane.sdf(pos.getCenter()) <= Plane.CULL_DIST){
            cir.setReturnValue(1f);
        }
    }

}