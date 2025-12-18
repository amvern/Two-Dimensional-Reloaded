package github.amvern.twodimensionalreloaded.client.mixin;

import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Inject(method = "add(Lnet/minecraft/client/particle/Particle;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void cullParticle(Particle particle, CallbackInfo ci) {
        Plane plane = TwoDimensionalReloadedClient.plane;
        if (plane == null) return;

        ParticleAccessor accessor = (ParticleAccessor) particle;
        BlockPos pos = new BlockPos((int) accessor.getX(), (int) accessor.getY(), (int) accessor.getZ());

        if (Plane.shouldCull(pos, plane)) {
            ci.cancel();
        }
    }
}




