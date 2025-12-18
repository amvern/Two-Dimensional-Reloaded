package github.amvern.twodimensionalreloaded.client.mixin;

import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.client.access.MouseNormalizedGetter;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private @Nullable Entity vehicle;
    @Shadow public float xRotO;

    @Shadow public abstract float getXRot();

    @Shadow public float yRotO;

    @Shadow public abstract float getYRot();

    @Shadow public abstract void setXRot(float pitch);

    @Shadow public abstract void setYRot(float yaw);

    @Shadow public abstract BlockPos blockPosition();

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void turn(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        Plane plane = TwoDimensionalReloadedClient.plane;
        if (plane != null) {
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();

            MouseNormalizedGetter mouse = (MouseNormalizedGetter) Minecraft.getInstance().mouseHandler;
            float pitch = (float) (Mth.atan2(-mouse.twoDimensional$getNormalizedY() * 0.60, Math.abs(mouse.twoDimensional$getNormalizedX())) * Mth.RAD_TO_DEG);
            this.setXRot(Mth.clamp(pitch, -90, 90));

            double base = 0;
            if (TwoDimensionalReloadedClient.faceAway.isDown()) {
                this.setYRot((float) Mth.lerp(Mth.clamp(3. * mouse.twoDimensional$getNormalizedX() + 0.5, 0, 1), base + 90, base - 90));
            } else {
                this.setYRot((float) Mth.lerp(Mth.clamp(7 * mouse.twoDimensional$getNormalizedX() + 0.5, 0, 1), base + 90, base + 270));
            }

            if (this.vehicle != null) {
                this.vehicle.onPassengerTurned((Entity) (Object) this);
            }

            ci.cancel();
        }
    }

    @Inject(method = "shouldRender(DDD)Z", at = @At("HEAD"), cancellable = true)
    public void disableRenderingOutsidePlane(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!Plane.shouldCull(this.blockPosition(), TwoDimensionalReloadedClient.plane));
    }
}
