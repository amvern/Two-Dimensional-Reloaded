package github.mishkis.twodimensional.client.mixin;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.duck_interface.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.duck_interface.MouseNormalizedGetter;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityPlaneGetterSetter {
    @Shadow public abstract Vec3d getVelocity();

    @Shadow private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) { return null; };

    @Shadow public abstract World getWorld();

    @Shadow private @Nullable Entity vehicle;
    @Shadow public float prevPitch;

    @Shadow public abstract float getPitch();

    @Shadow public float prevYaw;

    @Shadow public abstract float getYaw();

    @Shadow public abstract void setPitch(float pitch);

    @Shadow public abstract void setYaw(float yaw);

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract void setBodyYaw(float bodyYaw);

    @Shadow public abstract Vec3d getCameraPosVec(float tickDelta);

    @Shadow public abstract Vec3d getEyePos();

    @Shadow public abstract boolean canHit();

    @Unique
    private Plane plane = null;

    @Override
    @Nullable
    public Plane twoDimensional$getPlane() {
        return plane;
    }

    @Override
    public void twoDimensional$setPlane(Plane plane) {
        this.plane = plane;
    }

    @Inject(method = "updateVelocity", at = @At("HEAD"), cancellable = true)
    public void updateVelocity(float speed, Vec3d movementInput, CallbackInfo ci) {
        if (twoDimensional$getPlane() != null) {
            this.setVelocity(this.getVelocity().add(movementInputToVelocity(movementInput, speed, (float) (twoDimensional$getPlane().getYaw() * MathHelper.DEGREES_PER_RADIAN))));
            ci.cancel();
        }
    }

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    public void changeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        Plane plane = twoDimensional$getPlane();
        if (plane != null) {
            this.prevPitch = this.getPitch();
            this.prevYaw = this.getYaw();

            MouseNormalizedGetter mouse = (MouseNormalizedGetter) MinecraftClient.getInstance().mouse;
            float pitch = (float) (MathHelper.atan2(-mouse.twoDimensional$getNormalizedY() * 0.60, Math.abs(mouse.twoDimensional$getNormalizedX())) * MathHelper.DEGREES_PER_RADIAN);
            this.setPitch(MathHelper.clamp(pitch, -90, 90));

            this.setYaw((float) MathHelper.lerp(MathHelper.clamp(7 * mouse.twoDimensional$getNormalizedX() + 0.5, 0, 1), plane.getYaw() * MathHelper.DEGREES_PER_RADIAN + 90, plane.getYaw() * MathHelper.DEGREES_PER_RADIAN + 270));

            if (this.vehicle != null) {
                this.vehicle.onPassengerLookAround((Entity) (Object) this);
            }

            ci.cancel();
        }
    }
}
