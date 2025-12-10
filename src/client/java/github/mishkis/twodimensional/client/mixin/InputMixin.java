package github.mishkis.twodimensional.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import net.minecraft.client.player.Input;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Input.class)
public class InputMixin {
    @Shadow public float leftImpulse;

    @ModifyReturnValue(method = "hasForwardImpulse", at = @At("RETURN"))
    private boolean countSidewaysMovementOnPlane(boolean original) {
        if (TwoDimensionalClient.plane != null) {
            return original || Mth.abs(this.leftImpulse) > 1.0E-5F;
        }
        return original;
    }
}
