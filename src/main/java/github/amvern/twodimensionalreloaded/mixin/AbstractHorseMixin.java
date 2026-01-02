package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents executeRidersJump from applying movement to the Z coordinate.
 * Fixes issues when facing a culled region and jumping toward the camera.
 * */
@Mixin({ AbstractHorse.class, Camel.class })
public abstract class AbstractHorseMixin {

    @Inject(method = "executeRidersJump", at = @At("RETURN"))
    private void clampRidersJumpZ(float f, Vec3 vec3, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        Vec3 delta = entity.getDeltaMovement();
        entity.setDeltaMovement(delta.x, delta.y, 0.0);
    }

}