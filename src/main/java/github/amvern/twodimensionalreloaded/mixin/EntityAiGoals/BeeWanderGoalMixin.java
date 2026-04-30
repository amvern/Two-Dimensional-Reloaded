package github.amvern.twodimensionalreloaded.mixin.EntityAiGoals;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Bee.BeeWanderGoal.class)
public class BeeWanderGoalMixin {
    @Shadow @Final Bee this$0;

    @ModifyReturnValue(method = "findPos", at = @At("RETURN"))
    private Vec3 clampFindPosZ(Vec3 original) {
        if (original == null) return null;

        Bee bee = this$0;

        return new Vec3(original.x, original.y, bee.getZ()
        );
    }

    @Redirect(
        method = "start",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;createPath(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/pathfinder/Path;"
        )
    )
    private Path redirectPath(PathNavigation nav, BlockPos pos, int accuracy) {
        Bee bee = this$0;

        BlockPos fixed = new BlockPos(pos.getX(), pos.getY(), bee.blockPosition().getZ());

        return nav.createPath(fixed, accuracy);
    }
}