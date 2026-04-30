package github.amvern.twodimensionalreloaded.mixin.EntityAiGoals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PanicGoal.class)
public class PanicGoalMixin {

    @Redirect(
        method = "findRandomPosition",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/util/DefaultRandomPos;getPos(Lnet/minecraft/world/entity/PathfinderMob;II)Lnet/minecraft/world/phys/Vec3;"
        )
    )
    private Vec3 restrictPanicRandomPos(PathfinderMob mob, int xz, int y) {
        Vec3 original = DefaultRandomPos.getPos(mob, xz, y);
        if (original == null) return null;

        return new Vec3(original.x, original.y, mob.getZ());
    }
}