package github.mishkis.twodimensional.client.mixin;

import github.mishkis.twodimensional.access.InteractionLayerGetterSetter;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.LayerMode;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void disableInteractionOutsidePlane(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        Plane plane = TwoDimensionalClient.plane;
        if (plane == null) return;

        double dist = plane.sdf(hitResult.getBlockPos().getCenter());
        boolean isOnPlane = hitResult.getBlockPos().getCenter().z == plane.getZ();

        if (this instanceof InteractionLayerGetterSetter holder) {
            LayerMode mode = holder.getInteractionLayer();

            boolean cancel = switch (mode) {
                case BASE -> !isOnPlane;
                case FACE_AWAY -> Plane.shouldCull(hitResult.getBlockPos(), plane) || dist >= 1.8 || isOnPlane;
            };

            if (cancel) cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    // TODO: fix busted logic (test in survival bc creative mining speed maybe biffs it?)

    //        @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    //        private void cancelStartDestoryBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
    //            LocalPlayer player = Minecraft.getInstance().player;
    //            if(!(player instanceof  EntityPlaneGetterSetter planeHolder)) return;
    //
    //        Plane plane = planeHolder.twoDimensional$getPlane();
    //        if (plane == null) return;
    //
    //        double dist = plane.sdf(blockPos.getCenter());
    //        boolean isOnPlane = blockPos.getCenter().z == plane.getOffset().z;
    //
    //        LayerMode mode = (player instanceof InteractionLayerHolder holder) ? holder.getInteractionLayer() : LayerMode.BASE;
    //
    //            boolean cancel = switch (mode) {
    //                case BASE -> !isOnPlane;
    //                case FACE_AWAY -> Plane.shouldCull(blockPos, plane) || dist >= 1.8 || isOnPlane;
    //                case FACE_CAMERA -> dist >= 1.8 || isOnPlane || Plane.shouldCull(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1), plane);
    //            };
    //
    //            if (cancel) {
    //                cir.setReturnValue(false);
    //                player.resetAttackStrengthTicker();
    //            }
    //    }
    //
    //    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    //    private void cancelDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    //        LocalPlayer player = Minecraft.getInstance().player;
    //        if(!(player instanceof EntityPlaneGetterSetter planeHolder)) return;
    //
    //        Plane plane = planeHolder.twoDimensional$getPlane();
    //        if (plane == null) return;
    //
    //        double dist = plane.sdf(pos.getCenter());
    //        boolean isOnPlane = pos.getCenter().z == plane.getOffset().z;
    //
    //        LayerMode mode = (player instanceof InteractionLayerHolder holder) ? holder.getInteractionLayer() : LayerMode.BASE;
    //
    //        boolean cancel = switch (mode) {
    //            case BASE -> !isOnPlane;
    //            case FACE_AWAY -> Plane.shouldCull(pos, plane) || dist >= 1.8 || isOnPlane;
    //            case FACE_CAMERA -> dist >= 1.8 || isOnPlane || Plane.shouldCull(pos.above(), plane);
    //        };
    //
    //        if (cancel) cir.setReturnValue(false);
    //    }
}
