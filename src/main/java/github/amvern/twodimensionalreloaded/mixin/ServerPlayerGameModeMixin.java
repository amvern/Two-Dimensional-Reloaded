package github.amvern.twodimensionalreloaded.mixin;

import github.amvern.twodimensionalreloaded.access.InteractionLayerGetterSetter;
import github.amvern.twodimensionalreloaded.utils.LayerMode;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow @Final protected ServerPlayer player;

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void blockMayUseItemAt(ServerPlayer serverPlayer, Level level, ItemStack itemStack, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        double dist = Plane.sdf(blockHitResult.getBlockPos().getCenter());
        boolean isOnPlane = blockHitResult.getBlockPos().getCenter().z == Plane.getZ();

        if(this.player instanceof InteractionLayerGetterSetter holder) {
            LayerMode mode = holder.getInteractionLayer();

            boolean cancel = switch (mode) {
                case BASE -> !isOnPlane;
                case FACE_AWAY -> Plane.shouldCull(blockHitResult.getBlockPos()) || dist >= 1.8;
            };

            if (cancel) cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}