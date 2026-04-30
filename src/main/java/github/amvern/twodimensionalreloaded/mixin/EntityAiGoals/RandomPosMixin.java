package github.amvern.twodimensionalreloaded.mixin.EntityAiGoals;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.util.RandomPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RandomPos.class)
public class RandomPosMixin {

    @Inject(method = "generateRandomDirection", at = @At("RETURN"), cancellable = true)
    private static void clampRandomDirection(RandomSource random, int horizontalDist, int verticalDist, CallbackInfoReturnable<BlockPos> cir) {
        BlockPos original = cir.getReturnValue();
        if (original == null) return;

        cir.setReturnValue(new BlockPos(
            original.getX(),
            original.getY(),
            (int) Plane.getZ()));
    }

    @Inject(method = "generateRandomDirectionWithinRadians", at = @At("RETURN"), cancellable = true)
    private static void clampDirection(
        RandomSource random,
        double minHorizontalDist,
        double maxHorizontalDist,
        int verticalDist,
        int flyingHeight,
        double xDir,
        double zDir,
        double maxXzRadiansFromDir,
        CallbackInfoReturnable<BlockPos> cir
    ) {

        BlockPos pos = cir.getReturnValue();
        if (pos == null) return;

        cir.setReturnValue(new BlockPos(
            pos.getX(),
            pos.getY(),
            (int) Plane.getZ()
        ));
    }
}