package github.amvern.twodimensionalreloaded.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.SkyLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SkyLightEngine.class)
public abstract class SkyLightEngineMixin {
    @Shadow @Final private BlockPos.MutableBlockPos mutablePos;

    @WrapOperation(
        method = "propagateIncrease",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/lighting/SkyLightEngine;getOpacity(Lnet/minecraft/world/level/block/state/BlockState;)I"
        )
    )
    private int twodimensionalreloaded$skyLightGetOpacity(
        SkyLightEngine instance, BlockState blockState, Operation<Integer> original, @Local(name = "toNode") long toNode
    ) {
        if (Plane.shouldCull(this.mutablePos)) {
            return 1;
        }

        return original.call(instance, blockState);
    }
}