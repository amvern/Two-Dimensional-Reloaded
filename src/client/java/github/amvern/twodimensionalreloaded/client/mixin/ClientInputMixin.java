package github.amvern.twodimensionalreloaded.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientInput.class)
public class ClientInputMixin {

    @Shadow public Input keyPresses;;

    @ModifyReturnValue(method = "hasForwardImpulse", at = @At("RETURN"))
    private boolean countSidewaysMovementOnPlane(boolean original) {
        if (TwoDimensionalReloadedClient.plane != null) {
            return original || this.keyPresses.left() || this.keyPresses.right();
        }
        return original;
    }
}