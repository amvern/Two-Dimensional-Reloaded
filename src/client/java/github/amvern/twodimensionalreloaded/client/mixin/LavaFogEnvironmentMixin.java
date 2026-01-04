package github.amvern.twodimensionalreloaded.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFogEnvironment.class)
public class LavaFogEnvironmentMixin {

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    public void setupFog(FogData fogData, Camera camera, ClientLevel clientLevel, float f, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (camera.entity() instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            fogData.environmentalStart = -4.0F;
            fogData.environmentalEnd = 14;
        } else {
            fogData.environmentalStart = -8.0F;
            fogData.environmentalEnd = 9F;
        }

        fogData.skyEnd = fogData.environmentalEnd;
        fogData.cloudEnd = fogData.environmentalEnd;

        ci.cancel();
    }
}
