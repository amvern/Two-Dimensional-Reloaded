package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Inject(
        method = "isFaceSturdy(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventNorthSouthAttach(BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (direction == Direction.SOUTH) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "isFaceSturdy(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/SupportType;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventNorthSouthAttach2(BlockGetter level, BlockPos pos, Direction direction, SupportType supportType, CallbackInfoReturnable<Boolean> cir) {
        if (supportType == SupportType.CENTER) {
            if (direction == Direction.SOUTH) {
                cir.setReturnValue(false);
            }
        }
    }
}