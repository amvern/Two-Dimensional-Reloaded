package github.mishkis.twodimensional.mixin;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Mixin to force nether portals to spawn on Z = 1
 * */
@Mixin(PortalForcer.class)
public class PortalForcerMixin {

    @Redirect(method = "createPortal", at = @At(value = "INVOKE",target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"))
    private BlockPos.MutableBlockPos forcePortalZ(BlockPos.MutableBlockPos mutable, Vec3i origin, int x, int y, int z) {
        BlockPos lockedOrigin = new BlockPos(origin.getX(), origin.getY(), 1);
        return mutable.setWithOffset(lockedOrigin, x, y, z);
    }

    @Inject(method = "createPortal", at = @At("RETURN"))
    private void preparePortalSpawnArea(BlockPos blockPos, Direction.Axis axis, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir) {
        Optional<BlockUtil.FoundRectangle> optRect = cir.getReturnValue();
        if (optRect.isEmpty()) return;

        BlockUtil.FoundRectangle rect = optRect.get();
        ServerLevel level = ((PortalForcerAccessor) this).getLevel();

        ResourceKey<Level> dimension = level.dimension();

//        if(dimension == Level.NETHER) {
//            for(int x = 0; x < 6; x++) {
//                for(int y = 0; y < 7; y++) {
//                    level.setBlockAndUpdate(new BlockPos((rect.minCorner.getX() + 3) - x, ((rect.minCorner.getY() - 2) + y), 0),Blocks.NETHERRACK.defaultBlockState());
//                }
//            }
//        }

        for(int x = 0; x < 4; x++) {
            for(int y = 0; y < 5; y++) {
                level.setBlockAndUpdate(new BlockPos((rect.minCorner.getX() + 2) - x, ((rect.minCorner.getY() - 1) + y), 0),Blocks.AIR.defaultBlockState());
            }
        }

        for(int x = 0; x < 4; x++) {
            BlockState blockState = (dimension == Level.NETHER) ? Blocks.NETHERRACK.defaultBlockState() : Blocks.DIRT.defaultBlockState();
            level.setBlockAndUpdate(new BlockPos((rect.minCorner.getX() + 2) - x, rect.minCorner.getY() - 2, 0), blockState);
        }
    }
}


