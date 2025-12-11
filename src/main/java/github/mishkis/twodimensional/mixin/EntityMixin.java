package github.mishkis.twodimensional.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import github.mishkis.twodimensional.access.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
import github.mishkis.twodimensional.utils.PlanePersistentState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityPlaneGetterSetter {
    @Unique
    private Plane twoDimensional$plane = null;

    @Override
    @Nullable
    public Plane twoDimensional$getPlane() {
        if ((Object)this instanceof ServerPlayer player) {
            return PlanePersistentState.getPlayerPlane(player);
        }

        return twoDimensional$plane;
    }

    @Override
    public void twoDimensional$setPlane(Plane plane) {
        this.twoDimensional$plane = plane;
    }
    @Shadow public abstract void setDeltaMovement(Vec3 velocity);

    @Shadow public abstract Vec3 getDeltaMovement();

    @Shadow private static Vec3 getInputVector(Vec3 movementInput, float speed, float yaw) {return null;}

    @Shadow private Vec3 deltaMovement;

    @Shadow private Vec3 position;

    @Shadow private BlockPos blockPosition;

    @Shadow public abstract Vec3 position();

    @Shadow public abstract float getYRot();

    @Shadow public double xo;

    @Shadow public double yo;

    @Shadow public double zo;

    @Inject(method = "moveRelative", at = @At("HEAD"), cancellable = true)
    public void moveRelative(float speed, Vec3 movementInput, CallbackInfo ci) {
        if (twoDimensional$getPlane() != null) {
            movementInput = new Vec3(movementInput.x + movementInput.z * Mth.sign(this.getYRot() - 180), movementInput.y, 0.);
            this.setDeltaMovement(this.getDeltaMovement().add(getInputVector(movementInput, speed, 0f)));
            ci.cancel();
        }
    }

    @Inject(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    public void clampVelocityToPlane(Vec3 velocity, CallbackInfo ci) {
        Plane plane = twoDimensional$getPlane();
        if (plane != null) {
            this.deltaMovement = plane.intersectPoint(velocity.add(this.position())).subtract(this.position());
            ci.cancel();
        }
    }

    @Inject(method = "setPosRaw", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", shift = At.Shift.AFTER))
    public void clampSetPos(double x, double y, double z, CallbackInfo ci) {
        Plane plane = twoDimensional$getPlane();
        if (plane != null) {
            this.position = plane.intersectPoint(new Vec3(x, y, z));
        }
    }

    @Inject(method = "setPosRaw", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;<init>(III)V", shift = At.Shift.AFTER))
    public void clampBlockPos(double x, double y, double z, CallbackInfo ci) {
        Plane plane = twoDimensional$getPlane();
        if (plane != null) {
            this.blockPosition = BlockPos.containing(plane.intersectPoint(new Vec3(x, y, z)));
        }
    }

    @Inject(method = "absMoveTo(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void clampPrevPos(double x, double y, double z, CallbackInfo ci, @Local (ordinal = 0) double d, @Local (ordinal = 1) double e) {
        Plane plane = twoDimensional$getPlane();
        if (plane != null) {
            Vec3 clampedPos = plane.intersectPoint(new Vec3(d, y, e));
            this.xo = clampedPos.x;
            this.yo = clampedPos.y;
            this.zo = clampedPos.z;
        }
    }
}
