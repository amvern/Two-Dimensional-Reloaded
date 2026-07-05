package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "hasLineOfSight(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;D)Z", at = @At("HEAD"), cancellable = true)
    public void hasLineOfSightSameZ(Entity target, ClipContext.Block blockCollidingContext, ClipContext.Fluid fluidCollidingContext, double eyeHeight, CallbackInfoReturnable<Boolean> cir) {
        if(target.level() != ((LivingEntity)(Object)this).level()) {
            cir.setReturnValue(false);
        }

        if(((LivingEntity)(Object)this) instanceof EnderDragon) return;

        if(target.blockPosition().getZ() != ((LivingEntity)(Object)this).blockPosition().getZ()) {
            cir.setReturnValue(false);
        }
    }
}