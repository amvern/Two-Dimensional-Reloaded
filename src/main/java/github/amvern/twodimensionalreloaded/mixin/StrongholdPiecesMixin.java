package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StrongholdPieces.class)
public class StrongholdPiecesMixin {

    @ModifyArg(
        method = "findAndCreatePieceFactory",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/structures/StrongholdPieces$PortalRoom;createPiece(Lnet/minecraft/world/level/levelgen/structure/StructurePieceAccessor;IIILnet/minecraft/core/Direction;I)Lnet/minecraft/world/level/levelgen/structure/structures/StrongholdPieces$PortalRoom;"
        ),
        index = 4
    )
    private static Direction forceEndPortalRoomDirection(Direction direction) {
        return Direction.WEST;
    }

}