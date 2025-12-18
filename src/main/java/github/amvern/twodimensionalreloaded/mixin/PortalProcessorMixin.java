package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to mark player as inside portal if they are near it, enables right click portal useWithoutItem
 */
@Mixin(PortalProcessor.class)
public abstract class PortalProcessorMixin {

    @Shadow public abstract void setAsInsidePortalThisTick(boolean bl);

    @Shadow public abstract BlockPos getEntryPosition();

    @Inject(method = "processPortalTeleportation", at = @At("HEAD"))
    private void treatAsNearPortal(ServerLevel serverLevel, Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        BlockPos portalPos = this.getEntryPosition();

        if (entity.blockPosition().closerThan(portalPos, 5.0)) {
            this.setAsInsidePortalThisTick(true);
        }
    }
}
