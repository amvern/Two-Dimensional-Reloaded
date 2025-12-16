package github.mishkis.twodimensional.mixin;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to set start piece orientation along Plane
 */
@Mixin(StrongholdPieces.StartPiece.class)
public abstract class StartPieceMixin {

    @Inject(method = "<init>(Lnet/minecraft/util/RandomSource;II)V", at = @At("RETURN"))
    private void forceStrongholdXDirection(RandomSource random, int x, int z, CallbackInfo ci) {
        Direction facing = x >= 0 ? Direction.EAST : Direction.WEST;
        ((StructurePieceInvoker) this).callSetOrientation(facing);
    }
}






