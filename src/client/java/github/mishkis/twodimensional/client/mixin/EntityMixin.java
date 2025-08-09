package github.mishkis.twodimensional.client.mixin;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityPlaneGetterSetter {
    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract Vec3d getVelocity();

    @Shadow private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) { return null; };

    @Shadow public abstract World getWorld();

    @Unique
    private Plane plane = null;

    @Override
    public Plane twoDimensional$getPlane() {
        if (this.getWorld().isClient) {
            return TwoDimensionalClient.plane;
        }

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
}
