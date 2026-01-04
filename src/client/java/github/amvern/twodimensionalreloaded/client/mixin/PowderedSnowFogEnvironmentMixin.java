package github.amvern.twodimensionalreloaded.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.PowderedSnowFogEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PowderedSnowFogEnvironment.class)
public class PowderedSnowFogEnvironmentMixin {

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    public void setupFog(FogData fogData, Camera camera, ClientLevel clientLevel, float f, DeltaTracker deltaTracker, CallbackInfo ci) {
        fogData.environmentalStart = -8.0F;
        fogData.environmentalEnd = f * 0.5F;
        fogData.skyEnd = fogData.environmentalEnd;
        fogData.cloudEnd = fogData.environmentalEnd;

        ci.cancel();
    }

}