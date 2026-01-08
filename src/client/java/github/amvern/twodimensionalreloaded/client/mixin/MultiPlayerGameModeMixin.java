package github.amvern.twodimensionalreloaded.client.mixin;

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
    private void denyUseItemOnClient(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        double dist = Plane.sdf(hitResult.getBlockPos().getCenter());
        if(Plane.shouldCull(hitResult.getBlockPos()) || dist >= 1.8) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}