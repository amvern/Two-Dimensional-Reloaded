package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(StrongholdPieces.PortalRoom.class)
public abstract class StrongholdPortalRoomMixin {

    @ModifyArgs(
        method = "postProcess",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/structures/StrongholdPieces$PortalRoom;placeBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/block/state/BlockState;IIILnet/minecraft/world/level/levelgen/structure/BoundingBox;)V"
        )
    )
    private void setEndPortalEyes(Args args) {
        BlockState state = args.get(1);
        int x = args.get(2);
        Direction facing = state.is(Blocks.END_PORTAL_FRAME) ? state.getValue(EndPortalFrameBlock.FACING) : null;

        if (state.is(Blocks.END_PORTAL_FRAME) && facing != null) {
            switch (facing) {
                case EAST, WEST -> state = state.setValue(EndPortalFrameBlock.HAS_EYE, true);
                case NORTH, SOUTH -> {
                    if (x == 4) state = state.setValue(EndPortalFrameBlock.HAS_EYE, true);
                }
            }

            args.set(1, state);
        }
    }
}