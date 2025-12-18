package github.mishkis.twodimensional.mixin;

import net.minecraft.world.entity.PortalProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PortalProcessor.class)
public class PortalProcessorMixin {

    /**
     * Cancel the decayTick call inside processPortalTeleportation().
     * This prevents portalTime from decreasing when insidePortalThisTick is false.
     */
    @Redirect(
            method = "processPortalTeleportation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/PortalProcessor;decayTick()V"
            )
    )
    private void cancelDecayTick(PortalProcessor instance) {
        // Do nothing. decayTick() is skipped.
    }
}
