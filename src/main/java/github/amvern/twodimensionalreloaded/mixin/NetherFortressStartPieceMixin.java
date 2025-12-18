package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin to shift nether fortress start piece & children to align fortress to z = 0
 * */
@Mixin(NetherFortressPieces.StartPiece.class)
public abstract class NetherFortressStartPieceMixin extends NetherFortressPieces.BridgeCrossing {

    public NetherFortressStartPieceMixin(int i, BoundingBox boundingBox, Direction direction) {
        super(i, boundingBox, direction);
    }

    @Shadow @Final
    public List<StructurePiece> pendingChildren;

    @Inject(method = "<init>(Lnet/minecraft/util/RandomSource;II)V", at = @At("RETURN"))
    private void forceZToZero(RandomSource random, int x, int z, CallbackInfo ci) {
        int zLength = this.boundingBox.getZSpan();
        int centerZ = 0;
        int currentCenterZ = this.boundingBox.minZ() + zLength / 2;
        int shiftZ = centerZ - currentCenterZ;
        this.boundingBox.move(0, 0, shiftZ);

        for (StructurePiece piece : this.pendingChildren) {
            piece.getBoundingBox().move(0, 0, shiftZ);
        }
    }
}


