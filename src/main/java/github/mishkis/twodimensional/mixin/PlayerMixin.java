package github.mishkis.twodimensional.mixin;

import github.mishkis.twodimensional.access.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "blockActionRestricted", at = @At("HEAD"), cancellable = true)
    private void disableBlockBreakingOutsidePlane(Level world, BlockPos pos, GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        Plane plane = ((EntityPlaneGetterSetter) this).twoDimensional$getPlane();
        if (plane != null) {
            double dist = plane.sdf(pos.getCenter());
            if (dist <= Plane.CULL_DIST || dist >= 1.8) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "tick", at =  @At("HEAD"))
    private void updatePlaneContainedEntities(CallbackInfo ci) {
        Plane plane = ((EntityPlaneGetterSetter) this).twoDimensional$getPlane();
        if (plane != null && this.tickCount % 20 == 0 && this.level() instanceof ServerLevel world) {
            world.getEntities(this, AABB.ofSize(position(), 64, 32, 64)).forEach(entity -> {
                EntityPlaneGetterSetter entityPlane = (EntityPlaneGetterSetter) entity;
                if (entityPlane.twoDimensional$getPlane() == null && !(entity instanceof Player)) {
                    entityPlane.twoDimensional$setPlane(plane);
                    plane.containedEntities.add(entity);
                }
            });
        }
    }
}
