package github.mishkis.twodimensional.mixin;

import com.mojang.authlib.GameProfile;
import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.access.EntityPlaneGetterSetter;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Shadow public abstract ServerLevel serverLevel();

    public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    private void copyPlane(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        Plane plane = ((EntityPlaneGetterSetter) oldPlayer).twoDimensional$getPlane();
        ((EntityPlaneGetterSetter) this).twoDimensional$setPlane(plane);

        if (plane != null) {
            // sync to client
            TwoDimensional.updatePlane(this.getServer(), (ServerPlayer) (Player) this, plane.getOffset().x, plane.getOffset().z, plane.getYaw());
        }
    }

    @ModifyArgs(
            method = "fudgeSpawnLocation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/PlayerRespawnLogic;getOverworldRespawnPos(Lnet/minecraft/server/level/ServerLevel;II)Lnet/minecraft/core/BlockPos;"
            )
    )
    private void clampSpawnXZ(Args args) {
        Plane plane = ((EntityPlaneGetterSetter) this).twoDimensional$getPlane();
        if (plane != null) {
            int x = args.get(1);
            int z = args.get(2);
            Vec3 intersectPoint = plane.intersectPoint(new Vec3(x, 0, z));
            args.set(1, (int) intersectPoint.x);
            args.set(2, (int) intersectPoint.z);
        }
    }

}
