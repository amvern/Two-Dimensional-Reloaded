package github.mishkis.twodimensional.client.mixin;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void cullPlane(BlockState state, BlockGetter world, BlockPos pos, Direction side, BlockPos otherPos, CallbackInfoReturnable<Boolean> cir) {
        Plane plane = TwoDimensionalClient.plane;
        if (plane != null) {
            double dist = plane.sdf(pos.getCenter());
            if (dist <= Plane.CULL_DIST || dist > 32){
                cir.setReturnValue(false);
                return;
            } else if (dist <= 0.5) {
                if (side.getStepY() == 0 && plane.sdf(pos.relative(side).getCenter()) <= Plane.CULL_DIST){
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
//
//        BlockState blockState = world.getBlockState(pos.offset(side));
//        if (!blockState.isFullCube(world, pos.offset(side))) {
//            cir.setReturnValue(true);
//        }
    }
}
