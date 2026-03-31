package github.amvern.twodimensionalreloaded.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import static github.amvern.twodimensionalreloaded.utils.Plane.PLANE_ENTITY_FLAG;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract void setDeltaMovement(Vec3 velocity);
    @Shadow public abstract Vec3 getDeltaMovement();
    @Shadow protected static Vec3 getInputVector(Vec3 movementInput, float speed, float yaw) {return null;}
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
        Entity entity = (Entity)(Object)this;
        if (entity instanceof EnderDragon) return;
        if((entity).hasAttached(PLANE_ENTITY_FLAG)) {
            movementInput = new Vec3(movementInput.x + movementInput.z * Mth.sign(this.getYRot() - 180), movementInput.y, 0.);
            this.setDeltaMovement(this.getDeltaMovement().add(getInputVector(movementInput, speed, 0f)));
            ci.cancel();
        }
    }

    @Inject(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    public void clampVelocityToPlane(Vec3 velocity, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof EnderDragon) return;
        if((entity).hasAttached(PLANE_ENTITY_FLAG)) {
            this.deltaMovement = Plane.intersectPoint(velocity.add(this.position())).subtract(this.position());
            ci.cancel();
        }
    }

    @Inject(method = "setPosRaw", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", shift = At.Shift.AFTER))
    public void clampSetPos(double x, double y, double z, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof EnderDragon) return;
        if((entity).hasAttached(PLANE_ENTITY_FLAG)) {
            this.position = Plane.intersectPoint(new Vec3(x, y, z));
        }
    }

    @Inject(method = "setPosRaw", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;<init>(III)V", shift = At.Shift.AFTER))
    public void clampBlockPos(double x, double y, double z, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof EnderDragon) return;
        if((entity).hasAttached(PLANE_ENTITY_FLAG)) {
            this.blockPosition = BlockPos.containing(Plane.intersectPoint(new Vec3(x, y, z)));
        }
    }

    @Inject(method = "absSnapTo(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void clampPrevPos(double x, double y, double z, CallbackInfo ci, @Local (ordinal = 0) double d, @Local (ordinal = 1) double e) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof EnderDragon) return;
        if(entity.hasAttached(PLANE_ENTITY_FLAG)) {
            Vec3 clampedPos = Plane.intersectPoint(new Vec3(d, y, e));
            this.xo = clampedPos.x;
            this.yo = clampedPos.y;
            this.zo = clampedPos.z;
        }
    }
}