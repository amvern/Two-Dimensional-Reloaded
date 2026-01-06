package github.amvern.twodimensionalreloaded.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
        return original || this.keyPresses.left() || this.keyPresses.right();
    }
}