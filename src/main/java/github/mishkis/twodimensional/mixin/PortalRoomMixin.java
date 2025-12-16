package github.mishkis.twodimensional.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to shift portal room so it gets centered on Plane
 * */
@Mixin(StrongholdPieces.PortalRoom.class)
public class PortalRoomMixin {

    @Inject(
            method = "<init>(ILnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/core/Direction;)V",
            at = @At("RETURN")
    )
    private void centerPortalRoomZ0(int i, BoundingBox box, Direction direction, CallbackInfo ci) {
        int roomZLength = box.getZSpan();
        int centerZ = 0;
        int currentCenterZ = box.minZ() + roomZLength / 2;
        int shiftZ = centerZ - currentCenterZ;
        box.move(0, 0, shiftZ);
    }
}





