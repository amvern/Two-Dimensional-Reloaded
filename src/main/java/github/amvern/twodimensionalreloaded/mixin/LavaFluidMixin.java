package github.amvern.twodimensionalreloaded.mixin;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void preventEntityDamage(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, CallbackInfo ci) {
        if(Plane.shouldCull(pos)) {
            ci.cancel();
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void preventFireSpread(ServerLevel level, BlockPos pos, FluidState fluidState, RandomSource random, CallbackInfo ci) {
        if(Plane.shouldCull(pos)) {
            ci.cancel();
        }
    }

    @Inject(method = "beforeDestroyingBlock", at = @At("HEAD"), cancellable = true)
    private void stopTheFizzBrother(LevelAccessor level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if(Plane.shouldCull(pos)) {
            ci.cancel();
        }
    }

    @Inject(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), cancellable = true)
    public void preventCulledBlockParticles(Level level, BlockPos pos, FluidState fluidState, RandomSource random, CallbackInfo ci) {
        if(Plane.shouldCull(pos)) {
            ci.cancel();
        }
    }
}