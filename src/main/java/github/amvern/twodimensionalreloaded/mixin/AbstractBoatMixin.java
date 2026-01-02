package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Clamps boat movement to Plane.
 * Ignores LivingEntity collisions because entities share same Z coordinate.
 * This makes water travel smoother and prevents gameplay breaking interactions
 * */
@Mixin(AbstractBoat.class)
public class AbstractBoatMixin {

    @Inject(method = "controlBoat", at = @At("RETURN"))
    private void clampControlBoatZ(CallbackInfo ci) {
        AbstractBoat boat = (AbstractBoat) (Object) this;

        Vec3 delta = boat.getDeltaMovement();
        boat.setDeltaMovement(delta.x, delta.y, 0.0);
    }

    @Inject(method = "floatBoat", at = @At("RETURN"))
    private void clampFloatBoatZ(CallbackInfo ci) {
        AbstractBoat boat = (AbstractBoat) (Object) this;

        Vec3 delta = boat.getDeltaMovement();
        boat.setDeltaMovement(delta.x, delta.y, 0.0);
    }

    @Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
    private void dontCollideWithLivingEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof LivingEntity) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "push", at = @At("HEAD"), cancellable = true)
    private void cancelLivingEntityPush(Entity entity, CallbackInfo ci) {
        if(entity instanceof Player) return;

        if(entity instanceof LivingEntity) {
            ci.cancel();
        }
    }

}