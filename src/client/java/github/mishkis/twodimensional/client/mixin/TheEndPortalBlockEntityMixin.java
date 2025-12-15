package github.mishkis.twodimensional.client.mixin;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO: constain the newly rendered side to be < a full block to match top of end portal block
/**
 * This mixin forces the end portal blocks to render the normally hidden face where the fountain edge gets culled
 */
@Mixin(TheEndPortalBlockEntity.class)
public class TheEndPortalBlockEntityMixin {

    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private void renderExposedFace(Direction direction, CallbackInfoReturnable<Boolean> cir) {
        BlockEntity be = (BlockEntity) (Object) this;
        BlockPos adjacentPos = be.getBlockPos().relative(direction);

        if (Plane.shouldCull(adjacentPos, TwoDimensionalClient.plane)) {
            cir.setReturnValue(true);
        }
    }
}