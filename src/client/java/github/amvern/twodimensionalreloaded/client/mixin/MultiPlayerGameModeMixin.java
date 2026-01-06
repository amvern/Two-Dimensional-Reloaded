package github.amvern.twodimensionalreloaded.client.mixin;

import github.amvern.twodimensionalreloaded.access.InteractionLayerGetterSetter;
import github.amvern.twodimensionalreloaded.utils.LayerMode;
import github.amvern.twodimensionalreloaded.utils.Plane;
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
        double dist = Plane.sdf(hitResult.getBlockPos().getCenter());
        boolean isOnPlane = hitResult.getBlockPos().getCenter().z == Plane.getZ();

        if (this instanceof InteractionLayerGetterSetter holder) {
            LayerMode mode = holder.getInteractionLayer();

            boolean cancel = switch (mode) {
                case BASE -> !isOnPlane;
                case FACE_AWAY -> Plane.shouldCull(hitResult.getBlockPos()) || dist >= 1.8 || isOnPlane;
            };

            if (cancel) cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}