package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ AbstractHorse.class, AbstractNautilus.class, Strider.class, AbstractBoat.class, LivingEntity.class })
public class MultiTargetTickMixin {

    @Inject(method = "tick", at = @At("RETURN"))
    private void clampMovementZ(CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;

        Vec3 delta = entity.getDeltaMovement();
        entity.setDeltaMovement(delta.x, delta.y, 0.0);

        entity.setPos(entity.getX(), entity.getY(), Math.floor(entity.getZ()) + 0.5);
    }

}