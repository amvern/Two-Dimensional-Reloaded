package github.mishkis.twodimensional.mixin;

import github.mishkis.twodimensional.access.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;
    @Unique
    Vec3 TwoDimensional$intersectPoint;

//     this is kinda jank
 @Inject(method = "teleport(DDDFFLjava/util/Set;)V", at = @At("HEAD"))
    private void clampInput(double x, double y, double z, float yaw, float pitch, Set<RelativeMovement> flags, CallbackInfo ci) {
        Plane plane = ((EntityPlaneGetterSetter) this.player).twoDimensional$getPlane();
        if (plane != null) {
            TwoDimensional$intersectPoint = plane.intersectPoint(new Vec3(x, y, z));
        } else {
            TwoDimensional$intersectPoint = new Vec3(x, y, z);
        }
    }

    @ModifyVariable(method = "teleport(DDDFFLjava/util/Set;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double clampX(double x) {
        return TwoDimensional$intersectPoint.x;
    }

    @ModifyVariable(method = "teleport(DDDFFLjava/util/Set;)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private double clampZ(double Z) {
        return TwoDimensional$intersectPoint.z;
    }
}
