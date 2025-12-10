package github.mishkis.twodimensional.client.mixin;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public class OptionsMixin {
    @Inject(method = "getCameraType", at = @At("HEAD"), cancellable = true)
    public void getCameraType(CallbackInfoReturnable<CameraType> cir) {
        if (TwoDimensionalClient.plane != null) {
            cir.setReturnValue(CameraType.THIRD_PERSON_BACK);
        }
    }
}
