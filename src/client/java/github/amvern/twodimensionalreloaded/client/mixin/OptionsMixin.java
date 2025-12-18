package github.amvern.twodimensionalreloaded.client.mixin;

import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import net.minecraft.client.CameraType;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public class OptionsMixin {
    @Inject(method = "getCameraType", at = @At("HEAD"), cancellable = true)
    public void getCameraType(CallbackInfoReturnable<CameraType> cir) {
        if (TwoDimensionalReloadedClient.plane != null) {
            cir.setReturnValue(CameraType.THIRD_PERSON_BACK);
        }
    }

    /**
     * Force view bobbing off, it behaves oddly with the offset camera POV imo
     * */
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void cancelBob(CallbackInfoReturnable<OptionInstance<Boolean>> cir) {
        if (TwoDimensionalReloadedClient.plane != null) {
            cir.setReturnValue(OptionInstance.createBoolean("options.viewBobbing", false));
        }
    }
}
